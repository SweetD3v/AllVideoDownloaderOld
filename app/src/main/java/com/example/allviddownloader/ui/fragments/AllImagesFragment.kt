package com.example.allviddownloader.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.allviddownloader.adapters.AllMediaAdapter
import com.example.allviddownloader.databinding.FragmentWaimagesBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.utils.dpToPx
import com.example.allviddownloader.utils.getMedia
import com.example.allviddownloader.widgets.MarginItemDecoration


class AllImagesFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }
    var imagesList = mutableListOf<Media>()

    companion object {
        open fun newInstance(): AllImagesFragment {
            return AllImagesFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadImages()
    }

    private fun loadImages() {
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            getMedia(ctx) { list ->
                if (imagesList.size != list.size) {
                    for (media in list) {
                        if (!media.isVideo) {
                            imagesList.add(media)
                        }
                    }
                    val allMediaAdapter = AllMediaAdapter(ctx, imagesList, binding.rlMain)
                    rvWAImages.adapter = allMediaAdapter
                }
            }
        }
    }

    override fun onBackPressed() {
    }
}