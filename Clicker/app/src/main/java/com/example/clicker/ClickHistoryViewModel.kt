package com.example.clicker

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.AndroidViewModel

class ClickHistoryViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    companion object {
        private const val KEY_BUNDLE_CLICK_HISTORY = "click_history"
        private const val PREFS_NAME = "ClickerPrefs"
        private const val KEY_DISK_CLICK_HISTORY = KEY_BUNDLE_CLICK_HISTORY
    }

    var clickHistory: MutableList<Long>
        get() {
            return savedStateHandle[KEY_BUNDLE_CLICK_HISTORY] ?: run {
                loadFromDisk()
                savedStateHandle[KEY_BUNDLE_CLICK_HISTORY] ?: mutableListOf()
            }
        }
        set(value) {
            savedStateHandle[KEY_BUNDLE_CLICK_HISTORY] = value
        }

    fun saveOnDisk() {
        val prefs = getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = clickHistory.joinToString(",")
        prefs.edit().putString(KEY_DISK_CLICK_HISTORY, json).apply()
    }

    fun loadFromDisk() {
        val prefs = getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_DISK_CLICK_HISTORY, null)
        if (json == null) {
            return
        }

        val result = arrayListOf<Long>()
        val parts = json.split(",")
        for (part in parts) {
            val value = part.toLongOrNull()
            if (value != null) {
                result.add(value)
            }
        }

        clickHistory = result
    }
}
