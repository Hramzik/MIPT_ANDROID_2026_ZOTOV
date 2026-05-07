package com.example.messenger.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.messenger.network.ApiService
import com.example.messenger.network.Chat
import kotlinx.coroutines.flow.Flow

class ChatsViewModel(
    private val api: ApiService,
    private val pageSize: Int = 20
) : ViewModel() {

    val chatsFlow: Flow<PagingData<Chat>> = Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = false)) {
        ChatsPagingSource(api, pageSize)
    }.flow.cachedIn(viewModelScope)

}
