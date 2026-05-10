package com.example.messenger.di

import com.example.messenger.chats.ChatsFragment
import com.example.messenger.chats.ChatsViewModel
import com.example.messenger.messages.MessagesFragment
import com.example.messenger.messages.MessagesViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {
    fun inject(viewModel: ChatsViewModel)
    fun inject(viewModel: MessagesViewModel)
}
