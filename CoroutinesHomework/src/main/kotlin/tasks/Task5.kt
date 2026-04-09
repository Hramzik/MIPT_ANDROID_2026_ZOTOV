package tasks

import kotlinx.coroutines.flow.Flow

/**
 * Задание 5: Реактивный поисковый пайплайн
 *
 * Реализуйте метод [build], который превращает поток пользовательского ввода
 * в поток результатов поиска. Это типичный пайплайн для строки поиска в
 * Android-приложении: не каждый символ вызывает запрос, а лишние запросы
 * отменяются при появлении новых.
 *
 * Требования к поведению:
 * - Запросы короче [minLength] символов игнорируются.
 * - Если пользователь быстро вводит текст, поиск запускается только
 *   после паузы в [debounceMs] мс — промежуточные запросы отбрасываются.
 * - Одинаковые запросы подряд не вызывают повторный поиск.
 * - Если приходит новый запрос, предыдущий поиск отменяется, даже если
 *   ещё не завершился.
 * - Перед каждым результатом поиска эмитируется [SearchResult.Loading].
 * - Если [search] выбросил исключение — эмитируется [SearchResult.Error]
 *   с сообщением, и поток продолжает обрабатывать следующие запросы.
 */
sealed class SearchResult {
    data object Loading : SearchResult()
    data class Success(val items: List<String>) : SearchResult()
    data class Error(val message: String) : SearchResult()
}

interface SearchPipeline {
    fun build(
        queries: Flow<String>,
        minLength: Int,
        debounceMs: Long,
        search: suspend (String) -> List<String>,
    ): Flow<SearchResult>
}

// TODO: реализуйте этот класс
class SearchPipelineImpl : SearchPipeline {
    override fun build(
        queries: Flow<String>,
        minLength: Int,
        debounceMs: Long,
        search: suspend (String) -> List<String>,
    ): Flow<SearchResult> {
        TODO("Not yet implemented")
    }
}
