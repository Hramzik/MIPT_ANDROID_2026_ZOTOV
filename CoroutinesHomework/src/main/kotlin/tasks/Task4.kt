package tasks

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Задание 4: Дедуплицирующий загрузчик (Deduplicating Loader)
 *
 * Этот паттерн используется в Coil, Glide и других библиотеках:
 * если несколько корутин одновременно запрашивают один и тот же ключ,
 * реальная загрузка должна происходить только один раз.
 *
 * Требования к поведению [load]:
 * - Если 10 корутин одновременно вызывают load("same-key"),
 *   [loader] должен быть вызван ровно 1 раз, но все 10 корутин
 *   получат результат.
 * - Разные ключи не влияют друг на друга — грузятся независимо.
 * - После того как загрузка завершилась, следующий вызов с тем же
 *   ключом снова запускает [loader].
 *
 * Запрещено использовать @Synchronized — только корутинные примитивы синхронизации.
 */
interface DeduplicatingLoader<K, V> {
    suspend fun load(key: K, loader: suspend (K) -> V): V
}

// TODO: реализуйте этот класс
class DeduplicatingLoaderImpl<K, V> : DeduplicatingLoader<K, V> {
    private val keyToDeferred = mutableMapOf<K, CompletableDeferred<V>>()
    private val keyToDeferredMutex = Mutex()

    override suspend fun load(key: K, loader: suspend (K) -> V): V {
        val hasDeferredAlreadyExisted: Boolean = keyToDeferredMutex.withLock {
            keyToDeferred.containsKey(key).also {
                if (!it) {
                    createDeferred(key)
                }
            }
        }

        if (hasDeferredAlreadyExisted) {
            return waitForExistingDeferred(key)
        }
        return completeDeferred(key, loader)

    }

    private suspend fun waitForExistingDeferred(key: K): V {
        val deferred = keyToDeferredMutex.withLock {
            keyToDeferred[key]!!
        }
        return deferred.await()
    }

    private suspend fun createDeferred(key: K) {
        keyToDeferred[key] = CompletableDeferred()
    }

    private suspend fun completeDeferred(key: K, loader: suspend (K) -> V): V {
        val deferred = keyToDeferred[key]!!

        try {
            val deferredResult = loader(key)
            deferred.complete(deferredResult)
            return deferredResult
        }
        catch (e: Throwable) {
            deferred.completeExceptionally(e)
            throw e
        }
        finally {
            keyToDeferredMutex.withLock {
                keyToDeferred.remove(key)
            }
        }
    }
}
