package com.example.allviddownloader.collage_maker.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allviddownloader.R;
import com.example.allviddownloader.collage_maker.ui.ToolType;

import java.util.ArrayList;
import java.util.List;

public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {

    public OnItemSelected mOnItemSelected;
    public List<ToolModel> mToolList = new ArrayList<>();

    public interface OnItemSelected {
        void onToolSelected(ToolType toolType);
    }

    public EditingToolsAdapter(OnItemSelected onItemSelected) {
        this.mOnItemSelected = onItemSelected;
        this.mToolList.add(new ToolModel("Crop", R.drawable.ic_crop_two, ToolType.CROP));
        this.mToolList.add(new ToolModel("Adjust", R.drawable.ic_adjust_two, ToolType.ADJUST));
        this.mToolList.add(new ToolModel("Filter", R.drawable.ic_filter_two, ToolType.FILTER));
        this.mToolList.add(new ToolModel("Overlay", R.drawable.ic_overlay_two, ToolType.OVERLAY));
        this.mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker_two, ToolType.STICKER));
        this.mToolList.add(new ToolModel("Text", R.drawable.ic_text_two, ToolType.TEXT));
        this.mToolList.add(new ToolModel("Fit", R.drawable.ic_fit_two, ToolType.INSTA));
        this.mToolList.add(new ToolModel("Blur", R.drawable.ic_blur_two, ToolType.BLUR));
        this.mToolList.add(new ToolModel("Splash", R.drawable.ic_splash_two, ToolType.SPLASH));
        this.mToolList.add(new ToolModel("Brush", R.drawable.ic_paint_two, ToolType.BRUSH));
        this.mToolList.add(new ToolModel("Mosaic", R.drawable.mosaic, ToolType.MOSAIC));
    }

    public EditingToolsAdapter(OnItemSelected onItemSelected, boolean z) {
        this.mOnItemSelected = onItemSelected;
        this.mToolList.add(new ToolModel("Layout", R.drawable.layout_onclick_and_clickout, ToolType.LAYOUT));
        this.mToolList.add(new ToolModel("Border", R.drawable.boader_onclick_and_clickout, ToolType.BORDER));
        this.mToolList.add(new ToolModel("Ratio", R.drawable.ratio_onclick_and_clickout, ToolType.RATIO));
        this.mToolList.add(new ToolModel("Filter", R.drawable.filter_onclick_and_clickout, ToolType.FILTER));
        this.mToolList.add(new ToolModel("Sticker", R.drawable.sticker_onclick_and_clickout, ToolType.STICKER));
        this.mToolList.add(new ToolModel("Text", R.drawable.text_onclick_and_clickout, ToolType.TEXT));
        this.mToolList.add(new ToolModel("Bg", R.drawable.bg_onclick_and_clickout, ToolType.BACKGROUND));
    }

    static class ToolModel {

        public int mToolIcon;

        public String mToolName;

        public ToolType mToolType;

        ToolModel(String str, int i, ToolType toolType) {
            this.mToolName = str;
            this.mToolIcon = i;
            this.mToolType = toolType;
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_editing_tools, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ToolModel toolModel = this.mToolList.get(i);
        viewHolder.txtTool.setText(toolModel.mToolName);
        viewHolder.imgToolIcon.setImageResource(toolModel.mToolIcon);
    }

    public int getItemCount() {
        return this.mToolList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;
        ConstraintLayout wrapTool;

        ViewHolder(View view) {
            super(view);
            this.imgToolIcon = view.findViewById(R.id.imgToolIcon);
            this.txtTool = view.findViewById(R.id.txtTool);
            this.wrapTool = view.findViewById(R.id.wrapTool);
            this.wrapTool.setOnClickListener(view1 -> EditingToolsAdapter.this.mOnItemSelected.onToolSelected((EditingToolsAdapter.this.mToolList.get(ViewHolder.this.getLayoutPosition())).mToolType));
        }
    }
}
