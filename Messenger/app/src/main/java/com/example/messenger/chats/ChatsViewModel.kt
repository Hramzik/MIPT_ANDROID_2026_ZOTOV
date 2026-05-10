package com.example.messenger.chats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.messenger.MyApp
import com.example.messenger.network.ApiService
import com.example.messenger.network.Chat
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatsViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var api: ApiService
    private val pageSize: Int = 20

    init {
        (application as MyApp).appComponent.inject(this)
    }

    val chatsFlow: Flow<PagingData<Chat>> = Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = false)) {
        ChatsPagingSource(api, pageSize)
    }.flow.cachedIn(viewModelScope)

}
