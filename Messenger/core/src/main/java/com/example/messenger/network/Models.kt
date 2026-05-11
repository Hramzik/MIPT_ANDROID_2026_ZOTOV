package com.example.messenger.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    val id: Int,
    val text: String
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    val id: Int,
    val messages: List<Message> = emptyList(),
    val total: Int? = null,
    val limit: Int? = null,
    val offset: Int? = null
)

@JsonClass(generateAdapter = true)
data class Chat(
    val id: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class ChatsResponse(
    val chats: List<Chat>? = null,
    val data: List<Chat>? = null,
    val total: Int? = null,
    val limit: Int? = null,
    val offset: Int? = null
) {
    fun items(): List<Chat> = data ?: chats ?: emptyList()
}
