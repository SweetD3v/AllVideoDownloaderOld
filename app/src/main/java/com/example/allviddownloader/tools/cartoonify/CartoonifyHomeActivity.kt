package com.example.allviddownloader.tools.cartoonify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityCartoonifyHomeBinding
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.adjustInsetsBottom
import com.example.allviddownloader.utils.adjustInsets
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
                    getString(R.string.admob_native_id),
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