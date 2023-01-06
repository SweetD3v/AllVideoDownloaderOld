package com.video.tools.videodownloader.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityExitBinding
import com.video.tools.videodownloader.utils.AdsUtils
import com.video.tools.videodownloader.utils.adjustInsetsBoth
import com.video.tools.videodownloader.utils.gone
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import com.video.tools.videodownloader.utils.topMargin

class ExitActivity : BaseActivity() {
    val binding by lazy { ActivityExitBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.imgBack.gone()
            toolbar.txtTitle.text = getString(R.string.exit)

            adjustInsetsBoth(this@ExitActivity, {
                toolbar.rlMain.topMargin = it
            }, {

            })

            btnExit.setOnClickListener { finish() }
            btnNotNow.setOnClickListener {
                startActivity(Intent(this@ExitActivity, MainActivity::class.java))
                finish()
            }

            val colorScheme = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(
                    ContextCompat.getColor(
                        this@ExitActivity,
                        R.color.white
                    )
                )
                .build()
            val customtabs = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorScheme)
                .build()

            binding.clGame1.setOnClickListener {
                AdsUtils.loadInterstitialAd(
                    this@ExitActivity,
                    RemoteConfigUtils.adIdInterstital(),
                    object : AdsUtils.Companion.FullScreenCallback() {
                        override fun continueExecution() {
                            customtabs.launchUrl(this@ExitActivity, getString(R.string.link_game2).toUri())
                        }
                    })
            }

            binding.clGame2.setOnClickListener {
                AdsUtils.loadInterstitialAd(
                    this@ExitActivity,
                    RemoteConfigUtils.adIdInterstital(),
                    object : AdsUtils.Companion.FullScreenCallback() {
                        override fun continueExecution() {
                            customtabs.launchUrl(this@ExitActivity, getString(R.string.link_game2).toUri())
                        }
                    })
            }

            binding.clGame3.setOnClickListener {
                AdsUtils.loadInterstitialAd(
                    this@ExitActivity,
                    RemoteConfigUtils.adIdInterstital(),
                    object : AdsUtils.Companion.FullScreenCallback() {
                        override fun continueExecution() {
                            customtabs.launchUrl(this@ExitActivity, getString(R.string.link_game2).toUri())
                        }
                    })
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ExitActivity, MainActivity::class.java))
        finish()
    }
}