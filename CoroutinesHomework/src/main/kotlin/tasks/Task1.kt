package tasks

import kotlinx.coroutines.delay

/**
 * Задание 1: Exponential Backoff
 *
 * Реализуйте suspend-функцию [retryWithBackoff], которая выполняет [block]
 * и при неудаче повторяет попытку с экспоненциально растущей задержкой.
 * Паттерн широко используется в сетевых клиентах для устойчивости к временным сбоям.
 *
 * Требования к поведению:
 * - При успехе [block] возвращает результат немедленно, без задержек.
 * - При ошибке функция повторяет попытку, выдерживая паузу перед каждым
 *   следующим вызовом. Пауза растёт экспоненциально от [baseDelayMs].
 * - После исчерпания [maxAttempts] пробрасывается исключение последней попытки.
 * - [maxAttempts] — это общее число вызовов [block], а не число повторов.
 * - Запрещено использовать Thread.sleep — только корутинные задержки.
 */
suspend fun <T> retryWithBackoff(
    maxAttempts: Int,
    baseDelayMs: Long,
    block: suspend (attempt: Int) -> T,
): T {
    var lastTryException: Throwable? = null

    for (attemptIndex in 0 until maxAttempts) {
        try {
            return block(attemptIndex)
        }
        catch (e: Throwable) {
            lastTryException = e
            if (attemptIndex == maxAttempts - 1) break
            val delayMs = baseDelayMs * (1L shl attemptIndex)
            delay(delayMs)
        }
    }

    throw lastTryException ?: IllegalStateException("No attempts were made")
}
