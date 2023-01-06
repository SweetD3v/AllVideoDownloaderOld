package com.video.tools.videodownloader.tools.cleaner

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.constraintlayout.motion.widget.MotionLayout
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityPhoneBoosterBinding
import com.video.tools.videodownloader.ui.activities.FullScreenActivity
import com.video.tools.videodownloader.utils.*
import com.video.tools.videodownloader.utils.AppUtils.Companion.CLEANER_TYPE
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils

class PhoneBoosterActivity : FullScreenActivity() {
    val binding by lazy { ActivityPhoneBoosterBinding.inflate(layoutInflater) }

    var handler: Handler? = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.imgBack.setOnClickListener { onBackPressed() }

            animMain.rotation = 0f

            selectToolType(CLEANER_TYPE)

            when (CLEANER_TYPE) {
                0 -> {
                    toolbar.txtTitle.text = getString(R.string.cleaner)
                    animMain.setAnimation(R.raw.cleaner_home1)
                }
                1 -> {
                    toolbar.txtTitle.text = getString(R.string.battery_saver)
                    animMain.setAnimation(R.raw.battery_saver)
                }
                2 -> {
                    toolbar.txtTitle.text = getString(R.string.network_optimization)
                    animMain.setAnimation(R.raw.net_opt)
                }
                3 -> {
                    toolbar.txtTitle.text = getString(R.string.phone_booster)
                    animMain.setAnimation(R.raw.phone_booster)
                    animMain.rotation = -45f
                }
                4 -> {
                    toolbar.txtTitle.text = getString(R.string.cpu_cooler)
                    animMain.setAnimation(R.raw.cpu_cooler)
                }
            }

            clCleaner.setOnClickListener {
                CLEANER_TYPE = 0
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }

            clBatterySaver.setOnClickListener {
                CLEANER_TYPE = 1
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }

            clNetOptimization.setOnClickListener {
                CLEANER_TYPE = 2
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }

            clPhoneBooster.setOnClickListener {
                CLEANER_TYPE = 3
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }
            clCpuCooler.setOnClickListener {
                CLEANER_TYPE = 4
                finish()
                startActivity(Intent(this@PhoneBoosterActivity, PhoneBoosterActivity::class.java))
            }

            motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int
                ) {

                }

                override fun onTransitionChange(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {
                }

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                    if (currentId == R.id.end)
                        animDone.gone()
                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {
                }

            })

            handler?.postDelayed({
                animMain.gone()
                animDone.visible()
                animDone.playAnimation()

                if (CLEANER_TYPE == 0) {
                    toastShort(this@PhoneBoosterActivity, "RAM Cleaned!")
                } else if (CLEANER_TYPE == 1) {
                    toastShort(this@PhoneBoosterActivity, "Battery Optimized!")
                } else if (CLEANER_TYPE == 2) {
                    toastShort(this@PhoneBoosterActivity, "Network Optimized!")
                } else if (CLEANER_TYPE == 3) {
                    toastShort(this@PhoneBoosterActivity, "Memory Freed!")
                } else if (CLEANER_TYPE == 4) {
                    toastShort(this@PhoneBoosterActivity, "CPU Optimized!")
                }

                motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
                    override fun onTransitionStarted(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int
                    ) {

                    }

                    override fun onTransitionChange(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int,
                        progress: Float
                    ) {
                    }

                    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                        if (currentId == R.id.end) {
                            if (NetworkState.isOnline()
//                                && RemoteConfigUtils.canEnter
                            ) {
                                AdsUtils.loadNative(
                                    this@PhoneBoosterActivity, RemoteConfigUtils.adIdNative(),
                                    adFrame
                                )
                            }
                        }
                    }

                    override fun onTransitionTrigger(
                        motionLayout: MotionLayout?,
                        triggerId: Int,
                        positive: Boolean,
                        progress: Float
                    ) {
                    }

                })

                animDone.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (NetworkState.isOnline()
//                            && RemoteConfigUtils.canEnter
                        ) {
                            AdsUtils.loadInterstitialAd(this@PhoneBoosterActivity,
                                RemoteConfigUtils.adIdInterstital(),
                                object : AdsUtils.Companion.FullScreenCallback() {
                                    override fun continueExecution() {
                                        motionLayout.transitionToEnd()
                                    }
                                })
                        } else {
                            motionLayout.transitionToEnd()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                })
            }, 5000)
        }
    }

    private fun selectToolType(cleanerType: Int) {
        binding.run {
            when (cleanerType) {
                0 -> {
                    clBatterySaver.visible()
                    clNetOptimization.visible()
                    clPhoneBooster.visible()
                    clCleaner.gone()
                    clBatterySaver.visible()
                }
                1 -> {
                    clBatterySaver.gone()
                    clNetOptimization.visible()
                    clPhoneBooster.visible()
                    clCleaner.visible()
                    clCpuCooler.visible()
                }
                2 -> {
                    clBatterySaver.visible()
                    clNetOptimization.gone()
                    clPhoneBooster.visible()
                    clCleaner.visible()
                    clBatterySaver.visible()
                }
                3 -> {
                    clBatterySaver.visible()
                    clNetOptimization.visible()
                    clPhoneBooster.gone()
                    clCleaner.visible()
                    clBatterySaver.visible()
                }
                4 -> {
                    clBatterySaver.visible()
                    clNetOptimization.visible()
                    clPhoneBooster.visible()
                    clCleaner.visible()
                    clCpuCooler.gone()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
