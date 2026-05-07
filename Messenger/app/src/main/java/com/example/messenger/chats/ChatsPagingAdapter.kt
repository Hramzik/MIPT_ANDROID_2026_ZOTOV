package com.example.messenger.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.network.Chat

class ChatsPagingAdapter(private val onClick: (Chat) -> Unit) : PagingDataAdapter<Chat, ChatsPagingAdapter.VH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item, onClick) else holder.clear()
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    class DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Chat, newItem: Chat) = oldItem == newItem
    }
}
