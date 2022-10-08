package com.example.allviddownloader.tools.photo_filters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityPhotoFilterHomeBinding
import com.example.allviddownloader.tools.cartoonify.CartoonActivity
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import gun0912.tedimagepicker.builder.TedImagePicker

class PhotoFilterHomeActivity : BaseActivity() {
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