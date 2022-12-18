package com.video.tools.videodownloader.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.video.tools.videodownloader.R

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
            val pd = ProgressDialogMine()
            if (!NetworkState.isOnline()) {
                fullScreenCallback?.continueExecution()
                return
            }
            var handler: Handler? = Handler(Looper.getMainLooper())
            var runnable: Runnable? = Runnable {
                pd.dismissDialog()
                interstitialAd?.show(activity)
            }
            try {
                pd.showDialog(activity, "Please wait...", false)
            } catch (e: Exception) {
                pd.dismissDialog()
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

                        pd.dismissDialog()
                        interstitialAd?.show(activity)
                        runnable?.let { handler?.removeCallbacks(it) }
                        handler = null
                        runnable = null
                    }

                    override fun onAdFailedToLoad(ad: LoadAdError) {
                        Log.e("TAG", "adException: ${ad.responseInfo}")
                        runnable?.let { handler?.removeCallbacks(it) }
                        handler = null
                        runnable = null
                        pd.dismissDialog()
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
            banner_id: String,
            bannerContainer: FrameLayout
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
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        fun loadNativeSmall(
            context: Context,
            adId: String,
            frameLayout: FrameLayout,
            adLoaded: () -> Unit,
            adFailed: () -> Unit
        ) {
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
                        override fun onAdLoaded() {
                            adLoaded()
                        }
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            frameLayout.visibility = View.GONE
                            adFailed()
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

    class ProgressDialogMine {
        var dialog: Dialog? = null
        fun showDialog(context: Context?, text: String, cancelable: Boolean) {
            dialog = Dialog(context!!, R.style.RoundedCornersDialog)
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setCancelable(false)
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            linearLayout.layoutParams = layoutParams
            val progressBar = ProgressBar(context)
            progressBar.indeterminateDrawable.setTint(context.resources.getColor(R.color.colorPrimary))
            val layoutParams_progress = LinearLayout.LayoutParams(dpToPx(48), dpToPx(48))
            layoutParams_progress.gravity = Gravity.CENTER_VERTICAL
            val linearlayoutParams_progress = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            linearLayout.setPadding(40, 48, 24, 48)
            linearlayoutParams_progress.gravity = Gravity.CENTER
            progressBar.layoutParams = layoutParams_progress
            linearLayout.addView(progressBar)
            val textView = TextView(context)
            textView.textSize = 15f
            textView.text = text
            textView.setTextColor(Color.GRAY)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setPadding(40, 0, 0, 0)
            val linearlayoutParams_text = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textView.layoutParams = linearlayoutParams_text
            linearLayout.addView(textView)
            dialog!!.window!!.setContentView(linearLayout, layoutParams)
            dialog!!.setCancelable(cancelable)
            if (dialog != null && !dialog!!.isShowing) {
                dialog!!.show()
            }
        }

        fun dismissDialog() {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }

        private fun dpToPx(dp: Int): Int {
            return Math.round(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp.toFloat(), Resources.getSystem().displayMetrics
                )
            )
        }
    }
}