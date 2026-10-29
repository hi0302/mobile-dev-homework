package com.example.hw05

import android.view.View

fun View.setDimmed(dimmed: Boolean) {
    isEnabled = !dimmed
    alpha = if (dimmed) 0.45f else 1f
}