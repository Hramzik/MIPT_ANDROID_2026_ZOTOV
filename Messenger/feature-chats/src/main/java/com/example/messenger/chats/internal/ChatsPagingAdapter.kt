package com.example.messenger.chats.internal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.messenger.chats.R
import com.example.messenger.network.Chat

class ChatsPagingAdapter(private val onClick: (Chat) -> Unit) : PagingDataAdapter<Chat, ChatItemViewHolder>(DiffUtilChatItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item, onClick) else holder.clear()
    }

    fun peekItem(position: Int): Chat? = getItem(position)
}
