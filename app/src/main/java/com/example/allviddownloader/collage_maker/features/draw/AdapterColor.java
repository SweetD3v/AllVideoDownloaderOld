package com.example.allviddownloader.collage_maker.features.draw;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allviddownloader.R;
import com.example.allviddownloader.collage_maker.utils.UtilsColor;
import com.github.pavlospt.CircleView;

import java.util.List;


public class AdapterColor extends RecyclerView.Adapter<AdapterColor.ViewHolder> {
    public BrushColorListener brushColorListener;
    public List<String> colors = UtilsColor.lstColorForBrush();
    private Context context;
    public int selectedColorIndex;

    public AdapterColor(Context context2, BrushColorListener brushColorListener2) {
        this.context = context2;
        this.brushColorListener = brushColorListener2;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.color.setFillColor(Color.parseColor(this.colors.get(i)));
        viewHolder.color.setStrokeColor(Color.parseColor(this.colors.get(i)));
        if (this.selectedColorIndex == i) {
            viewHolder.color.setStrokeColor(context.getResources().getColor(R.color.black));
            if (this.colors.get(i).equals("#00000000")) {
                viewHolder.color.setBackgroundColor(Color.parseColor("#00000000"));
                viewHolder.color.setStrokeColor(Color.parseColor("#FF4081"));
                return;
            }
            viewHolder.color.setBackgroundColor(-1);
        } else if (this.colors.get(i).equals("#00000000")) {
            viewHolder.color.setBackground(this.context.getDrawable(R.drawable.none));
            viewHolder.color.setBackgroundColor(Color.parseColor("#00000000"));
        } else {
            viewHolder.color.setBackgroundColor(Color.parseColor(this.colors.get(i)));
        }
    }

    @Override
    public int getItemCount() {
        return this.colors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleView color;

        ViewHolder(View view) {
            super(view);
            this.color = view.findViewById(R.id.color);
            this.color.setOnClickListener(view1 -> {
                AdapterColor.this.selectedColorIndex = ViewHolder.this.getLayoutPosition();
                AdapterColor.this.brushColorListener.onColorChanged(AdapterColor.this.colors.get(AdapterColor.this.selectedColorIndex));
                AdapterColor.this.notifyDataSetChanged();
            });
        }
    }

    public void setSelectedColorIndex(int i) {
        this.selectedColorIndex = i;
    }
}
