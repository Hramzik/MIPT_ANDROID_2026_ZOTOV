package com.example.messenger.di

import com.example.messenger.network.ApiService
import com.example.messenger.network.RetrofitClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService = RetrofitClient.apiService
}
