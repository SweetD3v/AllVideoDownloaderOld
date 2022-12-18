package com.video.tools.videodownloader.collage_maker.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.video.tools.videodownloader.collage_maker.features.college.PuzzleView;

public class SquarePuzzleView extends PuzzleView {
    public SquarePuzzleView(Context context) {
        super(context);
    }

    public SquarePuzzleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SquarePuzzleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth > measuredHeight) {
            measuredWidth = measuredHeight;
        }
        setMeasuredDimension(measuredWidth, measuredWidth);
    }
}
