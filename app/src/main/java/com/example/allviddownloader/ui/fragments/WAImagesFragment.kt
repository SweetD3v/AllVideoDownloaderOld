package com.example.allviddownloader.ui.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.example.allviddownloader.adapters.WAMediaAdapter
import com.example.allviddownloader.databinding.FragmentWaimagesBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.utils.AppUtils
import com.example.allviddownloader.utils.dpToPx
import com.example.allviddownloader.utils.getBitmapFromUri
import com.example.allviddownloader.utils.getMediaQMinus
import com.example.allviddownloader.widgets.MarginItemDecoration
import java.util.concurrent.Executors


class WAImagesFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }
    var imagesList = mutableListOf<Media>()

    companion object {
        open fun newInstance(): WAImagesFragment {
            return WAImagesFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            onPermissionGranted()
        } else {
            permissionRequest.launch(permissions.toTypedArray())
        }
    }

    override fun onPermissionGranted() {
        loadImages()
    }

    private fun loadImages() {
        binding.apply {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                executeNew()
            } else if (AppUtils.STATUS_DIRECTORY.exists()) {
                executeOld()
            } else {
                Toast.makeText(activity, "Can't find Whatsapp", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun executeOld() {
        val handler = Handler(Looper.getMainLooper())
        imagesList.clear()
        if (AppUtils.STATUS_DIRECTORY.exists()) {
            val imagesListNew = getMediaQMinus(ctx, AppUtils.STATUS_DIRECTORY)
            for (media in imagesListNew) {
                if (!media.isVideo) {
                    imagesList.add(media)
                }
            }
            Log.e("TAG", "executeOld: ${imagesList}")
            handler.post {
                val WAMediaAdapter = WAMediaAdapter(ctx, imagesList, binding.rlMain)
                binding.rvWAImages.adapter = WAMediaAdapter
                WAMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
            }
        }
    }

    private fun executeNew() {
        Executors.newSingleThreadExecutor().execute { loadImagesA30() }
    }

    fun loadImagesA30() {
        val handler = Handler(Looper.getMainLooper())
        val fromTreeUri = DocumentFile.fromTreeUri(
            ctx,
            ctx.contentResolver.persistedUriPermissions[0].uri
        )
        Log.e("TAG", "loadImagesA30: ${fromTreeUri}")
        val imagesListNew = mutableListOf<Media>()
        if (fromTreeUri == null) {
            handler.post { }
            return
        }
        val listFiles = fromTreeUri.listFiles()
        if (listFiles.isEmpty()) {
            handler.post { }
            return
        }
        for (documentFile in listFiles) {
            val uri = documentFile.uri
            Log.e("TAG", "loadImagesA30: ${getBitmapFromUri(ctx, uri)?.width}")
            val status = Media(
                uri,
                uri.toString(),
                ctx.contentResolver.getType(documentFile.uri)!!.contains("video"),
                documentFile.lastModified()
            )
            if (!status.isVideo) {
                if (!status.uri.toString().contains(".nomedia"))
                    imagesListNew.add(status)
            }
        }
        Log.e("HEYIMG: ", "${imagesList.size}")
        Log.e("HEYIMG1: ", "${imagesListNew.size}")
        if (imagesListNew.isNotEmpty()) {
            if (imagesListNew.size != imagesList.size) {
                imagesList = imagesListNew
                handler.post { refreshAdapter() }
            }
        }
    }

//    fun loadImagesA30() {
//        val handler = Handler(Looper.getMainLooper())
//        imagesList.clear()
//        imagesList = getMedia()
//        handler.post { refreshAdapter() }
//    }

    fun refreshAdapter() {
        val WAMediaAdapter = WAMediaAdapter(ctx, imagesList, binding.rlMain)
        binding.rvWAImages.adapter = WAMediaAdapter
        WAMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
    }

    override fun onBackPressed() {
        TODO("Not yet implemented")
    }
}