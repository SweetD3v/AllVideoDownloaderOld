package com.example.allviddownloader.tools.photo_filters

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityPhotoFilterBinding
import com.example.allviddownloader.tools.cartoonify.CartoonActivity
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.utils.*
import com.zomato.photofilters.SampleFilters

class PhotoFilterActivity : FullScreenActivity() {
    init {
        System.loadLibrary("NativeImageProcessor")
    }

    val binding by lazy { ActivityPhotoFilterBinding.inflate(layoutInflater) }
    val photoUri by lazy {
        intent.getStringExtra(CartoonActivity.SELECTED_PHOTO).toString().toUri()
    }
    var orgBitmap: Bitmap? = null
    var filterBmp: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rlMain.adjustInsets(this)

        binding.run {
            toolbar.txtTitle.text = getString(R.string.photo_filters)
            toolbar.imgSave.visible()
            toolbar.imgSave.setTextColor(Color.parseColor("#37E8FF"))
            toolbar.root.background = ContextCompat.getDrawable(
                this@PhotoFilterActivity,
                R.drawable.top_bar_gradient_light_blue1
            )
            toolbar.rlMain.adjustInsets(this@PhotoFilterActivity)
            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }
        }

        if (NetworkState.isOnline())
            AdsUtils.loadBanner(
                this, getString(R.string.banner_id_details),
                binding.bannerContainer
            )

        val bmp = getBitmapFromUri(this, photoUri)
        orgBitmap = bmp?.copy(bmp.config, true)
        filterBmp = orgBitmap

        binding.imgPhoto.setImageBitmap(orgBitmap)

        binding.rvFiltersList.layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.HORIZONTAL
        }

        binding.toolbar.imgSave.setOnClickListener {
            filterBmp?.let { filterBmp ->
                object : AsyncTaskRunner<Bitmap, String?>(this@PhotoFilterActivity) {
                    var pathStr: String = ""
                    override fun doInBackground(params: Bitmap?): String {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            saveBitmapImage(
                                this@PhotoFilterActivity,
                                params,
                                "IMG_FILTER_${System.currentTimeMillis()}",
                                "PhotoFilter"
                            ) {
                                pathStr = it
                            }
                        } else {
                            FileUtilsss.saveBitmapAsFileA10(
                                this@PhotoFilterActivity,
                                params,
                                "IMG_FILTER_${System.currentTimeMillis()}",
                                "PhotoFilter"
                            ) {
                                pathStr = it
                            }
                        }
                        return pathStr
                    }

                    override fun onPostExecute(result: String?) {
                        super.onPostExecute(result)
                        result?.let { path ->
                            MediaScannerConnection.scanFile(
                                this@PhotoFilterActivity, arrayOf(
                                    path
                                ), null
                            ) { path1: String?, uri1: Uri? -> }
                            Toast.makeText(
                                this@PhotoFilterActivity,
                                "Saved to: $path",
                                Toast.LENGTH_SHORT
                            ).show()

                            AdsUtils.loadInterstitialAd(this@PhotoFilterActivity,
                                getString(R.string.interstitial_id),
                                object : AdsUtils.Companion.FullScreenCallback() {
                                    override fun continueExecution() {
                                        PhotoFiltersUtils.photoFilterBmp = filterBmp
                                        startActivity(
                                            Intent(
                                                this@PhotoFilterActivity,
                                                PhotoFiltersSaveActivity::class.java
                                            )
                                                .putExtra("type", "filter")
                                        )
                                    }
                                })
                        }
                    }
                }.execute(filterBmp, true)
            }
        }

        loadAllFilters()
    }

    private fun loadAllFilters() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            val f1 = ThumbnailItem()
            val f2 = ThumbnailItem()
            val f3 = ThumbnailItem()
            val f4 = ThumbnailItem()
            val f5 = ThumbnailItem()
            val f6 = ThumbnailItem()

            f1.image = orgBitmap
            f2.image = orgBitmap
            f3.image = orgBitmap
            f4.image = orgBitmap
            f5.image = orgBitmap
            f6.image = orgBitmap
            ThumbnailsManager.clearThumbs()
            ThumbnailsManager.addThumb(f1) // Original Image


            f2.filter = SampleFilters.getStarLitFilter()
            ThumbnailsManager.addThumb(f2)

            f3.filter = SampleFilters.getBlueMessFilter()
            ThumbnailsManager.addThumb(f3)

            f4.filter = SampleFilters.getAweStruckVibeFilter()
            ThumbnailsManager.addThumb(f4)

            f5.filter = SampleFilters.getLimeStutterFilter()
            ThumbnailsManager.addThumb(f5)

            f6.filter = SampleFilters.getNightWhisperFilter()
            ThumbnailsManager.addThumb(f6)

            val thumbs: MutableList<ThumbnailItem> = ThumbnailsManager.processThumbs(this)
            val thumbnailsAdapter = ThumbnailsAdapter(this@PhotoFilterActivity, thumbs) { filter ->
                val bmp = filter.processFilter(orgBitmap)
                binding.imgPhoto.setImageBitmap(bmp)
                filterBmp = bmp
            }

            binding.rvFiltersList.adapter = thumbnailsAdapter
        }

        handler.post(runnable)
    }

    override fun onDestroy() {
        PhotoFiltersUtils.photoFilterBmp = null
        AdsUtils.destroyBanner()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }
}