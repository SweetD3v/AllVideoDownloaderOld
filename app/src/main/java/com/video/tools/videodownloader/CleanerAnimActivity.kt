package com.video.tools.videodownloader

import android.graphics.Color
import android.os.Bundle
import com.video.tools.videodownloader.databinding.ActivityBoosterAnimationsBinding
import com.video.tools.videodownloader.ui.activities.FullScreenActivity
import com.video.tools.videodownloader.utils.adjustInsets

class CleanerAnimActivity : FullScreenActivity() {
    val binding by lazy { ActivityBoosterAnimationsBinding.inflate(layoutInflater) }

    val type by lazy {
        if (intent.hasExtra("clean_type")) intent.getIntExtra(
            "clean_type",
            1
        ) else 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            rlMain.adjustInsets(this@CleanerAnimActivity)

            when (type) {
                1 -> {
                    txtTitle.text = "Phone Cleaner"
                    root.setBackgroundColor(Color.parseColor("#B1DEFF"))
                    animationView.setAnimation(R.raw.phone_clean)
                }
                2 -> {
                    txtTitle.text = "Battery Saver"
                    root.setBackgroundColor(Color.parseColor("#9EFFC1"))
                    animationView.setAnimation(R.raw.battery_saver)
                }
                3 -> {
                    txtTitle.text = "Booster"
                    root.setBackgroundColor(Color.parseColor("#7796E1"))
                    animationView.setAnimation(R.raw.boost)
                }
                else -> {
                    txtTitle.text = "CPU Cooler"
                    root.setBackgroundColor(Color.parseColor("#000000"))
                    animationView.setAnimation(R.raw.cooler)
                }
            }
        }
    }
}