package com.example.messenger.messages

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import android.util.Log
import com.example.messenger.network.ApiService
import com.example.messenger.network.Message

class MessagesPagingSource(
    private val api: ApiService,
    private val chatId: Int,
    private val pageSize: Int
) : BasePagingSource<Message>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val offset = params.key ?: 0
        val limit = params.loadSize.coerceAtMost(pageSize)
        android.util.Log.d("Messenger", "Loading: chat = $chatId, offset=$offset, loadSize=${params.loadSize}, limit=$limit")
        return try {
            val resp = callWithRetry { api.getChat(chatId, limit = limit, offset = offset) }
            android.util.Log.d("Messenger", "Result for chat = $chatId, offset=$offset limit=$limit -> code=${resp.code()}")
            if (resp.isSuccessful) {
                val body = resp.body()
                val items = body?.messages ?: emptyList()

                val prevKey = if (offset != 0) (offset - pageSize).coerceAtLeast(0) else null
                val nextKey = if (items.size >= limit) offset + pageSize else null
                LoadResult.Page(data = items, prevKey = prevKey, nextKey = nextKey)
            } else {
                LoadResult.Error(Exception("HTTP ${resp.code()} ${resp.message()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("Messenger", "Load exception for chat = $chatId, offset = $offset, limit = $limit", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        Log.d("Messenger", "getRefreshKey called with anchorPosition=${state.anchorPosition}")
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(pageSize) ?: page?.nextKey?.minus(pageSize)
        }
    }
}
