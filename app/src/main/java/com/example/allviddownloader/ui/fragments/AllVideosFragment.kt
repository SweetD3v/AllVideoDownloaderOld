package com.example.allviddownloader.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.allviddownloader.adapters.AllMediaAdapter
import com.example.allviddownloader.adapters.WAMediaAdapter
import com.example.allviddownloader.databinding.FragmentWaimagesBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.utils.dpToPx
import com.example.allviddownloader.utils.getMedia
import com.example.allviddownloader.widgets.MarginItemDecoration


class AllVideosFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }
    var imagesList = mutableListOf<Media>()

    companion object {
        open fun newInstance(): AllVideosFragment {
            return AllVideosFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadVideos()
    }

    private fun loadVideos() {
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            getMedia(ctx) { list ->
                if (imagesList.size != list.size) {
                    getMedia(ctx) { list ->
                        if (imagesList.size != list.size) {
                            for (media in list) {
                                if (media.isVideo) {
                                    imagesList.add(media)
                                }
                            }
                            val allMediaAdapter = AllMediaAdapter(ctx, imagesList, binding.rlMain)
                            rvWAImages.adapter = allMediaAdapter
                        }
                    }
                    val waMediaAdapter = WAMediaAdapter(ctx, imagesList)
                    rvWAImages.adapter = waMediaAdapter
                }
            }
        }
    }

    override fun onBackPressed() {
    }
}