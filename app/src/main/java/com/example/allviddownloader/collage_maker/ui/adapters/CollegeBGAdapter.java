package com.example.allviddownloader.collage_maker.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allviddownloader.R;
import com.example.allviddownloader.collage_maker.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class CollegeBGAdapter extends RecyclerView.Adapter<CollegeBGAdapter.ViewHolder> {

    public BackgroundChangeListener backgroundChangeListener;
    private Context context;

    public int selectedSquareIndex;

    public List<SquareView> squareViews = new ArrayList();

    public interface BackgroundChangeListener {
        void onBackgroundSelected(SquareView squareView);
    }

    public CollegeBGAdapter(Context context2, BackgroundChangeListener backgroundChangeListener2) {
        this.context = context2;
        this.backgroundChangeListener = backgroundChangeListener2;
        this.squareViews.add(new SquareView(Color.parseColor("#ffffff"), "White", true));
        this.squareViews.add(new SquareView(R.color.black, "Black"));
        List<String> lstColorForBrush = ColorUtils.lstColorForBrush();
        for (int i = 0; i < lstColorForBrush.size() - 2; i++) {
            this.squareViews.add(new SquareView(Color.parseColor(lstColorForBrush.get(i)), "", true));
        }
    }

    public CollegeBGAdapter(Context context2, BackgroundChangeListener backgroundChangeListener2, boolean z) {
        this.context = context2;
        this.backgroundChangeListener = backgroundChangeListener2;
        this.squareViews.add(new SquareView(R.drawable.gradient_1, "G1"));
        this.squareViews.add(new SquareView(R.drawable.gradient_2, "G2"));
        this.squareViews.add(new SquareView(R.drawable.gradient_3, "G3"));
        this.squareViews.add(new SquareView(R.drawable.gradient_4, "G4"));
        this.squareViews.add(new SquareView(R.drawable.gradient_5, "G5"));
        this.squareViews.add(new SquareView(R.drawable.gradient_11, "G11"));
        this.squareViews.add(new SquareView(R.drawable.gradient_10, "G10"));
        this.squareViews.add(new SquareView(R.drawable.gradient_6, "G6"));
        this.squareViews.add(new SquareView(R.drawable.gradient_7, "G7"));
        this.squareViews.add(new SquareView(R.drawable.gradient_13, "G13"));
        this.squareViews.add(new SquareView(R.drawable.gradient_14, "G14"));
        this.squareViews.add(new SquareView(R.drawable.gradient_16, "G16"));
        this.squareViews.add(new SquareView(R.drawable.gradient_17, "G17"));
        this.squareViews.add(new SquareView(R.drawable.gradient_18, "G18"));
    }

    public CollegeBGAdapter(Context context2, BackgroundChangeListener backgroundChangeListener2, List<Drawable> list) {
        this.context = context2;
        this.backgroundChangeListener = backgroundChangeListener2;
        for (Drawable squareView : list) {
            this.squareViews.add(new SquareView(1, "", false, true, squareView));
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.square_view_item, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SquareView squareView = this.squareViews.get(i);
        if (squareView.isColor) {
            viewHolder.squareView.setBackgroundColor(squareView.drawableId);
        } else if (squareView.drawable != null) {
            viewHolder.squareView.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.imageView.setImageDrawable(squareView.drawable);
        } else {
            viewHolder.squareView.setBackgroundResource(squareView.drawableId);
        }
        if (this.selectedSquareIndex == i) {
            viewHolder.wrapSquareView.setBackground(this.context.getDrawable(R.drawable.border_view));
        } else {
            viewHolder.wrapSquareView.setBackground(this.context.getDrawable(R.drawable.border_transparent_view));
        }
    }

    public void setSelectedSquareIndex(int i) {
        this.selectedSquareIndex = i;
    }

    public int getItemCount() {
        return this.squareViews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public View squareView;
        public ConstraintLayout wrapSquareView;

        public ViewHolder(View view) {
            super(view);
            this.squareView = view.findViewById(R.id.squareView);
            this.wrapSquareView = view.findViewById(R.id.wrapSquareView);
            this.imageView = view.findViewById(R.id.imgView);
            this.imageView.setVisibility(View.GONE);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            CollegeBGAdapter.this.selectedSquareIndex = getAdapterPosition();
            CollegeBGAdapter.this.backgroundChangeListener.onBackgroundSelected(CollegeBGAdapter.this.squareViews.get(CollegeBGAdapter.this.selectedSquareIndex));
            CollegeBGAdapter.this.notifyDataSetChanged();
        }
    }

    public static class SquareView {
        public Drawable drawable;
        public int drawableId;
        public boolean isBitmap;
        public boolean isColor;
        public String text;

        SquareView(int i, String str) {
            this.drawableId = i;
            this.text = str;
        }

        public SquareView(int i, String str, boolean z) {
            this.drawableId = i;
            this.text = str;
            this.isColor = z;
        }

        public SquareView(int i, String str, boolean z, boolean z2, Drawable drawable2) {
            this.drawableId = i;
            this.text = str;
            this.isColor = z;
            this.isBitmap = z2;
            this.drawable = drawable2;
        }
    }
}
