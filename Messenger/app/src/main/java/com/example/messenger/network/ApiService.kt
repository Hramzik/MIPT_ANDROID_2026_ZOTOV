package com.example.messenger.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/mipt_network/chat")
    suspend fun getChat(
        @Query("id") id: Int,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<ChatResponse>
}
