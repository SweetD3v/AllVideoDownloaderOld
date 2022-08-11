package com.example.allviddownloader.collage_maker.features;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class DrawBitmapModel {
    private Context context;
    private boolean isLoadBitmap;
    private List<Bitmap> lstBitmaps;
    private List<Integer> lstIconWhenDrawing;
    private List<BrushDrawView.Vector2> mPositions = new ArrayList(100);
    private int mainIcon;

    public DrawBitmapModel(int i, List<Integer> list, Context context2) {
        this.mainIcon = i;
        this.lstIconWhenDrawing = list;
        this.context = context2;
    }

    public void clearBitmap() {
        if (this.lstBitmaps != null && !this.lstBitmaps.isEmpty()) {
            this.lstBitmaps.clear();
        }
    }

    public int getMainIcon() {
        return this.mainIcon;
    }


    public List<Integer> getLstIconWhenDrawing() {
        return this.lstIconWhenDrawing;
    }


    public boolean isLoadBitmap() {
        return this.isLoadBitmap;
    }

    public void setLoadBitmap(boolean z) {
        this.isLoadBitmap = z;
    }


    public List<BrushDrawView.Vector2> getmPositions() {
        return this.mPositions;
    }

    public Bitmap getBitmapByIndex(int i) {
        if (this.lstBitmaps == null || this.lstBitmaps.isEmpty()) {
            init();
        }
        return this.lstBitmaps.get(i);
    }

    public void init() {
        if (this.lstBitmaps == null || this.lstBitmaps.isEmpty()) {
            this.lstBitmaps = new ArrayList();
            for (Integer intValue : this.lstIconWhenDrawing) {
                this.lstBitmaps.add(BitmapFactory.decodeResource(this.context.getResources(), intValue.intValue()));
            }
        }
    }
}
