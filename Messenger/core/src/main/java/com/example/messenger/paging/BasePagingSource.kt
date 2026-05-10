package com.example.messenger.paging

import androidx.paging.PagingSource
import retrofit2.Response
import com.example.messenger.network.RetryExecutor

abstract class BasePagingSource<Value : Any> : PagingSource<Int, Value>() {
    protected suspend fun <T> callWithRetry(call: suspend () -> Response<T>): Response<T> {
        return RetryExecutor.executeWithRetry(call)
    }
}
