package com.example.allviddownloader.collage_maker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class UtilsAsset {
    public static Bitmap loadBitmapFromAssets(Context context, String str) {
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(str);
            Bitmap decodeStream = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return decodeStream;
        } catch (Exception e) {
            return null;
        }
    }
}
