package com.tools.videodownloader.tools.photoeditor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityPicEditorHomeBinding
import com.tools.videodownloader.ui.activities.FullScreenActivity
import com.tools.videodownloader.ui.activities.PicEditActivity
import com.tools.videodownloader.ui.fragments.HomeFragment
import com.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.tools.videodownloader.utils.*
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils
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

            adjustInsetsBoth(this@PicEditorHomeActivity,
                {
                    toolbar.rlMain.topMargin = it
                }, {
                    rlMainTop.bottomMargin = it
                })

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@PicEditorHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@PicEditorHomeActivity,
                    RemoteConfigUtils.adIdNative(),
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