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

        val backButton: android.widget.ImageButton = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            startActivity(android.content.Intent(this, MainActivity::class.java))
        }

        clickCounter = ClickCounter.create(this)
        clicksChart = findViewById(R.id.view_click_chart)
        
        chartManager = ChartManager(clicksChart, clickCounter, this)
        chartManager.updateChart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
