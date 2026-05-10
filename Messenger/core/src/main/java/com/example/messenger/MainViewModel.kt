package com.example.messenger

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(private val ss: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_SELECTED = "selected_chat_id"
        private const val KEY_CHATS_RV = "chats_rv_state"
        private const val KEY_MESSAGES_RV = "messages_rv_state"
    }

    private val _selected = MutableStateFlow<Int?>(ss.get<Int?>(KEY_SELECTED))
    val selected: StateFlow<Int?> = _selected

    fun select(chatId: Int?) {
        _selected.value = chatId
        ss.set(KEY_SELECTED, chatId)
    }

    fun saveChatsRvState(obj: Any?) { ss.set(KEY_CHATS_RV, obj) }
    fun saveMessagesRvState(obj: Any?) { ss.set(KEY_MESSAGES_RV, obj) }
    fun getChatsRvState(): Any? = ss.get(KEY_CHATS_RV)
    fun getMessagesRvState(): Any? = ss.get(KEY_MESSAGES_RV)
}
