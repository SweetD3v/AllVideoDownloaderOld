package com.video.tools.videodownloader.tools.photo_filters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityPhotoFilterHomeBinding
import com.video.tools.videodownloader.tools.cartoonify.CartoonActivity
import com.video.tools.videodownloader.ui.activities.FullScreenActivity
import com.video.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.video.tools.videodownloader.utils.*
import com.video.tools.videodownloader.utils.AdsUtils
import com.video.tools.videodownloader.utils.NetworkState
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import com.video.tools.videodownloader.utils.adjustInsetsBoth
import gun0912.tedimagepicker.builder.TedImagePicker

class PhotoFilterHomeActivity : FullScreenActivity() {
    val binding by lazy { ActivityPhotoFilterHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@PhotoFilterHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details),
//                )

                AdsUtils.loadNative(
                    this@PhotoFilterHomeActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
            }

            toolbar.txtTitle.text = getString(R.string.photo_filters)
            toolbar.root.background = ContextCompat.getDrawable(
                this@PhotoFilterHomeActivity,
                R.drawable.top_bar_gradient_light_blue1
            )

            adjustInsetsBoth(this@PhotoFilterHomeActivity,
                {
                    toolbar.rlMain.topMargin = it
                }, {
                    rlMainTop.bottomMargin = it
                })

            llPhotoFilters.setOnClickListener {
                TedImagePicker.with(this@PhotoFilterHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri? ->
                        startActivity(
                            Intent(this@PhotoFilterHomeActivity, PhotoFilterActivity::class.java)
                                .putExtra(CartoonActivity.SELECTED_PHOTO, uri.toString())
                        )
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@PhotoFilterHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_filter")
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