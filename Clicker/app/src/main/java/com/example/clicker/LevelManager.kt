package com.example.clicker

import android.content.Context
import kotlin.math.ceil
import kotlin.math.log
import kotlin.properties.Delegates

object LevelManager {
    private var levelBase: Double by Delegates.notNull()

    fun init(context: Context) {
        levelBase = context.resources.getInteger(R.integer.level_base).toDouble()
    }

    fun getLevel(clicks: Int): Int {
        if (clicks <= 0) return 1
        return ceil(log(clicks.toDouble() + 1, levelBase)).toInt()
    }

    fun getRemainingClicksToNextLevel(clicks: Int): Long {
        val level = getLevel(clicks)
        val threshold = Math.pow(levelBase, level.toDouble()).toLong()
        return threshold - clicks
    }
}
