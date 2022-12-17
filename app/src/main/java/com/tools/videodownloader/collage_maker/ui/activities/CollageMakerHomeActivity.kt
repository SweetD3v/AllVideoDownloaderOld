package com.tools.videodownloader.collage_maker.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityCollageMakerHomeBinding
import com.tools.videodownloader.ui.activities.FullScreenActivity
import com.tools.videodownloader.ui.fragments.HomeFragment
import com.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.tools.videodownloader.utils.*
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import gun0912.tedimagepicker.builder.TedImagePicker

class CollageMakerHomeActivity : FullScreenActivity() {

    val binding by lazy { ActivityCollageMakerHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            toolbar.txtTitle.text = getString(R.string.collage_maker)
            toolbar.root.background = ContextCompat.getDrawable(
                this@CollageMakerHomeActivity,
                R.drawable.top_bar_gradient_purple
            )

            adjustInsetsBoth(this@CollageMakerHomeActivity, { marginTop ->
                toolbar.rlMain.topMargin = marginTop
            }, { marginBottom ->
                Log.e("TAG", "marginBottom: ${pxToDp(llBottom.marginBottom)}")
                rlRoot.bottomMargin = marginBottom
            })

            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@CollageMakerHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )
                AdsUtils.loadNative(
                    this@CollageMakerHomeActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
            }

            llCollageMaker.setOnClickListener {
                TedImagePicker.with(this@CollageMakerHomeActivity)
                    .dropDownAlbum()
                    .min(2, "Select at least 2 images")
                    .max(9, "You can't select more than 9 images")
                    .imageCountTextFormat("%s images")
                    .startMultiImage { list ->
                        val paths = ArrayList<String>()
                        for (i in list.indices) {
                            paths.add(list[i].toString())
                        }

                        val intent =
                            Intent(this@CollageMakerHomeActivity, CollageViewActivity::class.java)
                        intent.putStringArrayListExtra(
                            HomeFragment.KEY_DATA_RESULT,
                            paths
                        )
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@CollageMakerHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "collage_maker")
                )
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}