package com.video.tools.videodownloader.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.video.tools.videodownloader.databinding.ActivitySplashBinding
import com.video.tools.videodownloader.utils.adjustInsetsBoth
import com.video.tools.videodownloader.utils.bottomMargin
import com.video.tools.videodownloader.utils.gone
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import com.video.tools.videodownloader.utils.topMargin

class SplashScreenActivity : FullScreenActivity(), LifecycleObserver {
    val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val SPLASH_TIME_OUT: Long = 3000
    var isShowingAd = false
    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private var loadTime = 0L

    var cachePercentage: Int = 0
    var handlerConnecting: Handler? = Handler(Looper.getMainLooper())
    val runnableConnecting = object : Runnable {
        override fun run() {
            cachePercentage += 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                binding.progressLoading.setProgress(cachePercentage, true)
            else binding.progressLoading.progress = cachePercentage
            handlerConnecting?.postDelayed(this, 50)

            if (cachePercentage == 100) {
                handlerConnecting?.removeCallbacks(this)
                cachePercentage = 0
            }
        }
    }

    var handler: Handler? = Handler(Looper.getMainLooper())
    var runnable = Runnable { showAdIfAvailable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adjustInsetsBoth(this@SplashScreenActivity, {
            binding.clMain.topMargin = it
        }, {
            binding.clMain.bottomMargin = it
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onResume() {
        super.onResume()
        fetchAd()
        handler?.postDelayed(runnable, SPLASH_TIME_OUT)
        handlerConnecting?.post(runnableConnecting)
    }

    override fun onPause() {
        handler?.removeCallbacks(runnable)
        handler = Handler(Looper.getMainLooper())
        handlerConnecting?.removeCallbacks(runnableConnecting)
        handlerConnecting = Handler(Looper.getMainLooper())
        cachePercentage = 0
        super.onPause()
    }

    override fun onDestroy() {
        handler?.removeCallbacks(runnable)
        handler = null
        handlerConnecting?.removeCallbacks(runnableConnecting)
        handlerConnecting = null
        cachePercentage = 0
        super.onDestroy()
    }

    fun fetchAd() {
        if (isAdAvailable()) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                this@SplashScreenActivity.appOpenAd = appOpenAd
                loadTime = System.currentTimeMillis()
            }

        }
        val request: AdRequest = getAdRequest()
        AppOpenAd.load(
            this, RemoteConfigUtils.adIdAppOpen(), request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback!!
        )
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        continueExecution()
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                        binding.llLoading.gone()
                    }

                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        isShowingAd = false
                        continueExecution()
                    }
                }
            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd?.show(this)
        } else {
            continueExecution()
        }
    }

    fun continueExecution(){
        handlerConnecting?.removeCallbacks(runnableConnecting)
        cachePercentage = 0
        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        finish()
    }

    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = System.currentTimeMillis() - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    override fun onBackPressed() {
    }
}