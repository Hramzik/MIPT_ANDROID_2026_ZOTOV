package com.example.clicker

import android.content.Context
import android.os.Bundle

class ClickCounter private constructor(
    private val context: Context,
    private val prefsName: String = "ClickerPrefs",
    private val keyClickHistory: String = "click_history"
) {
    data class ClickRecord(val timestamp: Long)

    private val clickHistory = mutableListOf<ClickRecord>()
    private var listener: (() -> Unit)? = null

    companion object {
        fun create(context: Context): ClickCounter {
            return ClickCounter(context.applicationContext)
        }
    }

    init {
        loadClickHistory()
    }

    fun setClickListener(listener: () -> Unit) {
        this.listener = listener
    }

    fun increment() {
        clickHistory.add(ClickRecord(System.currentTimeMillis()))
        saveClickHistory()
        listener?.invoke()
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
    }

    private fun saveClickHistory() {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val json = clickHistory.joinToString(",") { it.timestamp.toString() }
        prefs.edit().putString(keyClickHistory, json).apply()
    }

    fun getTodayClicks(): Int {
        val now = System.currentTimeMillis()
        val startOfDay = getStartOfDay(now)
        return clickHistory.count { it.timestamp >= startOfDay }
    }

    fun getWeekClicks(): Int {
        val now = System.currentTimeMillis()
        val startOfWeek = getStartOfWeek(now)
        return clickHistory.count { it.timestamp >= startOfWeek }
    }

    fun getMonthClicks(): Int {
        val now = System.currentTimeMillis()
        val startOfMonth = getStartOfMonth(now)
        return clickHistory.count { it.timestamp >= startOfMonth }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getStartOfWeek(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(java.util.Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getStartOfMonth(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
