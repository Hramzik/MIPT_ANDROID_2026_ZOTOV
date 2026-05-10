package com.example.messenger

import android.app.Application
import com.example.messenger.di.CoreComponent
import com.example.messenger.di.CoreComponentProvider
import com.example.messenger.di.DaggerCoreComponent
import com.example.messenger.di.NetworkModule

class MyApp : Application(), CoreComponentProvider {
    override val coreComponent: CoreComponent by lazy {
        DaggerCoreComponent.builder()
            .networkModule(NetworkModule())
            .build()
    }
}
