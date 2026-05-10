package com.example.messenger

import android.app.Application
import com.example.messenger.di.AppComponent
import com.example.messenger.di.DaggerAppComponent
import com.example.messenger.di.NetworkModule

class MyApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .networkModule(NetworkModule())
            .build()
    }
}
