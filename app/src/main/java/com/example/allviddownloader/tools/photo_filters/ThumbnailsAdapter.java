package com.example.allviddownloader.tools.photo_filters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allviddownloader.databinding.ListThumbnailItemBinding;

import java.util.List;

public class ThumbnailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "THUMBNAILS_ADAPTER";
    private static int lastPosition = -1;
    private ThumbnailCallback thumbnailCallback;
    private List<ThumbnailItem> dataSet;

    public ThumbnailsAdapter(List<ThumbnailItem> dataSet, ThumbnailCallback thumbnailCallback) {
        Log.v(TAG, "Thumbnails Adapter has " + dataSet.size() + " items");
        this.dataSet = dataSet;
        this.thumbnailCallback = thumbnailCallback;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Log.v(TAG, "On Create View Holder Called");
        return new ThumbnailsViewHolder(ListThumbnailItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ThumbnailItem thumbnailItem = dataSet.get(holder.getAdapterPosition());
        Log.v(TAG, "On Bind View Called");
        ThumbnailsViewHolder thumbnailsViewHolder = (ThumbnailsViewHolder) holder;
        ListThumbnailItemBinding binding = thumbnailsViewHolder.binding;
        binding.thumbnail.setImageBitmap(thumbnailItem.getImage());
        binding.thumbnail.setScaleType(ImageView.ScaleType.FIT_START);
        setAnimation(holder.getAdapterPosition());
        binding.getRoot().setOnClickListener((View.OnClickListener) v -> {
            if (lastPosition != holder.getAdapterPosition()) {
                thumbnailCallback.onThumbnailClick(thumbnailItem.getFilter());
                lastPosition = holder.getAdapterPosition();
            }
        });
    }

    private void setAnimation(int position) {
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ThumbnailsViewHolder extends RecyclerView.ViewHolder {
        ListThumbnailItemBinding binding;

        public ThumbnailsViewHolder(ListThumbnailItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}