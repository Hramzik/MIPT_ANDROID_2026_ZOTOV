package com.example.clicker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val clickViewModel: ClickHistoryViewModel by viewModels()
    private lateinit var clickCounter: ClickCounter
    private lateinit var clickRegistrator: ClickRegistrator
    private lateinit var vibrationManager: VibrationManager

    private lateinit var clickCounterView: TextView
    private lateinit var nextLevelTeaserView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLayout()
        getViewsById()
        setupAuxButtons()
        setupMainClickerButton()

        OnClickCountUpdate()
    }

    private fun getViewsById() {
        clickCounterView = findViewById(R.id.view_click_counter)
        nextLevelTeaserView = findViewById(R.id.view_next_level_teaser)
    }

    override fun onPause() {
        super.onPause()
        clickViewModel.saveOnDisk()
    }

    private fun setupLayout() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupAuxButtons() {
        val authorsButton: TextView = findViewById(R.id.button_authors)
        authorsButton.setOnClickListener {
            Toast.makeText(this, R.string.button_authors_toast_text, Toast.LENGTH_SHORT).show()
        }

        val statisticsButton: android.widget.Button = findViewById(R.id.button_statistics)
        statisticsButton.setOnClickListener {
            startActivity(android.content.Intent(this, StatisticsActivity::class.java))
        }

        val exitButton: ImageButton = findViewById(R.id.button_exit)
        exitButton.setOnClickListener {
            finishAffinity()
        }

        val telegramButton: ImageButton = findViewById(R.id.button_author_telegram)
        telegramButton.setOnClickListener {
            val telegramUrl = getString(R.string.button_author_telegram_url)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(telegramUrl))
            startActivity(intent)
        }

        setupMuteButton()
    }

    private fun setupMuteButton() {
        vibrationManager = VibrationManager(this)
        val muteButton: ImageButton = findViewById(R.id.button_mute)

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
    }

    private fun setupMainClickerButton() {
        clickCounter = ClickCounter(clickViewModel.clickHistory)
        clickRegistrator = ClickRegistrator(clickViewModel.clickHistory)

        val mainClickerButton: ImageButton = findViewById(R.id.button_main_clicker)
        mainClickerButton.setOnClickListener {
            vibrationManager.tryVibrate()
            clickRegistrator.registerClick()
        }

        clickRegistrator.setClickCountUpdateListener {
            OnClickCountUpdate()
        }
    }

    private fun OnClickCountUpdate() {
        val level = LevelManager.getLevel(clickCounter.clickCount)
        val remaining = LevelManager.getRemainingClicksToNextLevel(clickCounter.clickCount)
        clickCounterView.text = clickCounter.clickCount.toString()
        nextLevelTeaserView.text = getString(R.string.next_level_teaser_text, remaining, level + 1)
    }
}
