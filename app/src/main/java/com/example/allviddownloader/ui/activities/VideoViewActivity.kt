package com.example.allviddownloader.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.allviddownloader.databinding.ActivityVideoviewBinding

class VideoViewActivity : AppCompatActivity() {
    private val binding by lazy { ActivityVideoviewBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            videoView.setVideoURI(intent.getStringExtra("path").toString().toUri())
            videoView.setMediaController(mediaController)
            videoView.start()
        }
    }

    override fun onBackPressed() {
        binding.apply {
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }
        finish()
        super.onBackPressed()
    }
}