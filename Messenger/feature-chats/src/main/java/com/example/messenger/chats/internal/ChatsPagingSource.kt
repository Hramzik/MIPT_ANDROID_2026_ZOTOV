package com.example.messenger.chats.internal

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.example.messenger.network.ApiService
import com.example.messenger.network.Chat
import com.example.messenger.paging.BasePagingSource

class ChatsPagingSource(
    private val api: ApiService,
    private val pageSize: Int
) : BasePagingSource<Chat>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Chat> {
        val offset = params.key ?: 0
        val limit = params.loadSize.coerceAtMost(pageSize)
        android.util.Log.d("Messenger", "Loading chats, offset=$offset, loadSize=${params.loadSize}, limit=$limit")
        return try {
            val resp = callWithRetry { api.getChats(limit = limit, offset = offset) }
            android.util.Log.d("Messenger", "Result chats offset=$offset, limit=$limit -> code=${resp.code()}")
            if (resp.isSuccessful) {
                val body = resp.body()
                val items = body?.items() ?: emptyList()
                val prevKey = if (offset != 0) (offset - pageSize).coerceAtLeast(0) else null
                val nextKey = if (items.size >= limit) offset + pageSize else null
                LoadResult.Page(data = items, prevKey = prevKey, nextKey = nextKey)
            } else {
                LoadResult.Error(Exception("HTTP ${resp.code()} ${resp.message()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("Messenger", "Load exception for chats offset=$offset limit=$limit", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Chat>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(pageSize) ?: page?.nextKey?.minus(pageSize)
        }
    }
}
