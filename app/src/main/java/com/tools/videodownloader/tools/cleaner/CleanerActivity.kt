package com.tools.videodownloader.tools.cleaner

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityCacheCleanerBinding
import com.tools.videodownloader.databinding.ItemJunkFilesBinding
import com.tools.videodownloader.phone_booster.app_utils.getAllAppsPermissions
import com.tools.videodownloader.phone_booster.app_utils.getCleanableSize
import com.tools.videodownloader.phone_booster.models.AppModel
import com.tools.videodownloader.ui.activities.FullScreenActivity
import com.tools.videodownloader.utils.*
import com.tools.videodownloader.utils.AdsUtils.Companion.loadInterstitialAd
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import com.tools.videodownloader.widgets.MarginItemDecoration
import java.util.*


class CleanerActivity : FullScreenActivity() {
    val binding by lazy { ActivityCacheCleanerBinding.inflate(layoutInflater) }

    var handler: Handler? = Handler(Looper.getMainLooper())
    var handlerReverse: Handler? = Handler(Looper.getMainLooper())
    var cachePercentageReverse: Int = 100
    var cachePercentage: Int = 0
    var showedAd: Boolean = false
    var junkAppsList: MutableList<AppModel> = mutableListOf()

    lateinit var junkAdapter: JunkAdapter

    val runnableReverse = object : Runnable {
        override fun run() {
            cachePercentageReverse -= 2
            binding.txtTotalPercentage.text = "$cachePercentageReverse %"
            binding.btnCleanJunk.text = "Cleaning..."
            handler?.postDelayed(this, 50)

            if (cachePercentageReverse == 0) {
                handlerReverse?.removeCallbacks(this)
                handlerReverse = null

                binding.run {
                    txtTotalCacheSize.animate().scaleX(0f).scaleY(0f).apply {
                        duration = 500
                        setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator?) {
                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                txtTotalCacheSize.text = "Cache cleaned"
                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationRepeat(p0: Animator?) {
                            }
                        })
                    }
                    txtTotalPercentage.animate().scaleX(0f).scaleY(0f).apply {
                        duration = 500
                        setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator?) {
                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                txtTotalPercentage.text = "0 B"
                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationRepeat(p0: Animator?) {
                            }
                        })
                    }

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            txtTotalPercentage.animate().scaleX(1f).scaleY(1f).apply {
                                duration = 500
                            }
                            txtTotalCacheSize.animate().scaleX(1f).scaleY(1f).apply {
                                duration = 500
                            }
                            btnCleanJunk.text = "Done"
                            btnCleanJunk.isEnabled = true
                        }, 500)
                }
            }

            handlerReverse?.postDelayed(this, 50)
        }
    }

    val runnable = object : Runnable {
        override fun run() {
            cachePercentage += 1
            binding.txtTotalPercentage.text = "$cachePercentage %"
            binding.btnCleanJunk.text = "Optimizing..."
            handler?.postDelayed(this, 50)

            if (cachePercentage == 100) {
                handler?.removeCallbacks(this)
                handler = null
                cachePercentage = 0

                binding.run {
                    object : AsyncTaskRunner<Void?, String>(this@CleanerActivity) {
                        override fun doInBackground(params: Void?): String {
                            return cacheDir.getProperSize(true).formatSize()
                        }

                        override fun onPostExecute(result: String?) {
                            super.onPostExecute(result)
                            result?.let { size ->

                                txtTotalPercentage.animate().scaleX(0f).scaleY(0f).apply {
                                    duration = 500
                                    setListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(p0: Animator?) {
                                        }

                                        override fun onAnimationEnd(p0: Animator?) {
                                            txtTotalPercentage.text = size
                                        }

                                        override fun onAnimationCancel(p0: Animator?) {
                                        }

                                        override fun onAnimationRepeat(p0: Animator?) {
                                        }
                                    })
                                }
                                txtTotalCacheSize.animate().scaleX(0f).scaleY(0f).apply {
                                    duration = 500
                                    setListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(p0: Animator?) {
                                        }

                                        override fun onAnimationEnd(p0: Animator?) {
                                            txtTotalCacheSize.text = "Cache size"
                                            btnCleanJunk.text = "Clean"
                                            btnCleanJunk.isEnabled = true
                                        }

                                        override fun onAnimationCancel(p0: Animator?) {
                                        }

                                        override fun onAnimationRepeat(p0: Animator?) {
                                        }
                                    })
                                }

                                Handler(Looper.getMainLooper())
                                    .postDelayed({
                                        txtTotalPercentage.animate().scaleX(1f).scaleY(1f).apply {
                                            duration = 500
                                        }
                                        txtTotalCacheSize.animate().scaleX(1f).scaleY(1f).apply {
                                            duration = 500
                                        }

                                    }, 500)
                            }
                        }
                    }.execute(null, false)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
                AdsUtils.loadNativeSmall(
                    this@CleanerActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
//                AdsUtils.loadBanner(
//                    this@CleanerActivity, getString(R.string.interstitial_id),
//                    bannerContainer
//                )
            }

            toolbar.txtTitle.text = getString(R.string.cleaner)
