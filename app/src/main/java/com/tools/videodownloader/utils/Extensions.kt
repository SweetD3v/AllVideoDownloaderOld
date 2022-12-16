package com.tools.videodownloader.utils

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.tools.videodownloader.AllVidApp
import com.tools.videodownloader.R
import com.tools.videodownloader.models.Media
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

var RECEIVER_ADDRESS = "andro.ops151@gmail.com"
var RootDirectoryFacebook =
    AllVidApp.getInstance().resources.getString(R.string.app_name) + "/Facebook/"
val originalPath =
    File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        AllVidApp.getInstance().resources.getString(R.string.app_name)
    )
val cachePathWA =
    File(
        AllVidApp.getInstance().cacheDir,
        "temp_wa"
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

var RootDirectoryWallpapers = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Wallpapers"
)

var RootDirectoryFacts = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Facts"
)
var RootDirectoryStatus = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Status"
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
public var RootDirectoryInstaDownlaoder = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Insta Downloader"
)

public var RootDirectoryFunny = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Funny Videos"
)

public var RootDirectoryFBDownlaoder = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "FB Downloader"
)

public var RootDirectoryCompressedVideo = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Compressed Video"
)

public var RootDirectoryCompressedPhoto = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Compressed Photo"
)

public var RootDirectoryCartoonified = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Cartoonified"
)
public var RootDirectoryCollageMaker = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Collage Maker"
)

public var RootDirectoryPhotoEditor = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Photo Editor"
)

public var RootDirectorySketchified = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "Skecthified"
)

public var RootDirectoryPhotoFilter = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "PhotoFilter"
)

public var RootDirectoryPhotoWarp = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            + File.separator + AllVidApp.getInstance()
        .getString(R.string.app_name) + File.separator + "PhotoWarp"
)

fun File.getProperSize(countHiddenItems: Boolean): Long {
    return if (isDirectory) {
        getDirectorySize(this, countHiddenItems)
    } else {
        length()
    }
}

private fun getDirectorySize(dir: File, countHiddenItems: Boolean): Long {
    var size = 0L
    if (dir.exists()) {
        val files = dir.listFiles()
        if (files != null) {
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    size += getDirectorySize(files[i], countHiddenItems)
                } else if (!files[i].name.startsWith('.') && !dir.name.startsWith('.') || countHiddenItems) {
                    size += files[i].length()
                }
            }
        }
    }
    return size
}

fun Long.formatSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return "${
        DecimalFormat("#,##0.##").format(
            this / 1024.0.pow(digitGroups.toDouble())
        )
    } ${units[digitGroups]}"
}

fun saveBitmapImage(
    context: Context, bitmap: Bitmap?,
    displayName: String,
    directory: String,
    path: (String) -> Unit
) {
    val values = ContentValues()
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
    values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        values.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DCIM + File.separator
                    + context.getString(R.string.app_name) + File.separator
                    + File(directory).name
        )
    } else {
        values.put(
            MediaStore.MediaColumns.DATA,
            Environment.DIRECTORY_DCIM + File.separator + File(directory).name
        )
    }
    val uri = context.contentResolver
        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    try {
        val stream =
            context.contentResolver.openOutputStream(uri!!)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    } catch (e: Exception) {
        Log.e("TAG", "saveBitmapExc: ${e.message}")
        e.printStackTrace()
    }
    path(
        File(
            Environment.DIRECTORY_DCIM + File.separator
                    + context.getString(R.string.app_name) + File.separator
                    + File(directory).name
        ).absolutePath
    )
}

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

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
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

fun getVideoThumbUri(ctx: Context, path: String): Bitmap? {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = ctx.contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        filePathColumn,
        null,
        null,
        null
    )
    cursor?.use {
        it.moveToFirst()
//        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//        val picturePath = cursor.getString(columnIndex)
        it.close()

        return ThumbnailUtils.createVideoThumbnail(
            path,
            MediaStore.Video.Thumbnails.MICRO_KIND
        )
    }
    return null
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

fun getMedia(ctx: Context, file: File, block: (MutableList<Media>) -> Unit) {
    var mediaListFinal: MutableList<Media> = mutableListOf()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaStore.MediaColumns.RELATIVE_PATH + " LIKE ? "
        } else {
            MediaStore.Images.Media.DATA + " LIKE ? "
        }
        val selectionArgs = arrayOf("%${ctx.getString(R.string.app_name)}/${file.name}%")
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
                contentUri =
                    if (ctx.contentResolver.getType(uri)?.contains("video", true) == true) {
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
                    Media(
                        contentUri,
                        path,
                        ctx.contentResolver.getType(uri)?.contains("video", true) == true,
                        date
                    )

                Log.e("TAG", "loadinBG: ${media.path}")

                if (!File(path).isDirectory && File(path).exists())
                    mediaListFinal.add(media)
            }
        }

        mediaListFinal.sortByDescending { it.date }
        block(mediaListFinal)
    } else {
        if (file.exists()) {
            itemsFiles = mutableListOf()
            mediaListFinal = getMediaQMinus(ctx, file).reversed().toMutableList()
            block(mediaListFinal)
        }
    }

}

