package com.example.messenger.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
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

    // For chat searching
    fun peekItem(position: Int): Chat? = getItem(position)
}
