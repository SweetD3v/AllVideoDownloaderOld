package com.example.allviddownloader.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.allviddownloader.databinding.ActivityWallpaperDetailsBinding
import com.example.allviddownloader.ui.activities.WallpapersActivity.Companion.WALLPAPER_ORIGINAL_URL

class WallpapersDetailsActivity : BaseActivity() {
    val binding by lazy { ActivityWallpaperDetailsBinding.inflate(layoutInflater) }
    var isAllVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadWallpaper()

        binding.run {
            fabMore.setOnClickListener {
                isAllVisible = if (!isAllVisible) {
                    fabDownload.show()
                    fabShare.show()
                    txtDownload.visibility = View.VISIBLE
                    txtShare.visibility = View.VISIBLE

                    true
                } else {
                    fabDownload.hide()
                    fabShare.hide()
                    txtDownload.visibility = View.GONE
                    txtShare.visibility = View.GONE

                    false
                }
            }
        }
    }

    private fun loadWallpaper() {
        if (intent.hasExtra(WALLPAPER_ORIGINAL_URL))
            Log.e("TAG", "loadWallpaper: ${intent.getStringExtra(WALLPAPER_ORIGINAL_URL)}")
    }
}