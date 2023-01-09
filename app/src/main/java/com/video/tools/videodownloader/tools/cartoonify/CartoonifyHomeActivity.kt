package com.video.tools.videodownloader.tools.cartoonify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityCartoonifyHomeBinding
import com.video.tools.videodownloader.ui.activities.FullScreenActivity
import com.video.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.video.tools.videodownloader.utils.AdsUtils
import com.video.tools.videodownloader.utils.NetworkState
import com.video.tools.videodownloader.utils.adjustInsets
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import gun0912.tedimagepicker.builder.TedImagePicker

class CartoonifyHomeActivity : FullScreenActivity() {

    val binding by lazy { ActivityCartoonifyHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@CartoonifyHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@CartoonifyHomeActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
            }

            toolbar.root.background = ContextCompat.getDrawable(
                this@CartoonifyHomeActivity,
                R.drawable.top_bar_gradient_orange
            )

            toolbar.rlMain.adjustInsets(this@CartoonifyHomeActivity)

            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            llCartoonify.setOnClickListener {
                TedImagePicker.with(this@CartoonifyHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        val intent = Intent(
                            this@CartoonifyHomeActivity,
                            CartoonActivity::class.java
                        )
                        intent.putExtra(CartoonActivity.SELECTED_PHOTO, uri.toString())
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@CartoonifyHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "cartoonify")
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