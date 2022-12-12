package com.tools.videodownloader.ui.fragments

import android.Manifest
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tools.videodownloader.R
import com.tools.videodownloader.adapters.WAMediaAdapter
import com.tools.videodownloader.databinding.FragmentWaimagesBinding
import com.tools.videodownloader.models.Media
import com.tools.videodownloader.utils.addOuterGridSpacing
import com.tools.videodownloader.utils.getMediaWACoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class WAImagesFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }
    var decorationAdded: Boolean? = false

    var job = Job()
    var ioScope = CoroutineScope(Dispatchers.IO + job)
    var uiScope = CoroutineScope(Dispatchers.Main + job)

    private val PERMISSIONS = mutableListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    val permissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var granted = true
            if (result != null) {
                for (b in result.values) {
                    if (!b) {
                        granted = false
                        break
                    }
                }
            } else granted = false

        }

    val statusFileResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent = result.data!!
                Log.d("HEY: ", data.data.toString())
                ctx.contentResolver.takePersistableUriPermission(
                    data.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Toast.makeText(ctx, "Success", Toast.LENGTH_SHORT).show()
                onPermissionGranted()
            }
        }

    var waMediaAdapter: WAMediaAdapter? = null

    companion object {
        var imagesList: MutableList<Media> = mutableListOf()
        open fun newInstance(): WAImagesFragment {
            return WAImagesFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
//            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val albumGridSpacing = resources.getDimension(R.dimen.rv_margin)
            if (decorationAdded == false) {
                decorationAdded = true
                rvWAImages.addOuterGridSpacing((albumGridSpacing).toInt())
                rvWAImages.addItemDecoration(GridMarginDecoration(albumGridSpacing.toInt()))
            }

            waMediaAdapter = WAMediaAdapter(ctx, imagesList)
            binding.rvWAImages.adapter = waMediaAdapter
        }
    }

    internal class GridMarginDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = space / 2
            outRect.top = space / 2
            outRect.right = space / 2
            outRect.bottom = space / 2
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun openDocTreeStatus() {
        Log.e("TAG", "requestPermissionQ: ")
        val createOpenDocumentTreeIntent =
            (ctx.getSystemService(AppCompatActivity.STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
        val replace: String =
            (createOpenDocumentTreeIntent.getParcelableExtra<Parcelable>(DocumentsContract.EXTRA_INITIAL_URI) as Uri?).toString()
                .replace("/root/", "/document/")
        val parse: Uri =
            Uri.parse("$replace%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")
        Log.d("URI", parse.toString())
        createOpenDocumentTreeIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse)
        statusFileResultLauncher.launch(createOpenDocumentTreeIntent)
    }


    override fun onResume() {
        super.onResume()

//        if (allPermissionsGranted()) {
//            onPermissionGranted()
//        } else {
//            permissionRequest.launch(permissions.toTypedArray())
//        }
        if (!allPermissionsGranted() || ctx.contentResolver.persistedUriPermissions.size <= 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && ctx.contentResolver.persistedUriPermissions.size <= 0
            ) {
                openDocTreeStatus()
            } else {
                if (!allPermissionsGranted())
                    permissionsLauncher.launch(PERMISSIONS.toTypedArray())
                else onPermissionGranted()
            }
        }else{
            onPermissionGranted()
        }
    }

    override fun onPermissionGranted() {
        loadImages()
    }

    private fun loadImages() {
        binding.apply {
            job = Job()
            ioScope = CoroutineScope(Dispatchers.IO + job)
            uiScope = CoroutineScope(Dispatchers.Main + job)

            var imageListNew: MutableList<Media>
            ioScope.launch {
                getMediaWACoroutine(ctx) { list ->
                    Log.e("TAG", "loadImages: ${list.size}")
                    imageListNew =
                        list.filter { !it.path.contains(".noMedia", true) }.toMutableList()
                    imagesList = imageListNew

                    uiScope.launch {
                        waMediaAdapter?.updateEmployeeListItems(imagesList)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }
}
