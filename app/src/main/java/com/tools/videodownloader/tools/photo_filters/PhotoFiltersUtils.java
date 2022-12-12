package com.tools.videodownloader.tools.photo_filters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.tools.videodownloader.collage_maker.features.college.PuzzleLayout;
import com.tools.videodownloader.collage_maker.features.college.PuzzleView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class PhotoFiltersUtils {
    public static String string;
    public static String images;
    public static Bitmap photoFilterBmp;

    public static void loadPhoto(Activity activity, List<String> lstPaths, PuzzleLayout puzzleLayout,
                                 PuzzleView puzzleView, List<Target> targets, int deviceWidth) {
        final int i;
        final ArrayList<Bitmap> arrayList = new ArrayList<>();
        if (lstPaths.size() > puzzleLayout.getAreaCount()) {
            i = puzzleLayout.getAreaCount();
        } else {
            i = lstPaths.size();
        }
        for (int i2 = 0; i2 < i; i2++) {
            Target target = new Target() {
                public void onBitmapFailed(Exception exc, Drawable drawable) {
                }

                public void onPrepareLoad(Drawable drawable) {
                }

                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                    int width = bitmap.getWidth();
                    float f = (float) width;
                    float height = (float) bitmap.getHeight();
                    float max = Math.max(f / f, height / f);
                    if (max > 1.0f) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (f / max), (int) (height / max), false);
                    }
                    arrayList.add(bitmap);
                    if (arrayList.size() == i) {
                        if (lstPaths.size() < puzzleLayout.getAreaCount()) {
                            for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
                                try {
                                    puzzleView.addPiece((Bitmap) arrayList.get(i % i));
                                } catch (Exception e) {
                                    Toast.makeText(activity, "An error occurred while loading image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            puzzleView.addPieces(arrayList);
                        }
                    }
                    targets.remove(this);
                }
            };
            try {
                Picasso picasso = Picasso.get();
                picasso.load(Uri.parse(lstPaths.get(i2))).resize(deviceWidth, deviceWidth).centerInside().config(Bitmap.Config.RGB_565).into((Target) target);
                targets.add(target);
            } catch (Exception e) {
                Log.e("TAG", "loadPhotoExc: " + e.getMessage());
            }
        }
    }
}
