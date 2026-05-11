package com.example.messenger

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import android.view.Menu
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private val mainVm: MainViewModel by viewModels()
    private var isLandscape: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let { setSupportActionBar(it) }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        isLandscape = findViewById<android.view.View?>(com.example.messenger.core.R.id.messages_container) != null

        if (isLandscape) {
            if (supportFragmentManager.findFragmentById(com.example.messenger.core.R.id.chats_container) == null) {
                supportFragmentManager.beginTransaction()
                    .replace(com.example.messenger.core.R.id.chats_container, com.example.messenger.chats.api.ChatsFragment())
                    .replace(com.example.messenger.core.R.id.messages_container, com.example.messenger.messages.api.MessagesFragment())
                    .commit()
            }
        } else {
            if (supportFragmentManager.findFragmentById(com.example.messenger.core.R.id.fragment_container) == null) {
                supportFragmentManager.beginTransaction()
                    .replace(com.example.messenger.core.R.id.fragment_container, com.example.messenger.chats.api.ChatsFragment())
                    .commit()
            }
            
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // Clear menu when showing messages in port
        val currentFragment = supportFragmentManager.findFragmentById(com.example.messenger.core.R.id.fragment_container)
        if (currentFragment is com.example.messenger.messages.api.MessagesFragment && !isLandscape) {
            menu.clear()
            return true
        }
        return super.onPrepareOptionsMenu(menu)
    }
}