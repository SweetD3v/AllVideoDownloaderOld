package com.tools.videodownloader.tools.photo_filters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tools.videodownloader.R;
import com.tools.videodownloader.databinding.ListThumbnailItemBinding;

import java.util.List;

public class ThumbnailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "THUMBNAILS_ADAPTER";
    private int lastPosition = 0;
    private ThumbnailCallback thumbnailCallback;
    private List<ThumbnailItem> dataSet;
    Context context;

    public ThumbnailsAdapter(Context context, List<ThumbnailItem> dataSet, ThumbnailCallback thumbnailCallback) {
        this.context = context;
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
        final ThumbnailItem thumbnailItem = dataSet.get(holder.getBindingAdapterPosition());

        ThumbnailsViewHolder thumbnailsViewHolder = (ThumbnailsViewHolder) holder;
        ListThumbnailItemBinding binding = thumbnailsViewHolder.binding;
        binding.thumbnail.setImageBitmap(thumbnailItem.getImage());
        binding.thumbnail.setScaleType(ImageView.ScaleType.FIT_START);

        if (lastPosition == holder.getBindingAdapterPosition()) {
            binding.llRoot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
        } else {
            binding.llRoot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
        }

        binding.getRoot().setOnClickListener((View.OnClickListener) v -> {
            if (lastPosition != holder.getBindingAdapterPosition()) {
                binding.llRoot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
                notifyItemChanged(lastPosition, new Object());
                thumbnailCallback.onThumbnailClick(thumbnailItem.getFilter());
                lastPosition = holder.getBindingAdapterPosition();
            }
        });
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