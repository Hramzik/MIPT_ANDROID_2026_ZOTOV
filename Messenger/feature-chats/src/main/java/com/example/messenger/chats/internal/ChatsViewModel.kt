package com.example.messenger.chats.internal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.messenger.di.CoreComponentProvider
import com.example.messenger.network.ApiService
import com.example.messenger.network.Chat
import kotlinx.coroutines.flow.Flow

class ChatsViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService =
        (application as CoreComponentProvider).coreComponent.apiService()
    private val pageSize: Int = 20

    val chatsFlow: Flow<PagingData<Chat>> = Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = false)) {
        ChatsPagingSource(api, pageSize)
    }.flow.cachedIn(viewModelScope)
}
