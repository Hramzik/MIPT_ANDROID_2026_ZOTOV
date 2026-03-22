package com.example.clicker.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue

fun Context.colorFromAttr(attrRes: Int): Int? {
    val typedValue = TypedValue()
    if (theme.resolveAttribute(attrRes, typedValue, true)) {
        return typedValue.data
    }
    return null
}
