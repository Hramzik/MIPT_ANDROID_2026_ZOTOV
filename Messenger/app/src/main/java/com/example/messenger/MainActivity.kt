package com.example.messenger

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private val mainVm: MainViewModel by viewModels()

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

        val isLandscape = findViewById<android.view.View?>(R.id.messages_container) != null

        if (isLandscape) {
            if (supportFragmentManager.findFragmentByTag("CHATS") == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.chats_container, com.example.messenger.chats.ChatsFragment(), "CHATS")
                    .replace(R.id.messages_container, com.example.messenger.messages.MessagesFragment(), "MESSAGES")
                    .commit()
                
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.messages_container, com.example.messenger.messages.MessagesFragment(), "MESSAGES")
                        .commit()
            }
        } else {
            lifecycleScope.launchWhenStarted {
                mainVm.selected.collect { id ->
                    val frag = if (id != null) {
                        com.example.messenger.messages.MessagesFragment()
                    } else {
                        com.example.messenger.chats.ChatsFragment()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .commit()
                }
            }
        }
    }
}