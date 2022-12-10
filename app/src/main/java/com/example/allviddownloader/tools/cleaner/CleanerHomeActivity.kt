package com.example.allviddownloader.tools.cleaner

import android.content.Intent
import android.os.Bundle
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityCleanerHomeBinding
import com.example.allviddownloader.phone_booster.app_utils.getCleanableSize
import com.example.allviddownloader.phone_booster.app_utils.getStorageFreeSizePercent
import com.example.allviddownloader.ui.activities.CleanerActivity
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.utils.adjustInsets
import com.example.allviddownloader.utils.formatSize

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

            rlAppCleaner.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, CleanerActivity::class.java))
            }

            rlBatteryBoost.setOnClickListener {
            }

            getCleanableSize(this@CleanerHomeActivity) { size ->
                txtCleanSize.text = size.formatSize()
            }
            txtFreeStorage.text = getStorageFreeSizePercent()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
