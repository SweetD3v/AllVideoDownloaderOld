package com.tools.videodownloader.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tools.videodownloader.databinding.ActivityImageviewBinding

class ImageViewActivity : AppCompatActivity() {
    val binding by lazy { ActivityImageviewBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}