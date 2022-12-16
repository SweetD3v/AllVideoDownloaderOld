package com.tools.videodownloader.ui.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tools.videodownloader.R
import com.tools.videodownloader.adapters.WAMediaSavedAdapter
import com.tools.videodownloader.databinding.FragmentWaimagesBinding
import com.tools.videodownloader.models.Media
import com.tools.videodownloader.utils.RootDirectoryWhatsappShow
import com.tools.videodownloader.utils.addOuterGridSpacing
import com.tools.videodownloader.utils.getMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WADownloadsFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }

    var imagesList: MutableList<Media> = mutableListOf()
    var waMediaAdapter: WAMediaSavedAdapter? = null
    var decorationAdded: Boolean? = false

    var job = Job()
    var ioScope = CoroutineScope(Dispatchers.IO + job)
    var uiScope = CoroutineScope(Dispatchers.Main + job)

    companion object {
        open fun newInstance(): WADownloadsFragment {
            return WADownloadsFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {

//            AdsUtils.loadNative(
//                requireActivity(),
//                getString(R.string.admob_native_id),
//                adFrame
//            )

            rvWAImages.isNestedScrollingEnabled = false
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
//            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val albumGridSpacing = resources.getDimension(R.dimen.rv_margin)
            if (decorationAdded == false) {
                decorationAdded = true
                rvWAImages.addOuterGridSpacing((albumGridSpacing).toInt())
                rvWAImages.addItemDecoration(GridMarginDecoration(albumGridSpacing.toInt()))
            }

            waMediaAdapter = WAMediaSavedAdapter(ctx, imagesList)
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


    override fun onResume() {
        super.onResume()

//        if (allPermissionsGranted()) {
//            onPermissionGranted()
//        } else {
//            permissionRequest.launch(permissions.toTypedArray())
//        }

        loadImages()
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
                getMedia(ctx, RootDirectoryWhatsappShow) { list ->
                    imageListNew =
                        list.filter { !it.path.contains(".noMedia", true) }.toMutableList()
                    if (imagesList.size != imageListNew.size) {
                        uiScope.launch {
                            imagesList = imageListNew
                            waMediaAdapter?.mediaList = imagesList
                            waMediaAdapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }
}