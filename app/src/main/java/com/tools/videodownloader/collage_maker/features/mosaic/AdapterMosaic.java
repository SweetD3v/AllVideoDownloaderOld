package com.tools.videodownloader.collage_maker.features.mosaic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tools.videodownloader.R;
import com.tools.videodownloader.collage_maker.utils.AppConstants;
import com.tools.videodownloader.collage_maker.utils.SystemUtils;
import com.github.siyamed.shapeimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;


public class AdapterMosaic extends RecyclerView.Adapter<AdapterMosaic.ViewHolder> {
    private int borderWidth;
    private Context context;

    public MosaicChangeListener mosaicChangeListener;

    public List<MosaicItem> mosaicItems = new ArrayList();

    public int selectedSquareIndex;

    enum Mode {
        BLUR,
        MOSAIC,
        SHADER
    }

    interface MosaicChangeListener {
        void onSelected(MosaicItem mosaicItem);
    }

    public AdapterMosaic(Context context2, MosaicChangeListener mosaicChangeListener2) {
        this.context = context2;
        this.mosaicChangeListener = mosaicChangeListener2;
        this.borderWidth = SystemUtils.dpToPx(context2, AppConstants.BORDER_WIDTH_DP);
        this.mosaicItems.add(new MosaicItem(R.drawable.blue_mosoic, 0, Mode.BLUR));
        this.mosaicItems.add(new MosaicItem(R.drawable.mosaic_2, 0, Mode.MOSAIC));
        this.mosaicItems.add(new MosaicItem(R.drawable.mosaic_3, R.drawable.mosaic_33, Mode.SHADER));
        this.mosaicItems.add(new MosaicItem(R.drawable.mosaic_4, R.drawable.mosaic_44, Mode.SHADER));
        this.mosaicItems.add(new MosaicItem(R.drawable.mosaic_5, R.drawable.mosaic_55, Mode.SHADER));
        this.mosaicItems.add(new MosaicItem(R.drawable.mosaic_6, R.drawable.mosaic_66, Mode.SHADER));
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.splash_view, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(this.context).load(Integer.valueOf(this.mosaicItems.get(i).frameId)).into(viewHolder.mosaic);
        if (this.selectedSquareIndex == i) {
            viewHolder.mosaic.setBorderColor(ContextCompat.getColor(context,R.color.colorAccent));
            viewHolder.mosaic.setBorderWidth(this.borderWidth);
            return;
        }
        viewHolder.mosaic.setBorderColor(0);
        viewHolder.mosaic.setBorderWidth(this.borderWidth);
    }

    public int getItemCount() {
        return this.mosaicItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RoundedImageView mosaic;

        public ViewHolder(View view) {
            super(view);
            this.mosaic = view.findViewById(R.id.splash);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            AdapterMosaic.this.selectedSquareIndex = getAdapterPosition();
            if (AdapterMosaic.this.selectedSquareIndex < AdapterMosaic.this.mosaicItems.size()) {
                AdapterMosaic.this.mosaicChangeListener.onSelected(AdapterMosaic.this.mosaicItems.get(AdapterMosaic.this.selectedSquareIndex));
            }
            AdapterMosaic.this.notifyDataSetChanged();
        }
    }

    static class MosaicItem {
        int frameId;
        Mode mode;
        int shaderId;

        MosaicItem(int i, int i2, Mode mode2) {
            this.frameId = i;
            this.mode = mode2;
            this.shaderId = i2;
        }
    }
}
