package com.whats.stickers

import android.os.Build
import android.view.View
import android.view.Window

fun setLightStatusBarColor(window: Window, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    window.statusBarColor = color
}