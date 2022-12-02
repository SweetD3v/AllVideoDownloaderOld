package com.example.allviddownloader.ui.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityAllDownloadsBinding
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.adjustInsets

class AllDownloadsActivity : FullScreenActivity() {
    val binding by lazy { ActivityAllDownloadsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            toolbar.root.background = ContextCompat.getDrawable(
                this@AllDownloadsActivity,
                R.drawable.top_bar_gradient_green
            )
            toolbar.imgBack.setOnClickListener { onBackPressed() }

            toolbar.rlMain.adjustInsets(this@AllDownloadsActivity)

            if (NetworkState.isOnline()) {
                AdsUtils.loadBanner(
                    this@AllDownloadsActivity,
                    getString(R.string.banner_id_details), bannerContainer
                )
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}