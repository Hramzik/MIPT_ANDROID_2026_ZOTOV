package com.example.clicker

import android.content.Context
import android.content.SharedPreferences
import android.os.Vibrator

class VibrationManager(private val context: Context) {
    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var listener: (() -> Unit)? = null
    
    companion object {
        private const val PREFS_NAME = "vibration_prefs"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val VIBRATION_DURATION = 50L
    }
    
    var isVibrationEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) {
            sharedPreferences.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()
            listener?.invoke()
        }

    fun setOnVibrationStateChangeListener(listener: () -> Unit) {
        this.listener = listener
    }

    fun tryVibrate() {
        if (isVibrationEnabled && vibrator.hasVibrator()) {
            vibrator.vibrate(VIBRATION_DURATION)
        }
    }
    
    fun toggleVibration() {
        isVibrationEnabled = !isVibrationEnabled
    }
}