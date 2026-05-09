import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tasks.SearchEvent
import tasks.SearchState
import tasks.SearchViewModelImpl

class Task3Test {

    @Test
    fun `initial state is empty`() {
        val vm = SearchViewModelImpl()
        assertEquals(SearchState(), vm.state.value)
    }

    @Test
    fun `onQueryChanged sets loading state then success`() = runTest {
        val vm = SearchViewModelImpl()
        vm.onQueryChanged("kotlin") { listOf("Kotlin", "KotlinX") }
        assertEquals(SearchState(
            query = "kotlin",
            isLoading = false,
            results = listOf("Kotlin", "KotlinX"),
            error = null,
        ), vm.state.value)
    }

    @Test
    fun `onQueryChanged with empty query clears results without calling search`() = runTest {
        val vm = SearchViewModelImpl()
        vm.onQueryChanged("kotlin") { listOf("Kotlin") }
        var searchCalled = false
        vm.onQueryChanged("") { searchCalled = true; emptyList() }

        assertFalse(searchCalled)
        assertEquals(SearchState(query = "", isLoading = false, results = emptyList()), vm.state.value)
    }

    @Test
    fun `onQueryChanged on error sets error state and emits ShowError event`() = runTest {
        val vm = SearchViewModelImpl()
        vm.onQueryChanged("bad") { error("Network error") }

        assertEquals("Network error", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
        assertEquals(emptyList<String>(), vm.state.value.results)

        val errorEvent = vm.events.replayCache.filterIsInstance<SearchEvent.ShowError>().firstOrNull()
        assertNotNull(errorEvent)
        assertEquals("Network error", errorEvent!!.message)
    }

    @Test
    fun `onRetry repeats search with current query`() = runTest {
        val vm = SearchViewModelImpl()
        vm.onQueryChanged("kotlin") { error("fail") }
        vm.onRetry { listOf("Kotlin Coroutines") }

        assertEquals(listOf("Kotlin Coroutines"), vm.state.value.results)
        assertNull(vm.state.value.error)
    }

    @Test
    fun `onRetry does nothing when query is empty`() = runTest {
        val vm = SearchViewModelImpl()
        var searchCalled = false
        vm.onRetry { searchCalled = true; emptyList() }

        assertFalse(searchCalled)
    }

    @Test
    fun `onClearClicked resets state and emits ClearQuery event`() = runTest {
        val vm = SearchViewModelImpl()
        vm.onQueryChanged("kotlin") { listOf("Kotlin") }
        vm.onClearClicked()

        assertEquals(SearchState(), vm.state.value)
        assertTrue(vm.events.replayCache.any { it is SearchEvent.ClearQuery })
    }

    @Test
    fun `isLoading is false after successful search`() = runTest {
        val vm = SearchViewModelImpl()
        vm.onQueryChanged("test") { listOf("result") }
        assertFalse(vm.state.value.isLoading)
    }
}
