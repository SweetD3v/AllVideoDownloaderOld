package com.example.allviddownloader.collage_maker.features.college;

import android.content.Context;
import android.util.AttributeSet;

public class SquareCollegeView extends CollegeView {
    public SquareCollegeView(Context context) {
        super(context);
    }

    public SquareCollegeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SquareCollegeView(Context context, AttributeSet attributeSet, int i) {
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
