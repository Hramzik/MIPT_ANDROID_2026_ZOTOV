import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tasks.DeduplicatingLoaderImpl
import java.util.concurrent.atomic.AtomicInteger

class Task4Test {

    @Test
    fun `load returns correct value`() = runTest {
        val loader = DeduplicatingLoaderImpl<String, String>()
        val result = loader.load("key") { k -> "value-for-$k" }
        assertEquals("value-for-key", result)
    }

    @Test
    fun `different keys each call loader once`() = runTest {
        val loader = DeduplicatingLoaderImpl<String, Int>()
        val calls = mutableMapOf<String, Int>()

        loader.load("a") { calls["a"] = (calls["a"] ?: 0) + 1; 1 }
        loader.load("b") { calls["b"] = (calls["b"] ?: 0) + 1; 2 }

        assertEquals(1, calls["a"])
        assertEquals(1, calls["b"])
    }

    @Test
    fun `concurrent requests for same key call loader exactly once`() = runTest {
        val loader = DeduplicatingLoaderImpl<String, String>()
        val callCount = AtomicInteger(0)

        val jobs = List(10) {
            launch(Dispatchers.Default) {
                loader.load("shared-key") { k ->
                    callCount.incrementAndGet()
                    delay(50)
                    "result-$k"
                }
            }
        }
        jobs.forEach { it.join() }

        assertEquals(1, callCount.get(),
            "loader should be called exactly once, but was called ${callCount.get()} times")
    }

    @Test
    fun `all concurrent coroutines receive same result`() = runTest {
        val loader = DeduplicatingLoaderImpl<String, String>()
        val results = mutableListOf<String>()

        val jobs = List(5) {
            launch(Dispatchers.Default) {
                val r = loader.load("key") { delay(30); "the-result" }
                synchronized(results) { results += r }
            }
        }
        jobs.forEach { it.join() }

        assertTrue(results.all { it == "the-result" },
            "All coroutines should get 'the-result', got: $results")
        assertEquals(5, results.size)
    }

    @Test
    fun `after load completes, next load can start fresh`() = runTest {
        val loader = DeduplicatingLoaderImpl<String, Int>()
        val callCount = AtomicInteger(0)

        loader.load("key") { callCount.incrementAndGet(); 1 }
        loader.load("key") { callCount.incrementAndGet(); 2 }

        assertEquals(2, callCount.get())
    }

    @Test
    fun `concurrent requests for different keys are both called`() = runTest {
        val loader = DeduplicatingLoaderImpl<String, String>()
        val callCount = AtomicInteger(0)

        val j1 = launch(Dispatchers.Default) {
            loader.load("key1") { callCount.incrementAndGet(); delay(50); "r1" }
        }
        val j2 = launch(Dispatchers.Default) {
            loader.load("key2") { callCount.incrementAndGet(); delay(50); "r2" }
        }
        j1.join(); j2.join()

        assertEquals(2, callCount.get())
    }
}
