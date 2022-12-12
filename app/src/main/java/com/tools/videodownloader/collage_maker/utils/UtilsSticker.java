package com.tools.videodownloader.collage_maker.utils;

import android.graphics.RectF;

import androidx.annotation.NonNull;

public class UtilsSticker {


    public static void trapToRect(@NonNull RectF paramRectF, @NonNull float[] paramArrayOffloat) {
        paramRectF.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        int i;
        for (i = 1; i < paramArrayOffloat.length; i += 2) {
            float f3;
            float f2 = Math.round(paramArrayOffloat[i - 1] * 10.0F) / 10.0F;
            float f1 = Math.round(paramArrayOffloat[i] * 10.0F) / 10.0F;
            if (f2 < paramRectF.left) {
                f3 = f2;
            } else {
                f3 = paramRectF.left;
            }
            paramRectF.left = f3;
            if (f1 < paramRectF.top) {
                f3 = f1;
            } else {
                f3 = paramRectF.top;
            }
            paramRectF.top = f3;
            if (f2 <= paramRectF.right)
                f2 = paramRectF.right;
            paramRectF.right = f2;
            if (f1 <= paramRectF.bottom)
                f1 = paramRectF.bottom;
            paramRectF.bottom = f1;
        }
        paramRectF.sort();
    }
}
