package com.video.tools.videodownloader.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.video.tools.videodownloader.utils.setLightStatusBarColor
import com.video.tools.videodownloader.R

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBarColor(this, window, R.color.bg_default)
    }
}