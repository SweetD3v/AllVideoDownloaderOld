package com.video.tools.videodownloader.collage_maker.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.video.tools.videodownloader.R;
import com.video.tools.videodownloader.collage_maker.ui.EditingToolType;

import java.util.ArrayList;
import java.util.List;

public class BottomToolsAdapter extends RecyclerView.Adapter<BottomToolsAdapter.ViewHolder> {

    public OnItemSelected mOnItemSelected;

    public List<ToolModel> mToolList = new ArrayList<>();

    public interface OnItemSelected {
        void onToolSelected(EditingToolType editingToolType);
    }

    public void setmOnItemSelected(OnItemSelected mOnItemSelected) {
        this.mOnItemSelected = mOnItemSelected;
    }

    public BottomToolsAdapter() {
        this.mToolList.add(new ToolModel("Crop", R.drawable.crop_unpress, EditingToolType.CROP));
        this.mToolList.add(new ToolModel("Adjust", R.drawable.adjust_unpress, EditingToolType.ADJUST));
//        this.mToolList.add(new ToolModel("Filter", R.drawable.filter_unpress1, EditingToolType.FILTER));
//        this.mToolList.add(new ToolModel("Overlay", R.drawable.overlay_unpress, EditingToolType.OVERLAY));
//        this.mToolList.add(new ToolModel("Sticker", R.drawable.sticker_unpress2, EditingToolType.STICKER));
//        this.mToolList.add(new ToolModel("Text", R.drawable.text_unpress1, EditingToolType.TEXT));
        this.mToolList.add(new ToolModel("Fit", R.drawable.fit_unpress, EditingToolType.INSTA));
//        this.mToolList.add(new ToolModel("Blur", R.drawable.blur_unpress, EditingToolType.BLUR));
//        this.mToolList.add(new ToolModel("Splash", R.drawable.splash_unpress, EditingToolType.SPLASH));
        this.mToolList.add(new ToolModel("Brush", R.drawable.brush_unpress1, EditingToolType.BRUSH));
//        this.mToolList.add(new ToolModel("Mosaic", R.drawable.mosaic_unpress, EditingToolType.MOSAIC));
//        this.mToolList.add(new ToolModel("Beauty", R.drawable.beauty_unpress, ToolType.BEAUTY));
    }

    public BottomToolsAdapter(boolean z) {
        this.mToolList.add(new ToolModel("Layout", R.drawable.layout_onclick_and_clickout, EditingToolType.LAYOUT));
        this.mToolList.add(new ToolModel("Border", R.drawable.boader_onclick_and_clickout, EditingToolType.BORDER));
        this.mToolList.add(new ToolModel("Ratio", R.drawable.ratio_onclick_and_clickout, EditingToolType.RATIO));
//        this.mToolList.add(new ToolModel("Filter", R.drawable.filter_onclick_and_clickout, EditingToolType.FILTER));
//        this.mToolList.add(new ToolModel("Sticker", R.drawable.sticker_onclick_and_clickout, EditingToolType.STICKER));
//        this.mToolList.add(new ToolModel("Text", R.drawable.text_onclick_and_clickout, EditingToolType.TEXT));
        this.mToolList.add(new ToolModel("Bg", R.drawable.bg_onclick_and_clickout, EditingToolType.BACKGROUND));
    }

    class ToolModel {
        public int mToolIcon;
        public String mToolName;
        public EditingToolType mEditingToolType;

        ToolModel(String str, int i, EditingToolType editingToolType) {
            this.mToolName = str;
            this.mToolIcon = i;
            this.mEditingToolType = editingToolType;
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
            this.wrapTool.setOnClickListener(view1 -> mOnItemSelected.onToolSelected((mToolList.get(getAdapterPosition())).mEditingToolType));
        }
    }
}
