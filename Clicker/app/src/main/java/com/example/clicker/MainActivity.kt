package com.example.clicker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var clickCounter: ClickCounter
    private lateinit var vibrationManager: VibrationManager
    private lateinit var muteButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val authorsButton: TextView = findViewById(R.id.authors_button)
        authorsButton.setOnClickListener {
            Toast.makeText(this, R.string.authors_button_toast_text, Toast.LENGTH_SHORT).show()
        }

        val statisticsButton: android.widget.Button = findViewById(R.id.statistics_button)
        statisticsButton.setOnClickListener {
            startActivity(android.content.Intent(this, StatisticsActivity::class.java))
        }

        val exitButton: ImageButton = findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            finishAffinity()
        }

        val telegramButton: ImageButton = findViewById(R.id.author_telegram_button)
        telegramButton.setOnClickListener {
            val telegramUrl = getString(R.string.author_telegram_button_url)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(telegramUrl))
            startActivity(intent)
        }

        vibrationManager = VibrationManager(this)
        val muteButton: ImageButton = findViewById(R.id.mute_button)
        vibrationManager.setOnVibrationStateChangeListener {
            if (vibrationManager.isVibrationEnabled) {
                muteButton.setImageResource(R.drawable.baseline_volume_up_24)
            } else {
                muteButton.setImageResource(R.drawable.baseline_volume_off_24)
            }
        }

        muteButton.setOnClickListener {
            vibrationManager.toggleVibration()
        }

        clickCounter = ClickCounter.create(this)

        val mainClickerButton: ImageButton = findViewById(R.id.main_clicker_button)
        mainClickerButton.setOnClickListener {
            vibrationManager.tryVibrate()
            clickCounter.increment()
        }

        val clickCounterView: TextView = findViewById(R.id.click_counter_view)
        val nextLevelTeaserView: TextView = findViewById(R.id.next_level_teaser_view)
        clickCounter.setClickListener {
            clickCounterView.text = clickCounter.clickCount.toString()
            val level = LevelManager.getLevel(clickCounter.clickCount)
            val remaining = LevelManager.getRemainingClicksToNextLevel(clickCounter.clickCount)
            nextLevelTeaserView.text = getString(R.string.next_level_teaser_text, remaining, level + 1)
        }
        clickCounter.loadClickCount()
        clickCounter.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        clickCounter.saveState(outState)
    }
}
