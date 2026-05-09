import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tasks.DataRepositoryImpl

class Task2Test {

    private val repo = DataRepositoryImpl()

    @Test
    fun `returns results in input order`() = runTest {
        val io = StandardTestDispatcher(testScheduler)
        val cpu = StandardTestDispatcher(testScheduler)

        val result = repo.fetchAndParseAll(
            ids = listOf(1, 2, 3),
            ioDispatcher = io,
            cpuDispatcher = cpu,
            fetch = { id -> "raw-$id" },
            parse = { raw -> "$raw:parsed" },
        )
        assertEquals(listOf("raw-1:parsed", "raw-2:parsed", "raw-3:parsed"), result)
    }

    @Test
    fun `empty list returns empty result`() = runTest {
        val io = StandardTestDispatcher(testScheduler)
        val cpu = StandardTestDispatcher(testScheduler)

        val result = repo.fetchAndParseAll(emptyList(), io, cpu,
            fetch = { "" }, parse = { it })
        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `fetch runs on ioDispatcher`() = runTest {
        val io = StandardTestDispatcher(testScheduler)
        val cpu = StandardTestDispatcher(testScheduler)
        val wrongDispatcher = mutableListOf<Int>()

        repo.fetchAndParseAll(
            ids = listOf(1, 2, 3),
            ioDispatcher = io,
            cpuDispatcher = cpu,
            fetch = { id ->
                val actual = coroutineContext[ContinuationInterceptor] as? CoroutineDispatcher
                if (actual !== io) wrongDispatcher += id
                "raw-$id"
            },
            parse = { it },
        )
        assertEquals(emptyList<Int>(), wrongDispatcher,
            "fetch for ids $wrongDispatcher did not run on ioDispatcher")
    }

    @Test
    fun `parse runs on cpuDispatcher`() = runTest {
        val io = StandardTestDispatcher(testScheduler)
        val cpu = StandardTestDispatcher(testScheduler)
        val wrongDispatcher = mutableListOf<String>()

        repo.fetchAndParseAll(
            ids = listOf(1, 2, 3),
            ioDispatcher = io,
            cpuDispatcher = cpu,
            fetch = { "raw-$it" },
            parse = { raw ->
                val actual = coroutineContext[ContinuationInterceptor] as? CoroutineDispatcher
                if (actual !== cpu) wrongDispatcher += raw
                "$raw:parsed"
            },
        )
        assertEquals(emptyList<String>(), wrongDispatcher,
            "parse for $wrongDispatcher did not run on cpuDispatcher")
    }

    @Test
    fun `all ids are processed in parallel, not sequentially`() = runTest {
        val io = StandardTestDispatcher(testScheduler)
        val cpu = StandardTestDispatcher(testScheduler)

        repo.fetchAndParseAll(
            ids = List(5) { it },
            ioDispatcher = io,
            cpuDispatcher = cpu,
            fetch = { delay(200); "raw-$it" },
            parse = { delay(100); "$it:parsed" },
        )
        assertEquals(300L, currentTime,
            "Expected parallel execution (300ms), but took ${currentTime}ms — looks sequential")
    }

    @Test
    fun `parse receives output of fetch`() = runTest {
        val io = StandardTestDispatcher(testScheduler)
        val cpu = StandardTestDispatcher(testScheduler)
        val parseInputs = mutableListOf<String>()

        repo.fetchAndParseAll(
            ids = listOf(42),
            ioDispatcher = io,
            cpuDispatcher = cpu,
            fetch = { "fetched-$it" },
            parse = { raw -> parseInputs += raw; "done" },
        )
        assertEquals(listOf("fetched-42"), parseInputs)
    }
}