fun getMedia(ctx: Context, block: (MutableList<Media>) -> Unit) {
    var mediaListFinal: MutableList<Media>
    object : AsyncTaskRunner<String, MutableList<Media>>(ctx) {
        override fun doInBackground(params: String?): MutableList<Media> {
            if (originalPath.exists()) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
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
                            contentUri = if (ctx.contentResolver.getType(uri)
                                    ?.contains("video", true) == true
                            ) {
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
                                Media(
                                    contentUri,
                                    path,
                                    ctx.contentResolver.getType(uri)
                                        ?.contains("video", true) == true,
                                    date
                                )

                            if (!File(path).isDirectory)
                                mediaList.add(media)
                        }
                    }

                    mediaList.sortByDescending { it.date }
                    return mediaList
                } else {
                    itemsFiles = mutableListOf()
                    return getMediaQMinus(ctx, originalPath).reversed().toMutableList()
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

fun getMediaByName(ctx: Context, dirName: File, block: (MutableList<Media>) -> Unit) {
    var mediaListFinal: MutableList<Media>
    object : AsyncTaskRunner<String, MutableList<Media>>(ctx) {
        override fun doInBackground(params: String?): MutableList<Media> {
            if (dirName.exists()) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    val mediaList = mutableListOf<Media>()
                    val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.MediaColumns.RELATIVE_PATH + " LIKE ? "
                    } else {
                        MediaStore.Images.Media.DATA + " LIKE ? "
                    }
                    val selectionArgs =
                        if (params.toString() != ctx.getString(R.string.app_name)) {
                            arrayOf(
                                "%${
                                    ctx.getString(R.string.app_name)
                                }/${params}%"
                            )
                        } else arrayOf("%${ctx.getString(R.string.app_name)}%")

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
                            val uri = FileProvider.getUriForFile(
                                ctx,
                                "${ctx.packageName}.provider",
                                File(path)
                            )
                            var contentUri: Uri
                            contentUri =
                                if (ctx.contentResolver.getType(uri)
                                        ?.contains("video", true) == true
                                ) {
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
                                Media(
                                    contentUri,
                                    path,
                                    ctx.contentResolver.getType(uri)
                                        ?.contains("video", true) == true,
                                    date
                                )

                            if (!File(path).isDirectory)
                                mediaList.add(media)
                        }
                    }

                    mediaList.sortByDescending { it.date }
                    return mediaList
                } else {
                    itemsFiles = mutableListOf()
                    return getMediaQMinus(ctx, dirName).reversed().toMutableList()
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
    }.execute(dirName.name, false)
}

var itemsFiles = mutableListOf<Media>()

fun getMediaQMinus(ctx: Context): MutableList<Media> {
    val files = originalPath.listFiles()

    if (files != null) {
        val authority = ctx.packageName + ".provider"
        for (fileItem in files) {
            val mediaUri = FileProvider.getUriForFile(ctx, authority, fileItem)
            if (fileItem.isFile && fileItem.exists()) {
                val mid = fileItem.name.lastIndexOf(".")
                val ext = fileItem.name.substring(mid + 1, fileItem.name.length)

                if (ext.equals("jpg", true)
                    || ext.equals("png", true)
                    || ext.equals("jpeg", true)
                    || ext.equals("gif", true)
                ) {
                    itemsFiles.add(
                        Media(
                            mediaUri,
                            fileItem.absolutePath,
                            ctx.contentResolver.getType(mediaUri)?.contains("video", true) == true,
                            fileItem.lastModified()
                        )
                    )
                }
            } else {
                getMediaQMinus(ctx, fileItem)
            }
        }
    }
    return itemsFiles
}

