package com.example.allviddownloader.collage_maker.features.college.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allviddownloader.R;
import com.example.allviddownloader.collage_maker.features.college.PuzzleLayout;
import com.example.allviddownloader.collage_maker.features.college.SquarePuzzleView;
import com.example.allviddownloader.collage_maker.features.college.layout.slant.NumberSlantLayout;
import com.example.allviddownloader.collage_maker.features.college.layout.straight.NumberStraightLayout;

import java.util.ArrayList;
import java.util.List;

public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.PuzzleViewHolder> {
    private List<Bitmap> bitmapData = new ArrayList();
    private List<PuzzleLayout> layoutData = new ArrayList();

    public OnItemClickListener onItemClickListener;

    public int selectedIndex = 0;

    public interface OnItemClickListener {
        void onItemClick(PuzzleLayout puzzleLayout, int i);
    }

    public PuzzleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new PuzzleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_college, viewGroup, false));
    }

    public void setSelectedIndex(int i) {
        this.selectedIndex = i;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void onBindViewHolder(PuzzleViewHolder puzzleViewHolder, int position) {
        final PuzzleLayout collegeLayout = this.layoutData.get(puzzleViewHolder.getBindingAdapterPosition());
        puzzleViewHolder.puzzleView.setNeedDrawLine(true);
        puzzleViewHolder.puzzleView.setNeedDrawOuterLine(true);
        puzzleViewHolder.puzzleView.setTouchEnable(false);
        puzzleViewHolder.puzzleView.setLineSize(6);
        puzzleViewHolder.puzzleView.setLineColor(ContextCompat.getColor(puzzleViewHolder.itemView.getContext(), R.color.textColorPrimary));
        puzzleViewHolder.puzzleView.setPuzzleLayout(collegeLayout);
        if (this.selectedIndex == puzzleViewHolder.getBindingAdapterPosition()) {
            puzzleViewHolder.puzzleView.setBackgroundColor(ContextCompat.getColor(puzzleViewHolder.itemView.getContext(), R.color.colorPrimary));
        } else {
            puzzleViewHolder.puzzleView.setBackgroundColor(0);
        }
        puzzleViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PuzzleAdapter.this.onItemClickListener != null) {
                    int i = 0;
                    if (collegeLayout instanceof NumberSlantLayout) {
                        i = ((NumberSlantLayout) collegeLayout).getTheme();
                    } else if (collegeLayout instanceof NumberStraightLayout) {
                        i = ((NumberStraightLayout) collegeLayout).getTheme();
                    }
                    PuzzleAdapter.this.onItemClickListener.onItemClick(collegeLayout, i);
                }
                PuzzleAdapter.this.selectedIndex = puzzleViewHolder.getBindingAdapterPosition();
                PuzzleAdapter.this.notifyDataSetChanged();
            }
        });
        if (this.bitmapData != null) {
            int size = this.bitmapData.size();
            if (collegeLayout.getAreaCount() > size) {
                for (int i2 = 0; i2 < collegeLayout.getAreaCount(); i2++) {
                    puzzleViewHolder.puzzleView.addPiece(this.bitmapData.get(i2 % size));
                }
                return;
            }
            puzzleViewHolder.puzzleView.addPieces(this.bitmapData);
        }
    }

    public int getItemCount() {
        if (this.layoutData == null) {
            return 0;
        }
        return this.layoutData.size();
    }

    public void refreshData(List<PuzzleLayout> list, List<Bitmap> list2) {
        this.layoutData = list;
        this.bitmapData = list2;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }

    public static class PuzzleViewHolder extends RecyclerView.ViewHolder {
        SquarePuzzleView puzzleView;

        public PuzzleViewHolder(View view) {
            super(view);
            this.puzzleView = view.findViewById(R.id.puzzle);
        }
    }
}
