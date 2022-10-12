package com.example.allviddownloader.collage_maker.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;

import com.example.allviddownloader.AllVidApp;
import com.example.allviddownloader.R;
import com.example.allviddownloader.collage_maker.features.college.PuzzleView;

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

    public static File saveBitmapAsFile(Bitmap bitmap, String dirName) {
        FileOutputStream fileOutputStream;
        String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        File file2 = new File(file + "/" + AllVidApp.Companion.getInstance().getString(R.string.app_name) + "/" + dirName);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        try {
            File file3 = new File(file + "/" + AllVidApp.Companion.getInstance().getString(R.string.app_name) + "/" + dirName + "/" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) + ".jpg");
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

    public static File saveBitmapAsFile(Bitmap bitmap) {
        FileOutputStream fileOutputStream;
        String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file2 = new File(file + "/" + AllVidApp.Companion.getInstance().getString(R.string.app_name) + "/");
        if (!file2.exists()) {
            file2.mkdirs();
        }
        try {
            File file3 = new File(file + "/" + AllVidApp.Companion.getInstance().getString(R.string.app_name) + "/" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) + ".jpg");
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

    public static Bitmap createBitmap(PuzzleView puzzleView, int i) {
        puzzleView.clearHandling();
        puzzleView.invalidate();
        Bitmap createBitmap = Bitmap.createBitmap(i, (int) (((float) i) / (((float) puzzleView.getWidth()) / ((float) puzzleView.getHeight()))), Bitmap.Config.ARGB_8888);
        puzzleView.draw(new Canvas(createBitmap));
        return createBitmap;
    }

    public static Bitmap createBitmap(PuzzleView puzzleView) {
        puzzleView.clearHandling();
        puzzleView.invalidate();
        Bitmap createBitmap = Bitmap.createBitmap(puzzleView.getWidth(), puzzleView.getHeight(), Bitmap.Config.ARGB_8888);
        puzzleView.draw(new Canvas(createBitmap));
        return createBitmap;
    }

}