package com.example.clicker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import com.example.clicker.ui.SimpleLineChart

class StatisticsActivity : AppCompatActivity() {
    private val clickViewModel: ClickHistoryViewModel by viewModels()
    private lateinit var clickCounter: ClickCounter
    private lateinit var chartManager: ChartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_statistics)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_statistics)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        clickCounter = ClickCounter(clickViewModel.clickHistory)

        setupBackButton()
        setupTable()
        setupChart()
    }

    private fun setupBackButton() {
        val backButton: android.widget.ImageButton = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupChart() {
        val clicksChart: SimpleLineChart = findViewById(R.id.view_click_chart)
        chartManager = ChartManager(clicksChart, clickCounter, this)
        chartManager.updateChart()
    }

    private fun setupTable() {
        val monthClickCountView: TextView = findViewById(R.id.statistics_table_month_click_count)
        val weekClickCountView: TextView = findViewById(R.id.statistics_table_week_click_count)
        val dayClickCountView: TextView = findViewById(R.id.statistics_table_day_click_count)
        val minuteClickCountView: TextView = findViewById(R.id.statistics_table_minute_click_count)

        monthClickCountView.text = clickCounter.getMonthClickCount().toString()
        weekClickCountView.text = clickCounter.getWeekClickCount().toString()
        dayClickCountView.text = clickCounter.getTodayClickCount().toString()
        minuteClickCountView.text = clickCounter.getLastMinuteClickCount().toString()
    }
}
