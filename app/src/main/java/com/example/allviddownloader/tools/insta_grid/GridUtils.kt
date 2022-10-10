package com.example.allviddownloader.tools.insta_grid

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.example.allviddownloader.utils.FileUtilsss
import java.io.File
import java.io.IOException

class GridUtils {
    companion object {
        var bitmaps: Array<Bitmap?>? = null

        fun splitBitmap(ctx: Context, croppedBitmap: Bitmap): Array<Bitmap?> {
            val imgs: Array<Bitmap?> = arrayOfNulls(9)
            imgs[0] = Bitmap.createBitmap(
                croppedBitmap,
                0,
                0,
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            imgs[1] = Bitmap.createBitmap(
                croppedBitmap,
                croppedBitmap.width / 3,
                0,
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            imgs[2] = Bitmap.createBitmap(
                croppedBitmap,
                (croppedBitmap.width / 1.5f).toInt(),
                0,
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            imgs[3] = Bitmap.createBitmap(
                croppedBitmap,
                0,
                croppedBitmap.height / 3,
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            imgs[4] = Bitmap.createBitmap(
                croppedBitmap,
                croppedBitmap.width / 3,
                croppedBitmap.height / 3,
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            imgs[5] = Bitmap.createBitmap(
                croppedBitmap,
                (croppedBitmap.width / 1.5f).toInt(),
                croppedBitmap.height / 3,
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            imgs[6] = Bitmap.createBitmap(
                croppedBitmap,
                0,
                (croppedBitmap.height / 1.5f).toInt(),
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            imgs[7] = Bitmap.createBitmap(
                croppedBitmap,
                croppedBitmap.width / 3,
                (croppedBitmap.height / 1.5f).toInt(),
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            imgs[8] = Bitmap.createBitmap(
                croppedBitmap,
                (croppedBitmap.width / 1.5f).toInt(),
                (croppedBitmap.height / 1.5f).toInt(),
                croppedBitmap.width / 3,
                croppedBitmap.height / 3
            )
            return imgs
        }

        fun getImageFile(ctx: Context): File {
            val imageFileName = "JPEG_" + System.currentTimeMillis() + "_"
            val storageDir = ctx.getExternalFilesDir("temp")
            val file: File = File.createTempFile(
                imageFileName, ".jpg", storageDir
            )
            return file
        }

        fun getBitmap(context: Context, imageUri: Uri?): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                return bitmap
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        fun saveBitmapGrid(
            context: Context, bitmap: Bitmap?,
            displayName: String,
            directory: String
        ) {

            FileUtilsss.saveBitmapAsFileA10(
                context, bitmap,
                displayName, directory
            ) {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(it),
                    null
                ) { path, uri ->
                    Log.i("ExternalStorage", "Scanned $path:")
                    Log.i("ExternalStorage", "-> uri=$uri")
                }
            }
        }

        fun saveImageTemp(ctx: Context, bitmap: Bitmap?, list: (ArrayList<Uri>) -> Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                FileUtilsss.saveBitmapAsFileCache(
                    ctx,
                    bitmap,
                    "IMG_${System.currentTimeMillis()}.jpg"
                ) { path ->
                    val uri =
                        FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", File(path))
                    list(arrayListOf(uri))
                }
            } else {
                FileUtilsss.saveBitmapAsFileCache(
                    ctx,
                    bitmap,
                    "IMG_${System.currentTimeMillis()}.jpg"
                ) { path ->
                    val uri =
                        FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", File(path))
                    list(arrayListOf(uri))
                }
            }
        }
    }
}