fun getMediaQMinus(ctx: Context, file: File): MutableList<Media> {
    val files = file.listFiles()

    if (files != null) {
        val authority = ctx.packageName + ".provider"
        for (fileItem in files) {
            val mediaUri = FileProvider.getUriForFile(ctx, authority, fileItem)
            if (fileItem.isFile && fileItem.exists()) {
                val mid = fileItem.name.lastIndexOf(".")
                val ext = fileItem.name.substring(mid + 1, fileItem.name.length)

                if (!ext.equals(".noMedia", true)
                ) {
                    itemsFiles.add(
                        Media(
                            mediaUri,
                            fileItem.absolutePath,
                            ctx.contentResolver.getType(mediaUri)?.contains("video", true) == true,
                            fileItem.lastModified()
                        )
                    )
                }
            } else {
                getMediaQMinus(ctx, fileItem)
            }
        }
    }
    return itemsFiles
}

fun getMediaQMinusWA(ctx: Context, file: File): MutableList<Media> {
    val files = file.listFiles()

    if (files != null) {
        val authority = ctx.packageName + ".provider"
        for (fileItem in files) {
            val mediaUri = FileProvider.getUriForFile(ctx, authority, fileItem)
            if (fileItem.isFile && fileItem.exists()) {
                val mid = fileItem.name.lastIndexOf(".")
                val ext = fileItem.name.substring(mid + 1, fileItem.name.length)

                if (!ext.equals(".noMedia", true)
                ) {
                    itemsFiles.add(
                        Media(
                            mediaUri,
                            fileItem.absolutePath,
                            ctx.contentResolver.getType(mediaUri)?.contains("video", true) == true,
                            fileItem.lastModified()
                        )
                    )
                }
            } else {
                getMediaQMinus(ctx, fileItem)
            }
        }
    }
    return itemsFiles
}

fun shareMediaUri(
    context: Context,
    uriList: ArrayList<Uri>
) {
    var fileURI: Uri
    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = uriList.let {
            fileURI = it[0]
            context.contentResolver.getType(fileURI)
        }
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(
            Intent.EXTRA_SUBJECT,
            "Sharing file from the ${context.getString(R.string.app_name)}"
        )
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
    }
    context.startActivity(
        Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_media)
        )
    )
}

fun getMediaWA(ctx: Context, block: (MutableList<Media>) -> Unit) {
    val mediaListFinal: MutableList<Media> = mutableListOf()

    object : AsyncTaskRunner<Void?, MutableList<Media>>(ctx) {
        override fun doInBackground(params: Void?): MutableList<Media> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val persistedUri = ctx.contentResolver.persistedUriPermissions[0]
                persistedUri?.let {
                    val fromTreeUri = DocumentFile.fromTreeUri(
                        ctx,
                        it.uri
                    )

                    val listFiles = fromTreeUri?.listFiles()
                    if (listFiles != null) {
                        for (documentFile in listFiles) {
                            val uri = documentFile.uri
                            Log.e(
                                "TAG",
                                "loadImagesA30: ${
                                    ctx.contentResolver.getType(documentFile.uri)!!
                                        .contains("video")
                                }"
                            )
                            val status = Media(
                                uri,
                                uri.toString(),
                                ctx.contentResolver.getType(documentFile.uri)!!.contains("video"),
                                documentFile.lastModified()
                            )
                            if (!status.uri.toString().contains(".nomedia", true)) {
                                mediaListFinal.add(status)
                                Log.e("TAG", "doInBackground: ${mediaListFinal.size}")
                            }
                        }
                    }
                }
            } else {
                if (AppUtils.STATUS_DIRECTORY.exists()) {
                    itemsFiles = mutableListOf()
                    val imagesListNew = getMediaQMinus(ctx, AppUtils.STATUS_DIRECTORY)
                    for (media in imagesListNew) {
                        if (!media.isVideo) {
                            mediaListFinal.add(media)
                        }
                    }
                }
            }

            Log.e("TAG", "mediaListFinal: ${mediaListFinal.size}")
            return mediaListFinal
        }

        override fun onPostExecute(result: MutableList<Media>?) {
            super.onPostExecute(result)

            result?.let { list ->
                block(list)
            }
        }

    }.execute(null, false)
}

