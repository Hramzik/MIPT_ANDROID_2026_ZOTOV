package com.example.clicker

class ClickRegistrator(private val clickHistory: MutableList<Long>) {
    private var clickUpdateListener: (() -> Unit)? = null

    fun registerClick() {
        clickHistory.add(System.currentTimeMillis())
        clickUpdateListener?.invoke()
    }

    fun setClickCountUpdateListener(listener: () -> Unit) {
        this.clickUpdateListener = listener
    }
}
