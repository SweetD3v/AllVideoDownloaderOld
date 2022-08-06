package com.example.allviddownloader.utils

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.core.content.FileProvider
import com.example.allviddownloader.AllVidApp
import com.example.allviddownloader.R
import com.example.allviddownloader.models.Media
import java.io.File
import java.io.IOException
import java.io.OutputStream
import kotlin.math.roundToInt

var RECEIVER_ADDRESS = "andro.ops151@gmail.com"
var RootDirectoryFacebook =
    AllVidApp.getInstance().resources.getString(R.string.app_name) + "/Facebook/"
val originalPath =
    File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        AllVidApp.getInstance().resources.getString(R.string.app_name)
    )
var RootDirectoryFacebookShow = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Facebook"
)
var RootDirectoryInsta = "All Video HD Downloader/Insta/"
var RootDirectoryInstaShow = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Insta"
)
var RootDirectoryTwitter = "All Video HD Downloader/Twitter/"
var RootDirectoryTwitterShow = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Twitter"
)
var RootDirectoryWhatsappShow = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Whatsapp"
)

fun getClipBoardItems(ctx: Context): MutableList<String> {
    val clipboard: ClipboardManager? =
        ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val itemsList = mutableListOf<String>()

    if (clipboard!!.hasPrimaryClip()) {
        val primaryClip = clipboard.primaryClip!!
        for (i in 0 until primaryClip.itemCount) {
            itemsList.add(primaryClip.getItemAt(i).text.toString())
        }
        for (str in itemsList) {
            Log.e("TAG", "getClipBoardItems: $str")
        }
        return itemsList
    }
    return mutableListOf()
}

fun isNullOrEmpty(str: String?): Boolean {
    return str == null || str.isEmpty() || str.equals(
        "null",
        ignoreCase = true
    ) || str.equals("0", ignoreCase = true)
}

fun isAPI30OrAbove(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun getScreenWidth(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun dpToPx(context: Context, dp: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun saveBitmap(
    context: Context,
    bitmap: Bitmap,
    compressFormat: Bitmap.CompressFormat?,
    mime_type: String?,
    display_name: String?,
    path: String?
): Uri? {
    val openOutputStream: OutputStream
    val contentValues = ContentValues()
    contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, display_name)
    contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, mime_type)
    contentValues.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, path)
    val contentResolver: ContentResolver = context.contentResolver
    val insert: Uri? =
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    if (insert != null) {
        try {
            openOutputStream = contentResolver.openOutputStream(insert)!!
            return if (bitmap.compress(compressFormat, 90, openOutputStream)) {
                openOutputStream.close()
                insert
            } else {
                throw IOException("Failed to save bitmap.")
            }
        } catch (unused: IOException) {
            contentResolver.delete(insert, null, null)
            return insert
        } catch (th: Throwable) {
            th.addSuppressed(th)
        }
    }
    return null
}

fun getBitmapFromUri(context: Context, imageUri: Uri?): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        return bitmap
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

@Throws(IOException::class)
fun getVideoThumbnailA11(context: Context, uri: Uri?): Bitmap? {
    val mSize = Size(96, 96)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver.loadThumbnail(uri!!, mSize, null)
    } else null
}


fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
    var bitmap: Bitmap? = null
    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, videoUri)
        bitmap =
            mediaMetadataRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    } finally {
        mediaMetadataRetriever?.release()
    }
    return bitmap
}

fun getMedia(ctx: Context, block: (MutableList<Media>) -> Unit) {
    var mediaListFinal: MutableList<Media>
    object : AsyncTaskRunner<String, MutableList<Media>>(ctx) {
        override fun doInBackground(fileName: String?): MutableList<Media> {
            if (originalPath.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val mediaList = mutableListOf<Media>()
                    val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.MediaColumns.RELATIVE_PATH + " LIKE ? "
                    } else {
                        MediaStore.Images.Media.DATA + " LIKE ? "
                    }
                    val selectionArgs = arrayOf("%${ctx.getString(R.string.app_name)}%")
                    val contentResolver = ctx.applicationContext.contentResolver
                    contentResolver.query(
                        MediaStore.Files.getContentUri("external"),
                        null,
                        selection,
                        selectionArgs,
                        "${MediaStore.Video.Media.DATE_TAKEN} DESC"
                    )?.use { cursor ->
                        while (cursor.moveToNext()) {
                            val imageCol =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                            val path =
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                            val date =
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
                            val pathId = cursor.getString(imageCol)
                            val uri = Uri.parse(pathId)
                            var contentUri: Uri
                            contentUri = if (uri.toString().endsWith(".mp4")) {
                                ContentUris.withAppendedId(
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                            } else {
                                ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                            }
                            val media =
                                Media(contentUri, path, uri.toString().endsWith(".mp4"), date)

                            mediaList.add(media)
                        }
                    }

                    mediaList.sortByDescending { it.date }
                    return mediaList
                } else {
                    return getMediaQMinus(ctx).reversed().toMutableList()
                }
            }
            return mutableListOf()
        }

        override fun onPostExecute(result: MutableList<Media>?) {
            super.onPostExecute(result)

            result?.let { list ->
                mediaListFinal = list
                Log.e("TAG", "doInBackground: ${mediaListFinal}")
                block(mediaListFinal)
            }
        }
    }.execute("%${originalPath.name}%", false)
}

fun getMediaQMinus(ctx: Context): MutableList<Media> {
    val items = mutableListOf<Media>()

    originalPath.listFiles()?.forEach {
        val authority = ctx.packageName + ".provider"
        val mediaUri = FileProvider.getUriForFile(ctx, authority, it)
        items.add(Media(mediaUri, it.absolutePath, it.extension == "mp4", it.lastModified()))
    }

    return items
}

fun getMediaQMinus(ctx: Context, file: File): MutableList<Media> {
    val items = mutableListOf<Media>()

    file.listFiles()?.forEach {
        val authority = ctx.packageName + ".provider"
        val mediaUri = FileProvider.getUriForFile(ctx, authority, it)
        if (!it.absolutePath.contains(".noMedia", true)) {
            items.add(Media(mediaUri, it.absolutePath, it.extension == "mp4", it.lastModified()))
        }
    }

    return items
}

fun String.toTitleCase(): String? {
    var string = this
    // Check if String is null
    var whiteSpace = true
    val builder = StringBuilder(string) // String builder to store string
    val builderLength = builder.length

    // Loop through builder
    for (i in 0 until builderLength) {
        val c = builder[i] // Get character at builders position
        if (whiteSpace) {

            // Check if character is not white space
            if (!Character.isWhitespace(c)) {

                // Convert to title case and leave whitespace mode.
                builder.setCharAt(i, c.titlecaseChar())
                whiteSpace = false
            }
        } else if (Character.isWhitespace(c)) {
            whiteSpace = true // Set character is white space
        } else {
            builder.setCharAt(i, c.lowercaseChar()) // Set character to lowercase
        }
    }
    return builder.toString() // Return builders text
}