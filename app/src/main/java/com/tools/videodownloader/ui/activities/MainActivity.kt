package com.tools.videodownloader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.internet.speed_meter.SpeedMeterService
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityMainBinding
import com.tools.videodownloader.ui.fragments.HomeFragment
import com.tools.videodownloader.utils.AdsUtils
import com.tools.videodownloader.utils.NetworkState
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils

class MainActivity : BaseActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val tabTitles = arrayOf("Home", "Status Saver")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        startService(Intent(this, SpeedMeterService::class.java))

        Log.e("TAG", "interAdId: ${RemoteConfigUtils.adIdInterstital()}")

        binding.run {
            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@MainActivity, getString(R.string.banner_id_details),
                    bannerContainer
                )

            toolbar.imgBack.setImageResource(R.drawable.ic_drawer)

            fabEntertainment.setOnClickListener {
                startActivity(Intent(this@MainActivity, FunnyVideosActivity::class.java))
            }

            imgSettings.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        SettingsActivity::class.java
                    )
                )
            }

//            llHome.setOnClickListener {
//                viewPagerMain.currentItem = 0
//            }
//
//            llDownloads.setOnClickListener {
//                viewPagerMain.currentItem = 1
//            }

//            view1.setOnClickListener { v ->
//                motionLayout.transitionToState(
//                    R.id.initial,
//                    resources.getInteger(R.integer.navbar_motion_duration)
//                )
//                viewPagerMain.currentItem = 0
//            }
//
//            view2.setOnClickListener { v ->
//                motionLayout.transitionToState(
//                    R.id.stage1,
//                    resources.getInteger(R.integer.navbar_motion_duration)
//                )
//                viewPagerMain.currentItem = 1
//            }
//
//            view3.setOnClickListener { v ->
//                motionLayout.transitionToState(
//                    R.id.stage2,
//                    resources.getInteger(R.integer.navbar_motion_duration)
//                )
//                viewPagerMain.currentItem = 2
//            }
//
//            view4.setOnClickListener { v ->
//                motionLayout.transitionToState(
//                    R.id.stage3,
//                    resources.getInteger(R.integer.navbar_motion_duration)
//                )
//                viewPagerMain.currentItem = 3
//            }

            viewPagerMain.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPagerMain.isUserInputEnabled = false
            viewPagerMain.adapter = FragmentsAdapter(this@MainActivity)
//            viewPagerMain.registerOnPageChangeCallback(object :
//                ViewPager2.OnPageChangeCallback() {
//                override fun onPageScrolled(
//                    position: Int,
//                    positionOffset: Float,
//                    positionOffsetPixels: Int
//                ) {
////                    setBottomColor(position)
//                }
//            })

//            TabLayoutMediator(tabLayout, viewPagerMain) { tab, position ->
//                tab.text = tabTitles[position]
//            }.attach()
        }
    }

//    private fun setBottomColor(position: Int) {
//        when (position) {
//            0 -> {
//                binding.run {
//                    imgHome.imageTintList = ColorStateList.valueOf(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.primary
//                        )
//                    )
//                    txtHome.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.primary
//                        )
//                    )
//
//                    imgDownloads.imageTintList = ColorStateList.valueOf(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txtDownloads.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                }
//            }
//            1 -> {
//                binding.run {
//                    imgHome.imageTintList = ColorStateList.valueOf(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txtHome.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//
//                    imgDownloads.imageTintList = ColorStateList.valueOf(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.primary
//                        )
//                    )
//                    txtDownloads.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.primary
//                        )
//                    )
//                }
//            }
//        }
//    }

//    private fun setBottomTextColor(position: Int) {
//        binding.run {
//            when (position) {
//                0 -> {
//                    txt1.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.accent))
//                    txt2.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt3.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt4.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                }
//                1 -> {
//                    txt1.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt2.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.accent
//                        )
//                    )
//                    txt3.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt4.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                }
//                2 -> {
//                    txt1.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt2.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt3.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.accent
//                        )
//                    )
//                    txt4.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                }
//                3 -> {
//                    txt1.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt2.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt3.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.text_unselected
//                        )
//                    )
//                    txt4.setTextColor(
//                        ContextCompat.getColor(
//                            this@MainActivity,
//                            R.color.accent
//                        )
//                    )
//                }
//            }
//        }
//    }

    inner class FragmentsAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return 1
        }

        override fun createFragment(position: Int): Fragment {
            return HomeFragment.newInstance()
        }
    }

    var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }
        doubleBackToExitPressedOnce = true
        Snackbar.make(binding.root, "Press BACK again to exit", Snackbar.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}