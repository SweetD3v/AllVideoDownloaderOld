package com.example.allviddownloader.collage_maker.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;

import com.example.allviddownloader.collage_maker.features.college.CollegeView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    public static boolean fileIsExists(String str) {
        if (str == null || str.trim().length() <= 0) {
            return false;
        }
        try {
            if (!new File(str).exists()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static File saveBitmapAsFile(Bitmap bitmap) {
        FileOutputStream fileOutputStream;
        String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file2 = new File(file + "/PicsArc/");
        if (!file2.exists()) {
            file2.mkdirs();
        }
        try {
            File file3 = new File(file + "/PicsArc/" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) + ".jpg");
            file3.createNewFile();
            fileOutputStream = new FileOutputStream(file3);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file3;
        } catch (Exception e2) {

            fileOutputStream = null;
            e2.printStackTrace();
            if (fileOutputStream != null) {
            }
            return null;
        }
    }

    public static Bitmap createBitmap(CollegeView collegeView, int i) {
        collegeView.clearHandling();
        collegeView.invalidate();
        Bitmap createBitmap = Bitmap.createBitmap(i, (int) (((float) i) / (((float) collegeView.getWidth()) / ((float) collegeView.getHeight()))), Bitmap.Config.ARGB_8888);
        collegeView.draw(new Canvas(createBitmap));
        return createBitmap;
    }

    public static Bitmap createBitmap(CollegeView collegeView) {
        collegeView.clearHandling();
        collegeView.invalidate();
        Bitmap createBitmap = Bitmap.createBitmap(collegeView.getWidth(), collegeView.getHeight(), Bitmap.Config.ARGB_8888);
        collegeView.draw(new Canvas(createBitmap));
        return createBitmap;
    }

}