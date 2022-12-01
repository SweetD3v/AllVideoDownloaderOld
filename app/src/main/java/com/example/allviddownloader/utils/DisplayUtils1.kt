package com.example.allviddownloader.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DisplayUtils1 {
    companion object {
        fun View.adjustInsetsTop(activity: Activity) {
            ViewCompat.setOnApplyWindowInsetsListener(
                activity.window.decorView
            ) { _, insets ->
                val statusbarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusbarHeight
                insets
            }
        }
    }
}