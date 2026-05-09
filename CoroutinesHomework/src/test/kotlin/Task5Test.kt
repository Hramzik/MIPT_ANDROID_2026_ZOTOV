import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tasks.SearchPipelineImpl
import tasks.SearchResult

class Task5Test {

    private val pipeline = SearchPipelineImpl()

    @Test
    fun `filters queries shorter than minLength`() = runTest {
        val results = pipeline.build(
            queries = flowOf("", "a", "ab", "abc"),
            minLength = 3,
            debounceMs = 0,
            search = { q -> listOf(q) },
        ).toList()

        assertFalse(results.any { it is SearchResult.Success && it.items == listOf("") })
        assertFalse(results.any { it is SearchResult.Success && it.items == listOf("a") })
        assertTrue(results.any { it is SearchResult.Success && (it as SearchResult.Success).items == listOf("abc") })
    }

    @Test
    fun `emits Loading before Success`() = runTest {
        val results = pipeline.build(
            queries = flowOf("kotlin"),
            minLength = 1,
            debounceMs = 0,
            search = { listOf("Kotlin Coroutines") },
        ).toList()

        val loadingIdx = results.indexOfFirst { it is SearchResult.Loading }
        val successIdx = results.indexOfFirst { it is SearchResult.Success }
        assertTrue(loadingIdx >= 0, "Should emit Loading")
        assertTrue(successIdx > loadingIdx, "Loading should come before Success")
    }

    @Test
    fun `returns correct results on success`() = runTest {
        val results = pipeline.build(
            queries = flowOf("test"),
            minLength = 1,
            debounceMs = 0,
            search = { listOf("result-1", "result-2") },
        ).toList()

        val success = results.filterIsInstance<SearchResult.Success>()
        assertEquals(1, success.size)
        assertEquals(listOf("result-1", "result-2"), success.first().items)
    }

    @Test
    fun `emits Error and continues stream on search failure`() = runTest {
        val results = pipeline.build(
            queries = flowOf("bad", "good"),
            minLength = 1,
            debounceMs = 0,
            search = { q ->
                if (q == "bad") error("Network error") else listOf("ok")
            },
        ).toList()

        val errors = results.filterIsInstance<SearchResult.Error>()
        val successes = results.filterIsInstance<SearchResult.Success>()

        assertTrue(errors.isNotEmpty(), "Should have at least one Error")
        assertEquals("Network error", errors.first().message)
        assertTrue(successes.any { it.items == listOf("ok") }, "Should still get success after error")
    }

    @Test
    fun `distinctUntilChanged skips duplicate queries`() = runTest {
        var searchCalls = 0
        pipeline.build(
            queries = flow {
                emit("kotlin"); emit("kotlin"); emit("kotlin")
            },
            minLength = 1,
            debounceMs = 0,
            search = { searchCalls++; listOf("r") },
        ).toList()

        assertEquals(1, searchCalls, "Should call search only once for duplicate queries")
    }

    @Test
    fun `debounce cancels intermediate queries`() = runTest {
        var searchCalls = 0
        pipeline.build(
            queries = flow {
                emit("k")
                delay(10)
                emit("ko")
                delay(10)
                emit("kot")
                delay(200)
            },
            minLength = 1,
            debounceMs = 100,
            search = { q -> searchCalls++; listOf(q) },
        ).toList()

        assertEquals(1, searchCalls, "Debounce should collapse rapid queries into one")
    }

    @Test
    fun `flatMapLatest cancels slow previous search on new query`() = runTest {
        var firstSearchCompleted = false
        pipeline.build(
            queries = flow {
                emit("first")
                delay(50)
                emit("second")
            },
            minLength = 1,
            debounceMs = 0,
            search = { q ->
                if (q == "first") {
                    delay(500)
                    firstSearchCompleted = true
                }
                listOf("$q-result")
            },
        ).toList()

        assertFalse(firstSearchCompleted, "First search should have been cancelled by flatMapLatest")
    }
}
