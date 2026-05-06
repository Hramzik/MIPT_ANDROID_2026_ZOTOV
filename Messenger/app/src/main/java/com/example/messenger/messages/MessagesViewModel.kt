package com.example.messenger.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.messenger.network.ApiService
import com.example.messenger.network.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MessagesViewModel(
    private val api: ApiService,
    private val chatId: Int = 1,
    private val pageSize: Int = 20
) : ViewModel() {

    private val initialKey = MutableStateFlow<Int?>(null)

    val messagesFlow: Flow<PagingData<Message>> = initialKey
        .filterNotNull()
        .flatMapLatest { offset ->
            Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = false), initialKey = offset) {
                MessagesPagingSource(api, chatId, pageSize)
            }.flow.cachedIn(viewModelScope)
        }

    init {
        viewModelScope.launch {
            try {
                val resp = com.example.messenger.network.RetryExecutor.executeWithRetry {
                    api.getChat(chatId, limit = 1, offset = 0)
                }
                val total = resp.body()?.total ?: 0
                android.util.Log.d("Messenger", "Chat $chatId has $total messages")
                val initial = if (total <= 0) 0 else kotlin.math.max(0, total - pageSize)
                initialKey.value = initial
            } catch (e: Exception) {
                android.util.Log.e("Messenger", "Chat $chatId messages count fetch failed", e)
                initialKey.value = 0
            }
        }
    }
}
