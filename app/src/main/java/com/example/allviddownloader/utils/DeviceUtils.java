package com.example.allviddownloader.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.NonNull;

public class DeviceUtils {
    public static boolean isPortrait(@NonNull Resources resources) {
        return resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static int rotateScreen(Context context, Uri mVideoUri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap bmp;
            retriever.setDataSource(context, mVideoUri);
            bmp = retriever.getFrameAtTime();

            int videoWidth = bmp.getWidth();
            int videoHeight = bmp.getHeight();

            if (videoWidth > videoHeight) {
                return 1;
            }
            if (videoWidth < videoHeight) {
                return 0;
            }

        } catch (RuntimeException ex) {

        }
        return 0;
    }
}
