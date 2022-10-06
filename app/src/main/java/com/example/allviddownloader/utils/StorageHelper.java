package com.example.allviddownloader.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.example.allviddownloader.AllVidApp;
import com.example.allviddownloader.R;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.HashSet;

public class StorageHelper {
    public static void deleteFile(Context context, @NonNull final File file) throws ProgressException {
        ErrorCause error = new ErrorCause(file.getName());
        boolean success = false;
        try {
            success = file.delete();
        } catch (Exception e) {
            error.addCause(e.getLocalizedMessage());
        }
        if (!success && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DocumentFile document = getDocumentFile(context, file, false, false);
            success = document != null && document.delete();
            error.addCause("Failed SAF");
        }

        if (!success && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ContentResolver resolver = context.getContentResolver();

            try {
                Uri uri = getUriForFile(context, file);
                if (uri != null) {
                    resolver.delete(uri, null, null);
                }
                success = !file.exists();
            } catch (Exception e) {
                error.addCause(String.format("Failed CP: %s", e.getLocalizedMessage()));
                success = false;
            }
        }

        if (success) scanFile(context, new String[]{file.getPath()});
        else throw new ProgressException(error);
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, AllVidApp.Companion.getInstance().getPackageName() + ".provider", file);
    }

    public static void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context.getApplicationContext(), path, null, null);
    }

    private static DocumentFile getDocumentFile(Context context, @NonNull final File file, final boolean isDirectory, final boolean createDirectories) {

        Uri treeUri = getTreeUri(context);

        if (treeUri == null) return null;

        DocumentFile document = DocumentFile.fromTreeUri(context, treeUri);
        String sdcardPath = getSavedSdcardPath(context);
        String suffixPathPart = null;

        if (sdcardPath != null) {
            if (file.getPath().contains(sdcardPath))
                suffixPathPart = file.getAbsolutePath().substring(sdcardPath.length());
        } else {
            HashSet<File> storageRoots = StorageHelper.getStorageRoots(context);
            for (File root : storageRoots) {
                if (root != null) {
                    if (file.getPath().contains(root.getPath()))
                        suffixPathPart = file.getAbsolutePath().substring(file.getPath().length());
                }
            }
        }

        if (suffixPathPart == null) {
            return null;
        }

        if (suffixPathPart.startsWith(File.separator)) suffixPathPart = suffixPathPart.substring(1);

        String[] parts = suffixPathPart.split("/");

        for (int i = 0; i < parts.length; i++) {

            DocumentFile tmp = document.findFile(parts[i]);
            if (tmp != null)
                document = document.findFile(parts[i]);
            else {
                if (i < parts.length - 1) {
                    if (createDirectories) document = document.createDirectory(parts[i]);
                    else return null;
                } else if (isDirectory) document = document.createDirectory(parts[i]);
                else return document.createFile("image", parts[i]);
            }
        }

        return document;
    }

    private static Uri getTreeUri(Context context) {
        String uriString = Hawk.get(context.getString(R.string.preference_internal_uri_extsdcard_photos), null);

        if (uriString == null) return null;
        return Uri.parse(uriString);
    }

    private static String getSavedSdcardPath(Context context) {
        return Hawk.get("sd_card_path", null);
    }

    public static HashSet<File> getStorageRoots(Context context) {
        HashSet<File> paths = new HashSet<File>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index >= 0)
                    paths.add(new File(file.getAbsolutePath().substring(0, index)));
            }
        }
        return paths;
    }
}
