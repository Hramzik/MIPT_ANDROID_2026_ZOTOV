package com.example.messenger.network

import kotlin.math.min
import kotlinx.coroutines.delay
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.isActive

object RetryExecutor {
    suspend fun <T> executeWithRetry(call: suspend () -> retrofit2.Response<T>): retrofit2.Response<T> {
        var delayMs = 1000L
        val maxDelay = 30_000L
        val factor = 2.0
        while (coroutineContext.isActive) {
            val resp = try {
                call()
            } catch (e: Exception) {
                delay(delayMs)
                delayMs = min(maxDelay, (delayMs * factor).toLong())
                continue
            }

            if (resp.isSuccessful) return resp

            if (resp.code() == 429) {
                val retryAfterSec = resp.headers()["Retry-After"]?.toLongOrNull()
                val wait = if (retryAfterSec != null) retryAfterSec * 1000L else delayMs
                android.util.Log.d("Messenger", "Received 429, waiting ${wait}ms before retry")
                delay(wait)
                delayMs = min(maxDelay, (delayMs * factor).toLong())
                continue
            }

            return resp
        }

        throw kotlinx.coroutines.CancellationException("RetryExecutor cancelled")
    }
}
