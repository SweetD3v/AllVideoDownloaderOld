package com.tools.videodownloader.tools.compress

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityPhotocmpHomeBinding
import com.tools.videodownloader.ui.activities.FullScreenActivity
import com.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.tools.videodownloader.utils.*
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import gun0912.tedimagepicker.builder.TedImagePicker.Companion.with

class PhotoCmpHomeActivity : FullScreenActivity() {
    companion object {
        const val SELECTED_PHOTO = "SELECTED_PHOTO"
    }

    val binding by lazy { ActivityPhotocmpHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@PhotoCmpHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@PhotoCmpHomeActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
            }

            toolbar.root.background = ContextCompat.getDrawable(
                this@PhotoCmpHomeActivity,
                R.drawable.top_bar_gradient_purple
            )
            toolbar.txtTitle.text = getString(R.string.photo_compress)

            adjustInsetsBoth(this@PhotoCmpHomeActivity,
                {
                    toolbar.rlMain.topMargin = it
                }, {
                    rlMainTop.bottomMargin = it
                })

            toolbar.imgBack.setOnClickListener { onBackPressed() }

            llPhotoCompress.setOnClickListener {
                with(this@PhotoCmpHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        val intent = Intent(
                            this@PhotoCmpHomeActivity,
                            CompressPhotoActivity::class.java
                        )
                        intent.putExtra(SELECTED_PHOTO, uri.toString())
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@PhotoCmpHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_cmp")
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