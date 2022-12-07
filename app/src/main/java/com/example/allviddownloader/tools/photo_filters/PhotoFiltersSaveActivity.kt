package com.example.allviddownloader.tools.photo_filters

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityPhotoFiltersSaveBinding
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.adjustInsets

class PhotoFiltersSaveActivity : FullScreenActivity() {
    val binding by lazy { ActivityPhotoFiltersSaveBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (NetworkState.isOnline()) {
//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )

            AdsUtils.loadNative(
                this@PhotoFiltersSaveActivity,
                getString(R.string.admob_native_id),
                binding.adFrame
            )
        }

        binding.run {
            toolbar.txtTitle.text = getString(R.string.photo_warp)
            toolbar.root.background = ContextCompat.getDrawable(
                this@PhotoFiltersSaveActivity,
                R.drawable.top_bar_gradient_orange
            )
            toolbar.rlMain.adjustInsets(this@PhotoFiltersSaveActivity)
            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }
        }

        binding.preview.setImageBitmap(PhotoFiltersUtils.photoFilterBmp)

        binding.btnMyCreation.setOnClickListener {
            if (intent.getStringExtra("type") == "filter") {
                startActivity(
                    Intent(
                        this@PhotoFiltersSaveActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_filter")
                    }
                )
            } else {
                startActivity(
                    Intent(
                        this@PhotoFiltersSaveActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_warp")
                    }
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