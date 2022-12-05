package com.example.allviddownloader.ui.activities

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityCacheCleanerBinding
import com.example.allviddownloader.databinding.ItemJunkFilesBinding
import com.example.allviddownloader.phone_booster.app_utils.getAllAppsPermissions
import com.example.allviddownloader.phone_booster.models.AppModel
import com.example.allviddownloader.utils.*
import com.example.allviddownloader.utils.AdsUtils.Companion.loadInterstitialAd
import com.example.allviddownloader.widgets.MarginItemDecoration

class CleanerActivity : FullScreenActivity() {
    val binding by lazy { ActivityCacheCleanerBinding.inflate(layoutInflater) }

    var handler: Handler? = Handler(Looper.getMainLooper())
    var handlerReverse: Handler? = Handler(Looper.getMainLooper())
    var cachePercentageReverse: Int = 100
    var cachePercentage: Int = 0
    var showedAd: Boolean = false
    var junkAppsList: MutableList<AppModel> = mutableListOf()

    val runnableReverse = object : Runnable {
        override fun run() {
            cachePercentageReverse -= 2
            binding.txtTotalPercentage.text = "$cachePercentageReverse %"
            binding.btnCleanCache.text = "Cleaning..."
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
                            btnCleanCache.text = "Done"
                            btnCleanCache.isEnabled = true
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
            binding.btnCleanCache.text = "Optimizing..."
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
                                            btnCleanCache.text = "Clean"
                                            btnCleanCache.isEnabled = true
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
                    this@CleanerActivity, getString(R.string.admob_native_id),
                    adFrame
                )
            }

            toolbar.txtTitle.text = getString(R.string.cleaner)
            toolbar.root.background = ContextCompat.getDrawable(
                this@CleanerActivity,
                R.drawable.top_bar_gradient_orange
            )

            toolbar.rlMain.adjustInsets(this@CleanerActivity)

            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            var permissions = getAllAppsPermissions(this@CleanerActivity)
//            permissions = ArrayList(permissions.filter {
//                it.permissions.contains(batteryPerms[0])
//                        || it.permissions.contains(batteryPerms[1])
//                        || it.permissions.contains(batteryPerms[2])
//                        || it.permissions.contains(batteryPerms[3])
//            })
            for (permission in permissions) {
                Log.e(
                    "TAGApp",
                    "App Name : ${permission.appName} -> CacheSize: ${permission.appSize}"
                )
                junkAppsList.add(permission)
            }

            rvAppsThreats.layoutManager = LinearLayoutManager(this@CleanerActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
            val junkAdapter = JunkAdapter(this@CleanerActivity)
            junkAdapter.appItemClickListener = object : AppItemClickListener {
                override fun onAppClicked(appModel: AppModel, position: Int) {

                }
            }
            rvAppsThreats.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            rvAppsThreats.adapter = junkAdapter
            junkAdapter.updateList(junkAppsList)

//            btnCleanCache.setOnClickListener {
//                if (btnCleanCache.text.equals("Done")) {
//                    loadInterstitialAd(
//                        this@CleanerActivity,
//                        getString(R.string.interstitial_id),
//                        object : AdsUtils.Companion.FullScreenCallback() {
//
//                            override fun onAdFailed() {
//                                showedAd = false
//                            }
//
//                            override fun onAdDismissed() {
//                                showedAd = true
//                            }
//
//                            override fun onAdFailedToShow() {
//                                showedAd = false
//                            }
//
//                            override fun onAdShowed() {
//                                showedAd = true
//                            }
//
//                            override fun continueExecution() {
//                                onBackPressed()
//                            }
//                        })
//                } else {
//                    object : AsyncTaskRunner<Void?, String>(this@CleanerActivity) {
//                        override fun doInBackground(params: Void?): String {
//                            cacheDir.deleteRecursively()
//                            return "Cleaning..."
//                        }
//
//                        override fun onPostExecute(result: String?) {
//                            super.onPostExecute(result)
//                            result?.let { size ->
//                                handlerReverse?.post(runnableReverse)
//                                binding.txtTotalCacheSize.text = size
//                                btnCleanCache.isEnabled = false
//                            }
//                        }
//                    }.execute(null, false)
//                }
//            }
        }
    }

    inner class JunkAdapter(var ctx: Context) : RecyclerView.Adapter<JunkAdapter.VH>() {
        var junkAppsList: MutableList<AppModel> = mutableListOf()
        var appItemClickListener: AppItemClickListener? = null

        fun updateList(junkAppsList: MutableList<AppModel>) {
            this.junkAppsList = junkAppsList
            notifyDataSetChanged()
        }

        inner class VH(var binding: ItemJunkFilesBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemJunkFilesBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val appModel = junkAppsList[holder.bindingAdapterPosition]

            holder.binding.run {
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
                getString(R.string.interstitial_id),
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