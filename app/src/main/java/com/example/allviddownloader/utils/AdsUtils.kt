package com.example.allviddownloader.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class AdsUtils {
    companion object {
        private var adView: AdView? = null
        var clicksCountCreation: Int = 0
        var clicksCountWA: Int = 0
        var clicksCountWallp: Int = 0
        var clicksCount4: Int = 0
        var clicksCount3: Int = 0
        var clicksCount2: Int = 0
        var clicksCountTools: Int = 1

        var interstitialAd: InterstitialAd? = null

        abstract class FullScreenCallback {
            open fun onAdLoaded(interstitialAd: InterstitialAd?) {}
            open fun onAdShowed() {}
            open fun onAdFailed() {}
            open fun onAdFailedToShow() {}
            open fun onAdDismissed() {}
            abstract fun continueExecution()
        }

        fun loadInterstitialAd(
            activity: Activity,
            adId: String,
            fullScreenCallback: FullScreenCallback?
        ) {
            if (!NetworkState.isOnline()) {
                fullScreenCallback?.continueExecution()
                return
            }
            var handler: Handler? = Handler(Looper.getMainLooper())
            var runnable: Runnable? = Runnable {
                MyProgressDialog.dismissDialog()
                interstitialAd?.show(activity)
            }
            try {
                MyProgressDialog.showDialog(activity, "Please wait...", false)
            } catch (e: Exception) {
                MyProgressDialog.dismissDialog()
            }
            runnable?.let { handler?.postDelayed(it, 3000) }

            InterstitialAd.load(
                activity,
                adId,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        fullScreenCallback?.onAdLoaded(interstitialAd)
                        interstitialAd?.fullScreenContentCallback = object :
                            FullScreenContentCallback() {
                            override fun onAdShowedFullScreenContent() {
                                fullScreenCallback?.onAdShowed()
                            }

                            override fun onAdDismissedFullScreenContent() {
                                runnable?.let { handler?.removeCallbacks(it) }
                                handler = null
                                runnable = null
                                fullScreenCallback?.onAdDismissed()
                                fullScreenCallback?.continueExecution()
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                runnable?.let { handler?.removeCallbacks(it) }
                                handler = null
                                runnable = null
                                fullScreenCallback?.onAdFailedToShow()
                                fullScreenCallback?.continueExecution()
                            }
                        }

                        MyProgressDialog.dismissDialog()
                        interstitialAd?.show(activity)
                        runnable?.let { handler?.removeCallbacks(it) }
                        handler = null
                        runnable = null
                    }

                    override fun onAdFailedToLoad(ad: LoadAdError) {
                        runnable?.let { handler?.removeCallbacks(it) }
                        handler = null
                        runnable = null
                        MyProgressDialog.dismissDialog()
                        fullScreenCallback?.onAdFailed()
                        fullScreenCallback?.continueExecution()
                    }
                })
        }

        fun destroyBanner() {
            adView?.destroy()
        }

        fun loadBanner(
            activity: AppCompatActivity,
            bannerContainer: FrameLayout,
            banner_id: String
        ) {
            adView = AdView(activity)
            adView?.adUnitId = banner_id
            bannerContainer.addView(adView)
            val adRequest = AdRequest.Builder().build()
            val adSize = getAdSize(activity)
            adView?.adSize = adSize
            adView?.loadAd(adRequest)
        }

        fun getAdSize(activity: AppCompatActivity): AdSize {
            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        }

        fun loadNative(context: Context, adId: String, frameLayout: FrameLayout) {
            val adLoader =
                AdLoader.Builder(context, adId)
                    .forNativeAd { nativeAd: NativeAd ->
                        val adView =
                            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                                .inflate(
                                    R.layout.admob_native_medium_new,
                                    null
                                ) as NativeAdView
                        adView.mediaView = adView.findViewById(R.id.media_view)
                        adView.headlineView = adView.findViewById(R.id.primary)
                        adView.bodyView = adView.findViewById(R.id.secondary)
                        adView.callToActionView = adView.findViewById(R.id.call_to_action)
                        adView.iconView = adView.findViewById(R.id.icon)
                        adView.advertiserView = adView.findViewById(R.id.tertiary)
                        populateUnifiedNativeAdView(adView, nativeAd)
                        frameLayout.visibility = View.VISIBLE
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            frameLayout.visibility = View.GONE
                        }
                    })
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: $error")
                        }
                    })
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        fun loadNativeSmall(context: Context, adId: String, frameLayout: FrameLayout) {
            val adLoader =
                AdLoader.Builder(context, adId)
                    .forNativeAd { nativeAd: NativeAd ->
                        val adView =
                            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                                .inflate(
                                    R.layout.admob_native_small,
                                    null
                                ) as NativeAdView
                        adView.mediaView = adView.findViewById(R.id.media_view)
                        adView.headlineView = adView.findViewById(R.id.primary)
                        adView.bodyView = adView.findViewById(R.id.secondary)
                        adView.callToActionView = adView.findViewById(R.id.call_to_action)
                        adView.iconView = adView.findViewById(R.id.icon)
                        adView.advertiserView = adView.findViewById(R.id.tertiary)
                        populateUnifiedNativeAdView(adView, nativeAd)
                        frameLayout.visibility = View.VISIBLE
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            frameLayout.visibility = View.GONE
                        }
                    })
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: $error")
                        }
                    })
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun populateUnifiedNativeAdView(
            unifiedNativeAdView: NativeAdView,
            unifiedNativeAd: NativeAd
        ) {
            (unifiedNativeAdView.headlineView as TextView).text = unifiedNativeAd.headline
            (unifiedNativeAdView.bodyView as TextView).text = unifiedNativeAd.body
            (unifiedNativeAdView.callToActionView as TextView).text = unifiedNativeAd.callToAction
            val icon = unifiedNativeAd.icon
            if (icon == null) {
                unifiedNativeAdView.iconView?.visibility = View.INVISIBLE
            } else {
                (unifiedNativeAdView.iconView as ImageView).setImageDrawable(icon.drawable)
                unifiedNativeAdView.iconView?.visibility = View.VISIBLE
            }
            if (unifiedNativeAd.advertiser == null) {
                unifiedNativeAdView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (unifiedNativeAdView.advertiserView as TextView).text = unifiedNativeAd.advertiser
                unifiedNativeAdView.advertiserView?.visibility = View.VISIBLE
            }
            unifiedNativeAdView.setNativeAd(unifiedNativeAd)
        }
    }
}