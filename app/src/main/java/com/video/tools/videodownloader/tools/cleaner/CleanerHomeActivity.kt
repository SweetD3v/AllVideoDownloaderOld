package com.video.tools.videodownloader.tools.cleaner

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.video.tools.videodownloader.CleanerAnimActivity
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityCleanerHomeBinding
import com.video.tools.videodownloader.databinding.ActivityCleanerHomeNewBinding
import com.video.tools.videodownloader.phone_booster.app_utils.getCleanableSize
import com.video.tools.videodownloader.phone_booster.app_utils.getStorageFreeSizePercent
import com.video.tools.videodownloader.phone_booster.app_utils.getStorageOccupiedSizePercent
import com.video.tools.videodownloader.ui.activities.FullScreenActivity
import com.video.tools.videodownloader.utils.*


class CleanerHomeActivity : FullScreenActivity() {
    val binding by lazy { ActivityCleanerHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            adjustInsetsBoth(this@CleanerHomeActivity, {
                toolbar.rlMain.topMargin = it
            }, {
                bannerContainer.bottomMargin = it
            })

            toolbar.txtTitle.text = getString(R.string.cleaner)
            toolbar.imgBack.setOnClickListener { onBackPressed() }
            animCleaner.setOnClickListener {
                AppUtils.CLEANER_TYPE = 0
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clBatterySaver.setOnClickListener {
                AppUtils.CLEANER_TYPE = 1
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clNetOptimization.setOnClickListener {
                AppUtils.CLEANER_TYPE = 2
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clPhoneBooster.setOnClickListener {
                AppUtils.CLEANER_TYPE = 3
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
            clCpuCooler.setOnClickListener {
                AppUtils.CLEANER_TYPE = 4
                startActivity(Intent(this@CleanerHomeActivity, PhoneBoosterActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
