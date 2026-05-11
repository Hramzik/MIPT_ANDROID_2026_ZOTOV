package com.example.messenger.chats.internal

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.chats.R
import com.example.messenger.network.Chat

class ChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val SHOW_CHAT_ID = true
    }

    private val title: TextView = itemView.findViewById(R.id.textChatTitle)
    private val avatar: ImageView = itemView.findViewById(R.id.imageChatAvatar)
    fun bind(chat: Chat, onClick: (Chat) -> Unit) {
        title.text = if (SHOW_CHAT_ID) "${chat.name} (id: ${chat.id})" else chat.name
        val url = "https://i.pravatar.cc/80?img=${chat.id}"
        Glide.with(avatar)
            .load(url)
            .circleCrop()
            .into(avatar)
        itemView.setOnClickListener { onClick(chat) }
    }
    fun clear() {
        title.text = ""
        Glide.with(avatar).clear(avatar)
        itemView.setOnClickListener(null)
    }
}
