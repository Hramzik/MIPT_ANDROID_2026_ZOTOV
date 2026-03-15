package com.example.clicker

import kotlin.math.ceil
import kotlin.math.log10

object LevelManager {
    fun getLevel(clicks: Int): Int {
        if (clicks <= 0) return 1
        return ceil(log10(clicks.toDouble() + 1)).toInt()
    }

    fun getRemainingClicksToNextLevel(clicks: Int): Long {
        val level = getLevel(clicks)
        val threshold = Math.pow(10.0, level.toDouble()).toLong()
        return threshold - clicks
    }
}
