package com.video.tools.videodownloader.ui.activities

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityMainBinding
import com.video.tools.videodownloader.ui.fragments.HomeFragment
import com.video.tools.videodownloader.utils.AdsUtils
import com.video.tools.videodownloader.utils.NetworkState
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils

class MainActivity : BaseActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val tabTitles = arrayOf("Home", "Status Saver")
    val permissionsList = arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { list: Map<String, Boolean> ->
            val granted = list.all { it.value }

            if (!granted) {
                if (shouldShowRequestPermissionRationale(permissionsList[0])) {
                    askForStoragePermission()
                } else {
                    showPermissionDialog()
                }
            }
        }

    fun askForStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            storagePermissionLauncher.launch(permissionsList.toTypedArray())
        }
    }

    var permissionDialog: AlertDialog? = null

    fun showPermissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required")
            .setCancelable(false)
            .setMessage("Some permissions are needed to be allowed to use this app without any problems.")
            .setPositiveButton("Settings") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package", packageName,
                    null
                )
                intent.data = uri
                startActivity(intent)
            }
        if (permissionDialog == null)
            permissionDialog = builder.create()
        if ((permissionDialog?.isShowing != true)) {
            permissionDialog?.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configuration: Configuration = resources.configuration
        configuration.fontScale = 1f //0.85 small size, 1 normal size, 1,15 big etc

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        configuration.densityDpi = resources.displayMetrics.xdpi.toInt()
        baseContext.resources.updateConfiguration(configuration, metrics)

        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@MainActivity, RemoteConfigUtils.adIdBanner(),
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

    override fun onResume() {
        super.onResume()

        askForStoragePermission()
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
//        if (doubleBackToExitPressedOnce) {
//            finish()
//            return
//        }
//        doubleBackToExitPressedOnce = true
//        Snackbar.make(binding.root, "Press BACK again to exit", Snackbar.LENGTH_SHORT).show()
//        Handler(Looper.getMainLooper()).postDelayed({
//            doubleBackToExitPressedOnce = false
//        }, 2000)

        if (NetworkState.isOnline()) {
            AdsUtils.loadInterstitialAd(
                this,
                RemoteConfigUtils.adIdInterstital(),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        startActivity(Intent(this@MainActivity, ExitActivity::class.java))
                        finish()
                    }
                })
        } else {
            startActivity(Intent(this, ExitActivity::class.java))
            finish()
        }
    }
}