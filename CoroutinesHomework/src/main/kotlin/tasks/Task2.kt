package tasks

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Задание 2: Параллельная загрузка с переключением потоков
 *
 * Реализуйте метод [fetchAndParseAll] в классе [DataRepository].
 * Метод загружает и обрабатывает список элементов по их id: сначала
 * выполняет [fetch] (имитация сетевого запроса), затем [parse]
 * (имитация CPU-обработки). Оба шага должны выполняться на правильных
 * диспетчерах, а все элементы — обрабатываться параллельно.
 *
 * Требования к поведению:
 * - [fetch] для каждого id должен выполняться на [ioDispatcher].
 * - [parse] для каждого id должен выполняться на [cpuDispatcher].
 * - [parse] получает на вход результат [fetch] для того же id.
 * - Все id обрабатываются параллельно: время выполнения не должно
 *   расти линейно с числом элементов.
 * - Результаты возвращаются в том же порядке, что и входной список.
 */
interface DataRepository {
    suspend fun fetchAndParseAll(
        ids: List<Int>,
        ioDispatcher: CoroutineDispatcher,
        cpuDispatcher: CoroutineDispatcher,
        fetch: suspend (Int) -> String,
        parse: suspend (String) -> String,
    ): List<String>
}

// TODO: реализуйте этот класс
class DataRepositoryImpl : DataRepository {
    override suspend fun fetchAndParseAll(
        ids: List<Int>,
        ioDispatcher: CoroutineDispatcher,
        cpuDispatcher: CoroutineDispatcher,
        fetch: suspend (Int) -> String,
        parse: suspend (String) -> String,
    ): List<String> {
        TODO("Not yet implemented")
    }
}
