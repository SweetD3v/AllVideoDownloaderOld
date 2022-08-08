package com.example.allviddownloader.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.allviddownloader.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    abstract val binding: VB
    lateinit var ctx: Context

    open val permissions = mutableListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    open val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                onPermissionGranted()
            } else {
                view?.let { v ->
                    Snackbar.make(v, R.string.message_no_permissions, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.label_ok) {
                        }
                        .show()
                }
            }
        }

    open fun onPermissionGranted() = Unit

    protected fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })
        return binding.root
    }

//    open fun getMedia(): MutableList<Media> {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val mediaList = mutableListOf<Media>()
//            val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                MediaStore.MediaColumns.RELATIVE_PATH + " LIKE ? "
//            } else {
//                MediaStore.Images.Media.DATA + " LIKE ? "
//            }
//            val selectionArgs = arrayOf("%Android/media/com.whatsapp/WhatsApp/Media/.Statuses%")
//            val contentResolver = ctx.applicationContext.contentResolver
//            contentResolver.query(
//                MediaStore.Files.getContentUri("external"),
//                null,
//                selection,
//                selectionArgs,
//                "${MediaStore.Video.Media.DATE_TAKEN} DESC"
//            )?.use { cursor ->
//                while (cursor.moveToNext()) {
//                    val imageCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
//                    val path =
//                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
//                    val date =
//                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
//                    val pathId = cursor.getString(imageCol)
//                    val uri = Uri.parse(pathId)
//                    val contentUri = if (uri.toString().endsWith(".mp4")) {
//                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
//                    } else {
//                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//                    }
//                    val media = Media(contentUri, uri.toString().endsWith(".mp4"), date)
//                    mediaList.add(media)
//                }
//            }
//            Log.e("TAG", "getMedia: ${mediaList.size}")
//
//            mediaList.sortByDescending { it.date }
//            return mediaList
//        }
//        return mutableListOf()
//    }

    abstract fun onBackPressed()
}