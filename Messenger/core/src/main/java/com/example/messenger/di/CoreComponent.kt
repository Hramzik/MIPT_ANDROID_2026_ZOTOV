package com.example.messenger.di

import com.example.messenger.network.ApiService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface CoreComponent {
    fun apiService(): ApiService
}