fun getMediaWACoroutine(
    ctx: Context,
    mediaList: (MutableList<Media>) -> Unit
) {
    val mediaListFinal: MutableList<Media> = mutableListOf()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        var persistedUri: Uri? = null
        ctx.contentResolver.persistedUriPermissions.forEach { uriPermission ->
            if (!uriPermission.uri.toString().contains(".w4b")) {
                persistedUri = uriPermission.uri
            }
        }
        if (persistedUri.toString() == "")
            mediaList(mutableListOf())
        persistedUri?.let {
            val fromTreeUri = DocumentFile.fromTreeUri(
                ctx,
                it
            )

            val listFiles = fromTreeUri?.listFiles()
            if (listFiles != null) {
                for (documentFile in listFiles) {
                    val uri = documentFile.uri
                    val status = Media(
                        uri,
                        uri.toString(),
                        ctx.contentResolver.getType(documentFile.uri)?.contains("video") == true,
                        documentFile.lastModified()
                    )
                    if (!status.uri.toString().contains(".nomedia", true)) {
                        mediaListFinal.add(status)
                    }
                }
                Log.e("TAG", "getMediaWACoroutine: ${mediaListFinal.size}")
            }
            mediaList(mediaListFinal)
        } ?: mediaList(mutableListOf())
    } else {
        if (AppUtils.STATUS_DIRECTORY.exists()) {
            itemsFiles = mutableListOf()
            val imagesListNew = getMediaQMinusWA(ctx, AppUtils.STATUS_DIRECTORY)
            mediaList(imagesListNew)
        } else mediaList(mutableListOf())
    }
}

fun getMediaWAAll(ctx: Context, block: (MutableList<Media>) -> Unit) {
    val mediaListFinal: MutableList<Media> = mutableListOf()

    object : AsyncTaskRunner<Void?, MutableList<Media>>(ctx) {
        override fun doInBackground(params: Void?): MutableList<Media> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val persistedUri = ctx.contentResolver.persistedUriPermissions[0]
                persistedUri?.let {
                    val fromTreeUri = DocumentFile.fromTreeUri(
                        ctx,
                        it.uri
                    )

                    val listFiles = fromTreeUri?.listFiles()
                    if (listFiles != null) {
                        for (documentFile in listFiles) {
                            val uri = documentFile.uri
                            Log.e(
                                "TAG",
                                "loadImagesA30: ${
                                    ctx.contentResolver.getType(documentFile.uri)!!
                                        .contains("video")
                                }"
                            )
                            val status = Media(
                                uri,
                                uri.toString(),
                                ctx.contentResolver.getType(documentFile.uri)!!.contains("video"),
                                documentFile.lastModified()
                            )
                            if (!status.uri.toString().contains(".nomedia", true)) {
                                mediaListFinal.add(status)
                                Log.e("TAG", "doInBackground: ${mediaListFinal.size}")
                            }
                        }
                    }
                }
            } else {
                if (AppUtils.STATUS_DIRECTORY.exists()) {
                    itemsFiles = mutableListOf()
                    val imagesListNew = getMediaQMinus(ctx, AppUtils.STATUS_DIRECTORY)
                    for (media in imagesListNew) {
                        if (ctx.contentResolver.getType(media.uri)?.contains("image") == true
                            || ctx.contentResolver.getType(media.uri)?.contains("video") == true
                        ) {
                            mediaListFinal.add(media)
                        }
                    }
                }
            }

            Log.e("TAG", "mediaListFinal: ${mediaListFinal.size}")
            return mediaListFinal
        }

        override fun onPostExecute(result: MutableList<Media>?) {
            super.onPostExecute(result)

            result?.let { list ->
                block(list)
            }
        }

    }.execute(null, false)
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

fun toastShort(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun toastLong(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun shareMedia(
    context: Context,
    uri: Uri?,
    filePath: String
) {
    var fileURI: Uri = "".toUri()
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type =
            uri?.let {
                fileURI = it
                context.contentResolver.getType(fileURI)
            }
                ?: let {
                    fileURI = FileProvider.getUriForFile(
                        context, context.packageName + ".provider",
                        File(filePath)
                    )
                    context.contentResolver.getType(fileURI)
                }
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(
            Intent.EXTRA_SUBJECT,
            "Sharing file from the ${context.getString(R.string.app_name)}"
        )
        putExtra(
            Intent.EXTRA_TEXT,
            "Sharing file from the ${context.getString(R.string.app_name)} with some description"
        )
        putExtra(Intent.EXTRA_STREAM, fileURI)
    }
    context.startActivity(
        Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_media)
        )
    )
}

fun RecyclerView.addOuterGridSpacing(spacing: Int) {
    this.setPadding(
        paddingStart + spacing,
        paddingTop,
        paddingEnd + spacing,
        paddingBottom + spacing
    )
}

fun Any.toBoolean() = toString() == "true"

fun Any.toInt() = Integer.parseInt(toString())

fun Any.toStringSet() = toString().split(",".toRegex()).toSet()

fun shareToInsta(
    ctx: Context,
    uriList: ArrayList<Uri>
) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.setPackage("com.instagram.android")
    try {
        shareIntent.putExtra(
            Intent.EXTRA_STREAM,
            uriList[0]
        )
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    shareIntent.type = "image/jpeg"
    ctx.startActivity(shareIntent)
}