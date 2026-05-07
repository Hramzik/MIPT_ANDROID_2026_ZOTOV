package com.example.messenger.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.network.Chat

class ChatsFilterAdapter(private val onClick: (Chat) -> Unit) : ListAdapter<Chat, ChatsFilterAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Chat, newItem: Chat) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item) else holder.clear()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.textChatTitle)
        fun bind(chat: Chat) {
            title.text = chat.name
            itemView.setOnClickListener { onClick(chat) }
        }
        fun clear() {
            title.text = ""
            itemView.setOnClickListener(null)
        }
    }
}
