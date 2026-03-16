package com.example.clicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class StatisticsActivity : AppCompatActivity() {
    private lateinit var clickCounter: ClickCounter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_statistics_table)

        clickCounter = ClickCounter.create(this)

        val monthValue: TextView = findViewById(R.id.month_value)
        val weekValue: TextView = findViewById(R.id.week_value)
        val dayValue: TextView = findViewById(R.id.day_value)

        monthValue.text = clickCounter.getMonthClicks().toString()
        weekValue.text = clickCounter.getWeekClicks().toString()
        dayValue.text = clickCounter.getTodayClicks().toString()
    }
}