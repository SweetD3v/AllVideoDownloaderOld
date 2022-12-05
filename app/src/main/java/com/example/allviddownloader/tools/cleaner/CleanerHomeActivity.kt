package com.example.allviddownloader.tools.cleaner

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityCleanerHomeBinding
import com.example.allviddownloader.ui.activities.CleanerActivity
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.utils.adjustInsets

class CleanerHomeActivity : FullScreenActivity() {
    val binding by lazy { ActivityCleanerHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            txtTitle.text = getString(R.string.collage_maker)
            toolbar.adjustInsets(this@CleanerHomeActivity)

            imgBack.setOnClickListener {
                onBackPressed()
            }

            llAppCleaner.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, CleanerActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}