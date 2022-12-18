package com.video.tools.videodownloader.tools.photo_filters.deform

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityPhotoWarpHomeBinding
import com.video.tools.videodownloader.tools.cartoonify.CartoonActivity.Companion.SELECTED_PHOTO
import com.video.tools.videodownloader.ui.activities.FullScreenActivity
import com.video.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.video.tools.videodownloader.utils.*
import com.video.tools.videodownloader.utils.AdsUtils
import com.video.tools.videodownloader.utils.NetworkState
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import com.video.tools.videodownloader.utils.adjustInsetsBoth
import gun0912.tedimagepicker.builder.TedImagePicker

class PhotoWarpHomeActivity : FullScreenActivity() {

    val binding by lazy { ActivityPhotoWarpHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@PhotoWarpHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@PhotoWarpHomeActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
            }

            toolbar.txtTitle.text = getString(R.string.photo_warp)
            toolbar.root.background = ContextCompat.getDrawable(
                this@PhotoWarpHomeActivity,
                R.drawable.top_bar_gradient_orange
            )

            adjustInsetsBoth(this@PhotoWarpHomeActivity,
                {
                    toolbar.rlMain.topMargin = it
                }, {
                    rlMainTop.bottomMargin = it
                })

            llPhotoFilters.setOnClickListener {
                TedImagePicker.with(this@PhotoWarpHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri? ->
                        startActivity(
                            Intent(this@PhotoWarpHomeActivity, PhotoWarpActivity::class.java)
                                .putExtra(SELECTED_PHOTO, uri.toString())
                        )
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@PhotoWarpHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_warp")
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }
}