package com.example.allviddownloader.ui.activities

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.security.NetworkSecurityPolicy
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityMainBinding
import com.example.allviddownloader.ui.fragments.DownloadsFragment
import com.example.allviddownloader.ui.fragments.HomeFragment
import com.example.allviddownloader.ui.fragments.WAStatusFragment
import com.example.allviddownloader.utils.setLightStatusBarColor

class MainActivity : BaseActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val tabTitles = arrayOf("Home", "Status Saver")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.imgBack.setImageResource(R.drawable.ic_drawer)

            llHome.setOnClickListener {
                viewPagerMain.currentItem = 0
            }

            llDownloads.setOnClickListener {
                viewPagerMain.currentItem = 1
            }

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
            viewPagerMain.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
//                    setBottomTextColor(position)
                    setBottomColor(position)
                }
            })

//            TabLayoutMediator(tabLayout, viewPagerMain) { tab, position ->
//                tab.text = tabTitles[position]
//            }.attach()
        }
    }

    private fun setBottomColor(position: Int) {
        when (position) {
            0 -> {
                binding.run {
                    imgHome.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.primary
                        )
                    )
                    txtHome.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.primary
                        )
                    )

                    imgDownloads.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.text_unselected
                        )
                    )
                    txtDownloads.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.text_unselected
                        )
                    )
                }
            }
            1 -> {
                binding.run {
                    imgHome.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.text_unselected
                        )
                    )
                    txtHome.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.text_unselected
                        )
                    )

                    imgDownloads.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.primary
                        )
                    )
                    txtDownloads.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.primary
                        )
                    )
                }
            }
        }
    }

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
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> {
                    return HomeFragment.newInstance()
                }
                1 -> {
                    return DownloadsFragment.newInstance()
                }
                2 -> {
                    return HomeFragment.newInstance()
                }
                3 -> {
                    return WAStatusFragment.newInstance()
                }
            }
            return HomeFragment.newInstance()
        }
    }
}