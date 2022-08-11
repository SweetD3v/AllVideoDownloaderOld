package com.example.allviddownloader.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.allviddownloader.databinding.ActivityCleanerBinding

class CleanerActivity : AppCompatActivity() {
    val binding by lazy { ActivityCleanerBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}