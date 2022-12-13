package com.tools.videodownloader.tools.cleaner

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.tools.videodownloader.CleanerAnimActivity
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityCleanerHomeNewBinding
import com.tools.videodownloader.phone_booster.app_utils.getCleanableSize
import com.tools.videodownloader.phone_booster.app_utils.getStorageFreeSizePercent
import com.tools.videodownloader.phone_booster.app_utils.getStorageOccupiedSizePercent
import com.tools.videodownloader.ui.activities.FullScreenActivity
import com.tools.videodownloader.utils.adjustInsets
import com.tools.videodownloader.utils.formatSize


class CleanerHomeActivity : FullScreenActivity() {
    val binding by lazy { ActivityCleanerHomeNewBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        animateBubbles()

        binding.run {
            txtTitle.text = getString(R.string.collage_maker)
            toolbar.adjustInsets(this@CleanerHomeActivity)

            imgBack.setOnClickListener {
                onBackPressed()
            }

            txtTotalPercentage.text = getStorageOccupiedSizePercent()

            imgStorageCleaner.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, CleanerActivity::class.java))
            }

            imgJunkCleaner.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, CleanerAnimActivity::class.java)
                    .apply {
                        putExtra("clean_type", 1)
                    })
            }

            imgBatteryBoost.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, CleanerAnimActivity::class.java)
                    .apply {
                        putExtra("clean_type", 2)
                    })
            }

            imgCacheCleaner.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, CleanerAnimActivity::class.java)
                    .apply {
                        putExtra("clean_type", 3)
                    })
            }

            imgCPUCooler.setOnClickListener {
                startActivity(Intent(this@CleanerHomeActivity, CleanerAnimActivity::class.java)
                    .apply {
                        putExtra("clean_type", 4)
                    })
            }

            getCleanableSize(this@CleanerHomeActivity) { size ->
                txtCleanSize.text = size.formatSize()
            }
            txtFreeStorage.text = getStorageFreeSizePercent()
        }
    }

    private fun animateBubbles() {
        binding.run {
            imgJunkCleaner.post {
                val animation: Animation =
                    TranslateAnimation(0f, 0f, 0f, imgJunkCleaner.height / 2f).apply {
                        duration = 500 // duration - half a second
                        interpolator = LinearInterpolator() // do not alter animation rate
                        repeatCount = Animation.INFINITE // Repeat animation infinitely
                        repeatMode = Animation.REVERSE
                    }
                imgJunkCleaner.startAnimation(animation)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
