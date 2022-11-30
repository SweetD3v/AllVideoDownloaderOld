package com.example.allviddownloader.tools.photo_filters.deform

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityPhotoWarpHomeBinding
import com.example.allviddownloader.tools.cartoonify.CartoonActivity.Companion.SELECTED_PHOTO
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.adjustInsets
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
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            toolbar.txtTitle.text = getString(R.string.photo_warp)
            toolbar.root.background = ContextCompat.getDrawable(
                this@PhotoWarpHomeActivity,
                R.drawable.top_bar_gradient_orange
            )

            toolbar.rlMain.adjustInsets(this@PhotoWarpHomeActivity)

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