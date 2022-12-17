package com.tools.videodownloader.ui.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityAllDownloadsBinding
import com.tools.videodownloader.utils.AdsUtils
import com.tools.videodownloader.utils.NetworkState
import com.tools.videodownloader.utils.adjustInsets
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils

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
                    RemoteConfigUtils.adIdBanner(), bannerContainer
                )
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}