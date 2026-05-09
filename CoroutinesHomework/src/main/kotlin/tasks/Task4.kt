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
        val (deferred, amITheOwner) = keyToDeferredMutex.withLock {
            val existingDeffered = keyToDeferred[key]
            if (existingDeffered != null) {
                existingDeffered to false
            } else {
                val newDeffered = CompletableDeferred<V>()
                keyToDeferred[key] = newDeffered
                newDeffered to true
            }
        }

        if (!amITheOwner) {
            return deferred.await()
        }

        try {
            val result = loader(key)
            deferred.complete(result)
            return result
        } catch (e: Throwable) {
            deferred.completeExceptionally(e)
            throw e
        } finally {
            keyToDeferredMutex.withLock {
                keyToDeferred.remove(key)
            }
        }
    }
}
