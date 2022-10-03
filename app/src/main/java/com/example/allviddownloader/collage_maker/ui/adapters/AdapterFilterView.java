package com.example.allviddownloader.collage_maker.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allviddownloader.R;
import com.example.allviddownloader.collage_maker.ui.interfaces.FilterListener;
import com.example.allviddownloader.collage_maker.utils.AppConstants;
import com.example.allviddownloader.collage_maker.utils.SystemUtils;
import com.example.allviddownloader.collage_maker.utils.UtilsFilter;
import com.github.siyamed.shapeimageview.RoundedImageView;

import java.util.List;


public class AdapterFilterView extends RecyclerView.Adapter<AdapterFilterView.ViewHolder> {
    private List<Bitmap> bitmaps;
    private int borderWidth;
    private Context context;

    public List<UtilsFilter.FilterBean> filterEffects;

    public FilterListener mFilterListener;

    public int selectedFilterIndex = 0;

    public AdapterFilterView(List<Bitmap> list, Context context2, FilterListener filterListener, List<UtilsFilter.FilterBean> list2) {
        this.mFilterListener = filterListener;
        this.bitmaps = list;
        this.context = context2;
        this.filterEffects = list2;
        this.borderWidth = SystemUtils.dpToPx(context2, AppConstants.BORDER_WIDTH_DP);
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_filter_view, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mTxtFilterName.setText(this.filterEffects.get(i).getName());
        viewHolder.mImageFilterView.setImageBitmap(this.bitmaps.get(i));
        viewHolder.mImageFilterView.setBorderColor(ContextCompat.getColor(context, R.color.colorAccent));
        if (selectedFilterIndex == i) {
            viewHolder.mImageFilterView.setBorderColor(ContextCompat.getColor(context, R.color.colorAccent));
            viewHolder.mImageFilterView.setBorderWidth(this.borderWidth);
            return;
        }
        viewHolder.mImageFilterView.setBorderColor(ContextCompat.getColor(context, R.color.white));
        viewHolder.mImageFilterView.setBorderWidth(this.borderWidth);
    }

    public int getItemCount() {
        return this.bitmaps.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView mImageFilterView;
        TextView mTxtFilterName;

        ViewHolder(View view) {
            super(view);
            mImageFilterView = view.findViewById(R.id.imgFilterView);
            mTxtFilterName = view.findViewById(R.id.txtFilterName);
            view.setOnClickListener(view1 -> {
                selectedFilterIndex = ViewHolder.this.getLayoutPosition();
                mFilterListener.onFilterSelected(AdapterFilterView.this.filterEffects.get(AdapterFilterView.this.selectedFilterIndex).getConfig());
                notifyDataSetChanged();
            });
        }
    }
}
