package com.example.allviddownloader.tools.photoeditor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityPicEditorHomeBinding
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.ui.activities.PicEditActivity
import com.example.allviddownloader.ui.fragments.HomeFragment
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.adjustInsets
import gun0912.tedimagepicker.builder.TedImagePicker

class PicEditorHomeActivity : FullScreenActivity() {
    val binding by lazy { ActivityPicEditorHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            toolbar.txtTitle.text = getString(R.string.photo_editor)
            toolbar.root.background = ContextCompat.getDrawable(
                this@PicEditorHomeActivity,
                R.drawable.top_bar_gradient_yellow
            )

            toolbar.rlMain.adjustInsets(this@PicEditorHomeActivity)

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@PicEditorHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@PicEditorHomeActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            llEdit.setOnClickListener {
                TedImagePicker.with(this@PicEditorHomeActivity)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        Log.e("TAG", "onCreatePic: $uri")
                        val intent = Intent(
                            this@PicEditorHomeActivity,
                            PicEditActivity::class.java
                        )
                        intent.putExtra(HomeFragment.KEY_SELECTED_PHOTOS, uri.toString())
                        startActivity(intent)
                    }
            }

            llMyCreation.setOnClickListener {
                startActivity(
                    Intent(
                        this@PicEditorHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_editor")
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