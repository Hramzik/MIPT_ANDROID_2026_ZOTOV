package com.example.clicker

import android.content.Context
import android.os.Bundle

class ClickCounter private constructor(
    private val context: Context,
    private val prefsName: String = "ClickerPrefs",
    private val keyClickCount: String = "click_count"
) {
    var clickCount: Int = 0
        private set(value) {
            field = value
            saveClickCount()
            listener?.invoke()
        }

    private var listener: (() -> Unit)? = null

    companion object {
        fun create(context: Context): ClickCounter {
            return ClickCounter(context.applicationContext)
        }
    }

    init {
        loadClickCount()
    }

    fun setClickListener(listener: () -> Unit) {
        this.listener = listener
    }

    fun increment() {
        clickCount++
    }

    fun saveState(outState: Bundle) {
        outState.putInt(keyClickCount, clickCount)
    }

    fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            clickCount = it.getInt(keyClickCount)
        }
    }

    fun loadClickCount() {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        clickCount = prefs.getInt(keyClickCount, clickCount)
    }

    private fun saveClickCount() {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit().putInt(keyClickCount, clickCount).apply()
    }
}