//            toolbar.root.background = ContextCompat.getDrawable(
//                this@CleanerActivity,
//                R.drawable.top_bar_gradient_green
//            )

            adjustInsetsBoth(this@CleanerActivity, {
                toolbar.rlMain.topMargin = it
            }, {
                rlMain.bottomMargin = it
            })

            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            rvAppsThreats.layoutManager = LinearLayoutManager(this@CleanerActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
            rvAppsThreats.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            var permissions: MutableList<AppModel>
            junkAdapter = JunkAdapter(this@CleanerActivity)
            junkAdapter.appItemClickListener = object : AppItemClickListener {
                override fun onAppClicked(appModel: AppModel, position: Int) {

                }
            }
            rvAppsThreats.adapter = junkAdapter
            getAllAppsPermissions(this@CleanerActivity) { list ->
                junkAppsList = list

                junkAdapter.updateList(junkAppsList)
                getCleanableSize(this@CleanerActivity) { size ->
                    txtCleanableSize.text =
                        String.format(
                            Locale.ENGLISH,
                            getString(R.string.junk_files_found, size.formatSize())
                        )
                }
            }

            btnCleanJunk.setOnClickListener {
                junkAdapter.clearItems()
                txtCleanableSize.text =
                    String.format(
                        Locale.ENGLISH,
                        getString(R.string.junk_files_found, "0 B")
                    )
            }

//            permissions = ArrayList(permissions.filter {
//                it.permissions.contains(batteryPerms[0])
//                        || it.permissions.contains(batteryPerms[1])
//                        || it.permissions.contains(batteryPerms[2])
//                        || it.permissions.contains(batteryPerms[3])
//            })

            btnCleanJunk.setOnClickListener {
                if (btnCleanJunk.text.equals("Done")) {
                    loadInterstitialAd(
                        this@CleanerActivity,
                        RemoteConfigUtils.adIdInterstital(),
                        object : AdsUtils.Companion.FullScreenCallback() {

                            override fun onAdFailed() {
                                showedAd = false
                            }

                            override fun onAdDismissed() {
                                showedAd = true
                            }

                            override fun onAdFailedToShow() {
                                showedAd = false
                            }

                            override fun onAdShowed() {
                                showedAd = true
                            }

                            override fun continueExecution() {
                                onBackPressed()
                            }
                        })
                } else {
                    object : AsyncTaskRunner<Void?, String>(this@CleanerActivity) {
                        override fun doInBackground(params: Void?): String {
                            cacheDir.deleteRecursively()
                            return "Cleaning..."
                        }

                        override fun onPostExecute(result: String?) {
                            super.onPostExecute(result)
                            result?.let { size ->
                                handlerReverse?.post(runnableReverse)
                                binding.txtTotalCacheSize.text = size
                                btnCleanJunk.isEnabled = false
                            }
                        }
                    }.execute(null, false)
                }
            }
        }
    }

    inner class JunkAdapter(var ctx: Context) : RecyclerView.Adapter<JunkAdapter.VH>() {
        var junkAppsList: MutableList<AppModel> = mutableListOf()
        var appItemClickListener: AppItemClickListener? = null

        fun updateList(pdfList: MutableList<AppModel>) {
            setItems(pdfList)
        }

        fun setItems(items: MutableList<AppModel>) {
            clearItems()
            val newSize = items.size
            this.junkAppsList.addAll(items)
            notifyItemRangeInserted(0, newSize)
        }

        fun clearItems() {
            val oldSize = this.junkAppsList.size
            this.junkAppsList.clear()
            notifyItemRangeRemoved(0, oldSize)
        }

        inner class VH(var binding: ItemJunkFilesBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemJunkFilesBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val animation = AnimationUtils.loadAnimation(ctx, R.anim.fade_in)

            holder.binding.run {
                val appModel = junkAppsList[holder.bindingAdapterPosition]
                imgAppIcon.setImageDrawable(appModel.icon)
                txtAppName.text = appModel.appName
                txtCacheVal.text = appModel.appSize
            }
        }

        override fun getItemCount(): Int {
            return junkAppsList.size
        }
    }

    interface AppItemClickListener {
        fun onAppClicked(appModel: AppModel, position: Int)
    }

    override fun onResume() {
        super.onResume()

        handler = Handler(Looper.getMainLooper())
        handler?.post(runnable)
    }

    override fun onPause() {
        handler?.removeCallbacks(runnable)
        handler = null
        super.onPause()
    }

    override fun onBackPressed() {
        if (!showedAd) {
            loadInterstitialAd(
                this,
                RemoteConfigUtils.adIdInterstital(),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        finish()
                    }
                })
            return
        }
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}