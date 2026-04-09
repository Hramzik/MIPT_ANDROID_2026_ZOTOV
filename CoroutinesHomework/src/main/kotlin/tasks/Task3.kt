package tasks

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Задание 3: ViewModel-подобный компонент (StateFlow + SharedFlow)
 *
 * Реализуйте класс [SearchViewModel] — упрощённый аналог ViewModel
 * из Android Architecture Components. Компонент управляет состоянием
 * экрана поиска через [state] (текущее состояние UI) и [events]
 * (одноразовые события вроде навигации или показа снэкбара).
 *
 * Требования к поведению:
 * - [state] всегда отражает актуальное состояние UI, включая флаг загрузки и ошибку.
 * - Пустой запрос не вызывает [search] и сбрасывает результаты.
 * - Ошибка в [search] не крашит компонент: состояние переходит в ошибочное,
 *   а в [events] публикуется соответствующее событие.
 * - [onRetry] повторяет поиск с последним непустым запросом.
 * - [onClearClicked] полностью сбрасывает состояние и публикует событие в [events].
 */
data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<String> = emptyList(),
    val error: String? = null,
)

sealed class SearchEvent {
    data class ShowError(val message: String) : SearchEvent()
    data object ClearQuery : SearchEvent()
}

interface SearchViewModel {
    val state: StateFlow<SearchState>
    val events: MutableSharedFlow<SearchEvent>

    suspend fun onQueryChanged(query: String, search: suspend (String) -> List<String>)
    suspend fun onRetry(search: suspend (String) -> List<String>)
    suspend fun onClearClicked()
}

// TODO: реализуйте этот класс
class SearchViewModelImpl : SearchViewModel {
    override val state: MutableStateFlow<SearchState> = MutableStateFlow(SearchState())
    override val events: MutableSharedFlow<SearchEvent> = MutableSharedFlow(replay = 10)

    override suspend fun onQueryChanged(query: String, search: suspend (String) -> List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun onRetry(search: suspend (String) -> List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun onClearClicked() {
        TODO("Not yet implemented")
    }
}
