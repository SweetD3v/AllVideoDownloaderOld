package com.tools.videodownloader.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tools.videodownloader.utils.setLightStatusBarColor
import com.tools.videodownloader.R

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBarColor(this, window, R.color.bg_default)
    }
}