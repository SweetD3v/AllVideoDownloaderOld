package com.example.allviddownloader.ui.activities

import android.os.Bundle
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityAllDownloadsBinding
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState

class AllDownloadsActivity : BaseActivity() {
    val binding by lazy { ActivityAllDownloadsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            imgBack.setOnClickListener { onBackPressed() }

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