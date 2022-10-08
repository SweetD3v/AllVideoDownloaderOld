package com.example.allviddownloader.tools.compress

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityCompressVideoHomeBinding
import com.example.allviddownloader.tools.cartoonify.CartoonActivity
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import gun0912.tedimagepicker.builder.TedImagePicker

class CompressVideoHomeActivity : BaseActivity() {
    val binding by lazy { ActivityCompressVideoHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@CompressVideoHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@CompressVideoHomeActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            imgBack.setOnClickListener { onBackPressed() }

            llPhotoCompress.setOnClickListener {
                TedImagePicker.with(this@CompressVideoHomeActivity)
                    .dropDownAlbum()
                    .video()
                    .imageCountTextFormat("%s videos")
                    .start { uri: Uri ->
                        val intent = Intent(
                            this@CompressVideoHomeActivity,
                            CompressVideoActivity::class.java
                        )
                        intent.putExtra(CartoonActivity.SELECTED_PHOTO, uri.toString())
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@CompressVideoHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "video_cmp")
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