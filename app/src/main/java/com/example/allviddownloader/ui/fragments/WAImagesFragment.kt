package com.example.allviddownloader.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.example.allviddownloader.adapters.WAMediaAdapter
import com.example.allviddownloader.databinding.FragmentWaimagesBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.utils.*
import com.example.allviddownloader.widgets.MarginItemDecoration
import java.util.concurrent.Executors


class WAImagesFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }

    companion object {
        var imagesList: MutableList<Media> = mutableListOf()

        open fun newInstance(): WAImagesFragment {
            return WAImagesFragment()
        }

        open fun newInstance(mediaList: MutableList<Media>): WAImagesFragment {
            imagesList = mediaList
            Log.e("TAG", "newInstanceI: ${imagesList.size}")
            return WAImagesFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (allPermissionsGranted()) {
//            onPermissionGranted()
//        } else {
//            permissionRequest.launch(permissions.toTypedArray())
//        }
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            Log.e("TAG", "onPermissionGranted: ${imagesList.size}")
            waMediaAdapter = WAMediaAdapter(ctx, imagesList, binding.rlMain)
            binding.rvWAImages.adapter = waMediaAdapter
            waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
        }
    }

    lateinit var waMediaAdapter: WAMediaAdapter
//    override fun onPermissionGranted() {
//        binding.run {
//            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
//            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
//
//            Log.e("TAG", "onPermissionGranted: ${imagesList.size}")
//            waMediaAdapter = WAMediaAdapter(ctx, imagesList, binding.rlMain)
//            binding.rvWAImages.adapter = waMediaAdapter
//            waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
//        }
//        if (allPermissionsGranted()) {
//            onPermissionGranted()
//        } else {
//            permissionRequest.launch(permissions.toTypedArray())
//        }
//    }

    override fun onPermissionGranted() {
        loadImages()
    }

    private fun loadImages() {
        binding.apply {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val imageListNew = mutableListOf<Media>()
            getMediaWA(ctx) { list ->
                for (media in list) {
                    if (!media.isVideo and !media.uri.toString().contains(".nomedia", true)
                    ) {
                        imageListNew.add(media)
                    }
                }
                if (imageListNew.size != imagesList.size) {
                    imagesList = imageListNew
                    val waMediaAdapter = WAMediaAdapter(ctx, imagesList, binding.rlMain)
                    binding.rvWAImages.adapter = waMediaAdapter
                    waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
                }
            }
        }
    }

    fun refreshImages() {
        waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
    }

    override fun onBackPressed() {
        requireActivity().finish()
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

}