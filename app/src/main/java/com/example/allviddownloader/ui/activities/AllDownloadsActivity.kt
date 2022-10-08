package com.example.allviddownloader.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.databinding.ActivityAllDownloadsBinding

class AllDownloadsActivity : BaseActivity() {
    val binding by lazy { ActivityAllDownloadsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            imgBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}