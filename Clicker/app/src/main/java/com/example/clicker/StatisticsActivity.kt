package com.example.clicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart

class StatisticsActivity : AppCompatActivity() {
    private lateinit var clickCounter: ClickCounter
    private lateinit var clicksChart: LineChart
    private lateinit var chartManager: ChartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        clickCounter = ClickCounter.create(this)

        configureBackButton()
        configureChart()
        configureTable()
    }

    private fun configureBackButton() {
        val backButton: android.widget.ImageButton = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            startActivity(android.content.Intent(this, MainActivity::class.java))
        }
    }

    private fun configureChart() {
        clicksChart = findViewById(R.id.view_click_chart)
        chartManager = ChartManager(clicksChart, clickCounter, this)
        chartManager.updateChart()
    }

    private fun configureTable() {
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
