package com.example.clicker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var clickCounterView: TextView
    private lateinit var mainClickerButton: ImageButton
    private lateinit var clickCounter: ClickCounter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        clickCounterView = findViewById(R.id.click_counter_view)
        mainClickerButton = findViewById(R.id.main_clicker_button)
        clickCounter = ClickCounter.create(this)

        mainClickerButton.setOnClickListener {
            clickCounter.increment()
        }
        clickCounter.setClickListener {
            clickCounterView.text = clickCounter.clickCount.toString()
        }
        clickCounter.loadClickCount();
        clickCounter.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        clickCounter.saveState(outState)
    }
}
