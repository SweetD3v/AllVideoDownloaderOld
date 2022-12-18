package com.video.tools.videodownloader.collage_maker.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.steelkiwi.cropiwa.AspectRatio;
import com.video.tools.videodownloader.R;

import java.util.Arrays;
import java.util.List;


public class AspectRatioPreviewAdapter extends RecyclerView.Adapter<AspectRatioPreviewAdapter.ViewHolder> {
    public int lastSelectedView;
    public OnNewSelectedListener listener;
    public List<CustomAspectRatio> ratios;
    public CustomAspectRatio selectedRatio;

    public interface OnNewSelectedListener {
        void onNewAspectRatioSelected(AspectRatio aspectRatio);
    }

    public AspectRatioPreviewAdapter() {
        this.ratios = Arrays.asList(new CustomAspectRatio(10, 10, R.drawable.crop_free, R.drawable.crop_free_click, "Free"),
                new CustomAspectRatio(1, 1, R.drawable.ratio_1_1, R.drawable.ratio_1_1_click, "1:1"),
                new CustomAspectRatio(4, 3, R.drawable.ratio_4_3, R.drawable.ratio_4_3_click, "4:3"),
                new CustomAspectRatio(3, 4, R.drawable.ratio_3_4, R.drawable.ratio_3_4_click, "3:4"),
                new CustomAspectRatio(5, 4, R.drawable.ratio_5_4, R.drawable.ratio_5_4_click, "5:4"),
                new CustomAspectRatio(4, 5, R.drawable.ratio_4_5, R.drawable.ratio_4_5_click, "4:5"),
                new CustomAspectRatio(3, 2, R.drawable.ratio_3_2, R.drawable.ratio_3_2_click, "3:2"),
                new CustomAspectRatio(2, 3, R.drawable.ratio_2_3, R.drawable.ratio_2_3_click, "2:3"),
                new CustomAspectRatio(9, 16, R.drawable.ratio_9_16, R.drawable.ratio_9_16_click, "9:16"),
                new CustomAspectRatio(16, 9, R.drawable.ratio_3_2, R.drawable.ratio_3_2_click, "3:2"));
        this.selectedRatio = this.ratios.get(0);
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_aspect_ratio, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CustomAspectRatio customAspectRatio = this.ratios.get(position);
        if (position == lastSelectedView) {
            viewHolder.ratioView.setImageResource(customAspectRatio.getSelectedIem());
//            viewHolder.ratioView.setColorFilter(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
        } else {
            viewHolder.ratioView.setImageResource(customAspectRatio.getUnselectItem());
//            viewHolder.ratioView.setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
        }

        viewHolder.txtRatio.setText(customAspectRatio.getTxtRatio());
    }

    public void setLastSelectedView(int i) {
        this.lastSelectedView = i;
    }

    @Override
    public int getItemCount() {
        return this.ratios.size();
    }

    public void setListener(OnNewSelectedListener onNewSelectedListener) {
        this.listener = onNewSelectedListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ratioView;
        public TextView txtRatio;

        public ViewHolder(View view) {
            super(view);
            this.ratioView = view.findViewById(R.id.aspect_ratio_preview);
            this.txtRatio = view.findViewById(R.id.txtRatio);
            this.ratioView.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (AspectRatioPreviewAdapter.this.lastSelectedView != getAdapterPosition()) {
                AspectRatioPreviewAdapter.this.selectedRatio = AspectRatioPreviewAdapter.this.ratios.get(getAdapterPosition());
                AspectRatioPreviewAdapter.this.lastSelectedView = getAdapterPosition();
                if (AspectRatioPreviewAdapter.this.listener != null) {
                    AspectRatioPreviewAdapter.this.listener.onNewAspectRatioSelected(AspectRatioPreviewAdapter.this.selectedRatio);
                }
                AspectRatioPreviewAdapter.this.notifyDataSetChanged();
            }
        }
    }
}
