package com.example.messenger.chats.internal

import androidx.recyclerview.widget.DiffUtil
import com.example.messenger.network.Chat

object DiffUtilChatItemCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Chat, newItem: Chat) = oldItem == newItem
}
