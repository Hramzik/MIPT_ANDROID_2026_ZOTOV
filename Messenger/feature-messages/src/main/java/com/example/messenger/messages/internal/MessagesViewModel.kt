package com.example.messenger.messages.internal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.messenger.di.CoreComponentProvider
import com.example.messenger.network.ApiService
import com.example.messenger.network.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

class MessagesViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService = (application as CoreComponentProvider).coreComponent.apiService()
    private val pageSize: Int = 20

    private val selectedChatId = MutableStateFlow<Int?>(null)

    val messagesFlow: Flow<PagingData<Message>> = selectedChatId
        .filterNotNull()
        .flatMapLatest { chatId ->
            flow {
                val initial = fetchInitialKey(chatId)
                emitAll(
                    Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = false), initialKey = initial) {
                        MessagesPagingSource(api, chatId, pageSize)
                    }.flow
                )
            }
        }
        .cachedIn(viewModelScope)

    fun setChatId(chatId: Int?) {
        if (selectedChatId.value == chatId) return
        selectedChatId.value = chatId
    }

    private suspend fun fetchInitialKey(chatId: Int): Int {
        return try {
            val resp = com.example.messenger.network.RetryExecutor.executeWithRetry {
                api.getChat(chatId, limit = 1, offset = 0)
            }
            val total = resp.body()?.total ?: 0
            android.util.Log.d("Messenger", "Chat $chatId has $total messages")
            kotlin.math.max(0, total - pageSize)
        } catch (e: Exception) {
            android.util.Log.e("Messenger", "Chat $chatId messages count fetch failed", e)
            0
        }
    }

    fun sendMessage(text: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val chatId = selectedChatId.value
                if (chatId == null) {
                    onResult(false)
                    return@launch
                }
                com.example.messenger.network.RetryExecutor.executeWithRetry {
                    api.postMessage(chatId, text)
                }
                onResult(true)
            } catch (e: Exception) {
                android.util.Log.e("Messenger", "sendMessage failed", e)
                onResult(false)
            }
        }
    }
}
