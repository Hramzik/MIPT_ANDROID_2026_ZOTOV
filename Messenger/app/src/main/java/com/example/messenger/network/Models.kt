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
