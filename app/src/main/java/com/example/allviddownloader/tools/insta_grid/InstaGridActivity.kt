package com.example.allviddownloader.tools.insta_grid

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityInstaGridBinding
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.adjustInsets
import com.example.allviddownloader.utils.getBitmapFromUri
import com.yalantis.ucrop.UCrop
import gun0912.tedimagepicker.builder.TedImagePicker
import java.io.File

class InstaGridActivity : FullScreenActivity() {
    val CROPPED_IMAGE_NAME = "CroppedImage"

    val binding by lazy { ActivityInstaGridBinding.inflate(layoutInflater) }
    val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val destinationFileName = "${CROPPED_IMAGE_NAME}.jpg"

        val uCrop = UCrop.of(uri!!, Uri.fromFile(File(cacheDir, destinationFileName)))
        val options = UCrop.Options()
        uCrop.withOptions(options)
        uCrop.start(this, cropLauncher)
    }

    var bmp: Bitmap? = null

    val cropLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.data != null) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    bmp = getBitmapFromUri(this, resultUri)

                    bmp?.let {
                        GridUtils.bitmaps = GridUtils.splitBitmap(this, it)

                        startActivity(Intent(this, GridSaveActivity::class.java))
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            binding.toolbar.root.background = ContextCompat.getDrawable(
                this@InstaGridActivity,
                R.drawable.top_bar_gradient_pink1
            )

            toolbar.rlMain.adjustInsets(this@InstaGridActivity)

            toolbar.txtTitle.text = getString(R.string.insta_grid)

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@InstaGridActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )
                AdsUtils.loadNativeSmall(
                    this@InstaGridActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            imgSelectImage.setOnClickListener {
                TedImagePicker.with(this@InstaGridActivity)
                    .showCameraTile(false)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        val destinationFileName = "${CROPPED_IMAGE_NAME}.jpg"

                        val uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, destinationFileName)))
                        val options = UCrop.Options()
                        options.withAspectRatio(1f, 1f)
                        uCrop.withOptions(options)
                        uCrop.start(this@InstaGridActivity, cropLauncher)
                    }
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