package com.example.hw04

import android.view.View

fun View.setDimmed(dimmed: Boolean) {
    isEnabled = !dimmed
    alpha = if (dimmed) 0.4f else 1f
}