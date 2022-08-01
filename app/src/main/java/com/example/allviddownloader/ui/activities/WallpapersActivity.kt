package com.example.allviddownloader.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.databinding.ActivityWallpapersBinding

class WallpapersActivity : AppCompatActivity() {
    val binding by lazy { ActivityWallpapersBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadWallpapers()
    }

    private fun loadWallpapers() {

    }
}