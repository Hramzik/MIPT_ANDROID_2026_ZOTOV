package com.example.messenger.chats

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.network.Chat

class ChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.textChatTitle)
    fun bind(chat: Chat, onClick: (Chat) -> Unit) {
        title.text = chat.name
        itemView.setOnClickListener { onClick(chat) }
    }
    fun clear() {
        title.text = ""
        itemView.setOnClickListener(null)
    }
}
