package com.example.clicker

import android.content.Context
import android.os.Bundle
import java.util.concurrent.TimeUnit

class ClickCounter private constructor(
    private val context: Context,
    private val prefsName: String = "ClickerPrefs",
    private val keyClickHistory: String = "click_history"
) {
    data class ClickRecord(val timestamp: Long)

    private val dateManager = DateManager()
    private val clickHistory = mutableListOf<ClickRecord>()
    private var clickUpdateListener: (() -> Unit)? = null

    companion object {
        fun create(context: Context): ClickCounter {
            return ClickCounter(context.applicationContext)
        }
    }

    init {
        loadClickHistory()
    }

    fun setClickUpdateListener(listener: () -> Unit) {
        this.clickUpdateListener = listener
    }

    fun increment() {
        clickHistory.add(ClickRecord(System.currentTimeMillis()))
        saveClickHistory()
        clickUpdateListener?.invoke()
    }

    val clickCount: Int
        get() = clickHistory.size

    fun saveState(outState: Bundle) {
        // todo
    }

    fun restoreState(savedInstanceState: Bundle?) {
        // todo
    }

    fun loadClickHistory() {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val json = prefs.getString(keyClickHistory, null)
        if (json.isNullOrEmpty()) return

        clickHistory.clear()
        val parts = json.split(",")
        for (part in parts) {
            clickHistory.add(ClickRecord(part.toLong()))
        }

        clickUpdateListener?.invoke()
    }

    private fun saveClickHistory() {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val json = clickHistory.joinToString(",") { it.timestamp.toString() }
        prefs.edit().putString(keyClickHistory, json).apply()
    }

    fun getTodayClickCount(): Int {
            val now = System.currentTimeMillis()
            val startOfDay = dateManager.getStartOfDay(now)
            return clickHistory.count { it.timestamp >= startOfDay }
        }

    fun getDailyClicks(): Map<String, Int> {
        val now = System.currentTimeMillis()
        val startOfMonth = dateManager.getStartOfMonth(now)
        val dailyClicks = mutableMapOf<String, Int>()
        
        for (record in clickHistory) {
            if (record.timestamp >= startOfMonth) {
                val dateKey = dateManager.getDateString(record.timestamp)
                dailyClicks[dateKey] = dailyClicks.getOrDefault(dateKey, 0) + 1
            }
        }
        
        return dailyClicks
    }

    fun getWeekClickCount(): Int {
        val now = System.currentTimeMillis()
        val startOfWeek = dateManager.getStartOfWeek(now)
        return clickHistory.count { it.timestamp >= startOfWeek }
    }

    fun getMonthClickCount(): Int {
        val now = System.currentTimeMillis()
        val startOfMonth = dateManager.getStartOfMonth(now)
        return clickHistory.count { it.timestamp >= startOfMonth }
    }

    fun getLastMinuteClickCount(): Int {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - TimeUnit.MINUTES.toMillis(1)
        return clickHistory.count { it.timestamp >= oneMinuteAgo }
    }

    fun getLastMinuteClicksByInterval(): List<Int> {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - TimeUnit.MINUTES.toMillis(1)
        val intervalDuration = TimeUnit.SECONDS.toMillis(15)
        val intervals = 4

        val result = MutableList(intervals) { 0 }

        for (record in clickHistory) {
            if (record.timestamp >= oneMinuteAgo && record.timestamp <= now) {
                val timeSinceStart = record.timestamp - oneMinuteAgo
                val intervalIndex = (timeSinceStart / intervalDuration).toInt()
                if (intervalIndex >= 0 && intervalIndex < intervals) {
                    result[intervalIndex]++
                }
            }
        }

        return result
    }

}
