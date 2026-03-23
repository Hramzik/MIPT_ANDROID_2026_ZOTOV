package com.example.clicker

import java.util.concurrent.TimeUnit

class ClickCounter(private val clickHistory: MutableList<Long>) {
    private val dateManager = DateManager()

    val clickCount: Int
        get() = clickHistory.size

    fun getTodayClickCount(): Int {
            val now = System.currentTimeMillis()
            val startOfDay = dateManager.getStartOfDay(now)
            return clickHistory.count { it >= startOfDay }
        }

    fun getClickCountByDay(): Map<String, Int> {
        val now = System.currentTimeMillis()
        val startOfMonth = dateManager.getStartOfMonth(now)
        val clickCountByDay = mutableMapOf<String, Int>()
        
        for (timestamp in clickHistory) {
            if (timestamp >= startOfMonth) {
                val dateKey = dateManager.getDateString(timestamp)
                clickCountByDay[dateKey] = clickCountByDay.getOrDefault(dateKey, 0) + 1
            }
        }
        
        return clickCountByDay
    }

    fun getLastNDaylyClicks(daysCount: Int): List<Int> {
        val calendar = dateManager.getNDaysAgoCalendar(daysCount - 1)
        val clickCounts = mutableListOf<Int>()

        for (i in 0 until daysCount) {
            val dayStart = calendar.timeInMillis
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
            val dayEnd = calendar.timeInMillis
            
            val clicksOnDay = clickHistory.count { it >= dayStart && it < dayEnd }
            clickCounts.add(clicksOnDay)
        }

        return clickCounts
    }

    fun getWeekClickCount(): Int {
        val now = System.currentTimeMillis()
        val startOfWeek = dateManager.getStartOfWeek(now)
        return clickHistory.count { it >= startOfWeek }
    }

    fun getMonthClickCount(): Int {
        val now = System.currentTimeMillis()
        val startOfMonth = dateManager.getStartOfMonth(now)
        return clickHistory.count { it >= startOfMonth }
    }

    fun getLastMinuteClickCount(): Int {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - TimeUnit.MINUTES.toMillis(1)
        return clickHistory.count { it >= oneMinuteAgo }
    }

    fun getLastMinuteClickCountByInterval(): List<Int> {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - TimeUnit.MINUTES.toMillis(1)
        val intervalDuration = TimeUnit.SECONDS.toMillis(15)
        val intervals = 4

        val result = MutableList(intervals) { 0 }

        for (timestamp in clickHistory) {
            if (timestamp >= oneMinuteAgo && timestamp <= now) {
                val timeSinceStart = timestamp - oneMinuteAgo
                val intervalIndex = (timeSinceStart / intervalDuration).toInt()
                if (intervalIndex >= 0 && intervalIndex < intervals) {
                    result[intervalIndex]++
                }
            }
        }

        return result
    }
}
