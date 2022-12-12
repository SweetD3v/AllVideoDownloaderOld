package com.tools.videodownloader.tools.photo_filters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityPhotoFilterHomeBinding
import com.tools.videodownloader.tools.cartoonify.CartoonActivity
import com.tools.videodownloader.ui.activities.FullScreenActivity
import com.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.tools.videodownloader.utils.AdsUtils
import com.tools.videodownloader.utils.NetworkState
import com.tools.videodownloader.utils.adjustInsets
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
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            toolbar.txtTitle.text = getString(R.string.photo_filters)
            toolbar.root.background = ContextCompat.getDrawable(
                this@PhotoFilterHomeActivity,
                R.drawable.top_bar_gradient_light_blue1
            )
            toolbar.rlMain.adjustInsets(this@PhotoFilterHomeActivity)

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