package com.example.allviddownloader.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.allviddownloader.R
import java.io.*
import java.text.DecimalFormat
import java.util.concurrent.Executors

class FileUtilsss {

    companion object {
        interface CopyStatusListener {
            fun onCopyComplete(message: String?)
        }

        fun copyFileAPI30(
            ctx: Context,
            inputUri: Uri,
            outputVideoFile: File,
            file: (File) -> Unit
        ) {
            if (!outputVideoFile.parentFile.exists())
                outputVideoFile.parentFile.mkdirs()
            if (!outputVideoFile.exists())
                outputVideoFile.createNewFile()
            object : AsyncTaskRunner<Uri, File>(ctx) {
                override fun doInBackground(params: Uri?): File? {
                    try {
                        val parcelFileDescriptor: ParcelFileDescriptor? =
                            ctx.contentResolver.openFileDescriptor(inputUri, "r")
                        parcelFileDescriptor?.let { pfd ->
                            val inStream: InputStream =
                                FileInputStream(pfd.fileDescriptor)
                            val outStream: OutputStream = FileOutputStream(outputVideoFile)
                            val buff = ByteArray(1024)
                            var len: Int
                            while (inStream.read(buff).also { len = it } > 0) {
                                outStream.write(buff, 0, len)
                            }
                            inStream.close()
                            outStream.close()

                            return outputVideoFile
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    return null
                }

                override fun onPostExecute(result: File?) {
                    super.onPostExecute(result)

                    result?.let {
                        if (it.exists()) {
                            file(it)
                        }
                    }
                }

            }.execute(inputUri, false)

        }

        @Throws(IOException::class)
        fun copyFile(
            context: Context,
            uri: Uri?,
            dst: File,
            filePath: (String) -> Unit
        ): Boolean {
            if (!dst.exists())
                dst.createNewFile()
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                uri!!, "r"
            )
            val is1: InputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)
            val os: OutputStream = FileOutputStream(dst)
            val buff = ByteArray(1024)
            val size = is1.available()
            var len: Int
            while (is1.read(buff).also { len = it } > 0) {
                os.write(buff, 0, len)
            }
            is1.close()
            os.close()
            filePath(dst.absolutePath)
            return true
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

        @Throws(IOException::class)
        fun saveBitmapAsFileTools(
            context: Context,
            bitmap: Bitmap?,
            fileName: String,
            dirName: String,
            pathExported: (String) -> Unit
        ): File {
            val executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            val handler = Handler(Looper.getMainLooper())
            val fileOutputStream: FileOutputStream
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                        + File.separator + context.getString(R.string.app_name)
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            if (!File(file, dirName).exists())
                File(file, dirName).mkdirs()
            val file1 =
                File(file.absolutePath + File.separator + dirName + File.separator + fileName + ".jpg")
            file1.createNewFile()
            fileOutputStream = FileOutputStream(file1)
            executor.execute {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                handler.post {
                    try {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    pathExported(file1.absolutePath)
                }
            }
            return file1
        }

        @Throws(IOException::class)
        fun saveBitmapAsFile(context: Context, bitmap: Bitmap?, fileName: String): File {
            val executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            val handler = Handler(Looper.getMainLooper())
            val fileOutputStream: FileOutputStream
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                        + File.separator + context.getString(R.string.app_name)
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            val name = fileName.replace("(\\W|^_)*".toRegex(), "_")
            var file1 = File(file.absolutePath + File.separator + name + ".jpg")
            file1.createNewFile()
            fileOutputStream = FileOutputStream(file1)
            executor.execute {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                handler.post {
                    try {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return file1
        }

        @Throws(IOException::class)
        fun saveBitmapAsFileWA(context: Context, bitmap: Bitmap?, fileName: String): File {
            val executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            val handler = Handler(Looper.getMainLooper())
            val fileOutputStream: FileOutputStream
            val file = RootDirectoryWhatsappShow
            if (!file.exists()) {
                file.mkdirs()
            }
            val name = fileName.replace("(\\W|^_)*".toRegex(), "_")
            var file1 = File(file.absolutePath + File.separator + name + ".jpg")
            file1.createNewFile()
            fileOutputStream = FileOutputStream(file1)
            executor.execute {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                handler.post {
                    try {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return file1
        }

        @Throws(IOException::class)
        fun saveBitmapAsFileDir(context: Context, bitmap: Bitmap?, dirName: String): File {
            val executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            val handler = Handler(Looper.getMainLooper())
            val fileOutputStream: FileOutputStream
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                        + File.separator + context.getString(R.string.app_name)
                        + File.separator + dirName
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            val name = "IMG_${System.currentTimeMillis()}"
            var file1 = File(file.absolutePath + File.separator + name + ".jpg")
            file1.createNewFile()
            fileOutputStream = FileOutputStream(file1)
            executor.execute {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                handler.post {
                    try {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return file1
        }

        @Throws(IOException::class)
        fun saveBitmapAsFileCache(
            context: Context,
            bitmap: Bitmap?,
            fileName: String,
            filePath: (String) -> Unit
        ): File {
            val executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            val handler = Handler(Looper.getMainLooper())
            val fileOutputStream: FileOutputStream
            val file = cachePathWA

            if (!file.exists()) {
                file.mkdirs()
            }
            val name = fileName.replace("(\\W|^_)*".toRegex(), "_")
            var file1 = File(file.absolutePath + File.separator + name + ".jpg")
            file1.createNewFile()
            fileOutputStream = FileOutputStream(file1)
            executor.execute {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                handler.post {
                    try {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    filePath(file1.absolutePath)
                }
            }
            return file1
        }

        fun saveBitmapAPI30(
            context: Context, bitmap: Bitmap?,
            displayName: String,
            mimeType: String,
            directory: File,
            action: (Uri) -> Unit
        ) {
            val executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            val handler = Handler(Looper.getMainLooper())
            executor.execute {
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + File.separator
                            + context.getString(R.string.app_name) + File.separator + directory.name
                )
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
                handler.post {
                    action(uri!!)
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Throws(FileNotFoundException::class)
        fun saveVideoAPI30(
            context: Context,
            file: File?,
            fileName: String?,
            destinationFile: File?,
            path: (String) -> Unit
        ) {
            val valuesvideos: ContentValues
            valuesvideos = ContentValues()
            valuesvideos.put(
                MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM
                        + File.separator + context.getString(R.string.app_name) + File.separator + destinationFile?.name
            )
            valuesvideos.put(MediaStore.Video.Media.TITLE, "${fileName}.mp4")
            valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, "${fileName}.mp4")
            valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            valuesvideos.put(
                MediaStore.Video.Media.DATE_ADDED,
                System.currentTimeMillis() / 1000
            )
            valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
            valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 1)
            val resolver = context.contentResolver
            val collection =
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uriSavedVideo = resolver.insert(collection, valuesvideos)
            val pfd = context.contentResolver.openFileDescriptor(uriSavedVideo!!, "w")
            val executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            val handler = Handler(Looper.getMainLooper())
            executor.execute {
                if (pfd != null) {
                    try {
                        val out =
                            FileOutputStream(pfd.fileDescriptor)
                        val inputStream = FileInputStream(file)
                        val buf = ByteArray(8192)
                        var len: Int
                        while (inputStream.read(buf).also { len = it } > 0) {
                            out.write(buf, 0, len)
                        }
                        out.close()
                        inputStream.close()
                        pfd.close()
                        valuesvideos.clear()
                        valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    handler.post {
                        context.contentResolver.update(uriSavedVideo, valuesvideos, null, null)
                        path(
                            File(
                                Environment.DIRECTORY_DCIM
                                        + File.separator + context.getString(R.string.app_name) + File.separator + destinationFile,
                                fileName.toString()
                            ).absolutePath
                        )
                    }
                }
            }
        }

        @Throws(IOException::class)
        fun copy(src: File?, dst: File?) {
            FileInputStream(src).use { input ->
                FileOutputStream(dst).use { out ->
                    val buf = ByteArray(4096)
                    var len: Int
                    while (input.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }
                }
            }
        }

        @Throws(IOException::class)
        fun saveBitmapAsFileA10(
            context: Context,
            bitmap: Bitmap?,
            fileName: String,
            dirName: String,
            pathExported: (String) -> Unit
        ): File {
            val fileOutputStream: FileOutputStream
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                        + File.separator + context.getString(R.string.app_name)
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            if (!File(file, dirName).exists())
                File(file, dirName).mkdirs()
            val file1 =
                File(file.absolutePath + File.separator + dirName + File.separator + fileName + ".jpg")
            file1.createNewFile()
            fileOutputStream = FileOutputStream(file1)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            try {
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            pathExported(file1.absolutePath)
            return file1
        }

        fun getFileLength(size: Long?): String {
            var lenStr = ""
            size?.let { length ->
                val formater = DecimalFormat("#0.##")
                lenStr = if (length < 1024.0) {
                    formater.format(length) + " Byte"
                } else if (length < 1024.0 * 1024.0) {
                    formater.format((length / 1024.0).toDouble()) + " KB"
                } else if (length < 1024.0 * 1024.0 * 1024.0) {
                    formater.format(length / (1024.0 * 1024.0)) + " MB"
                } else {
                    formater.format(length / (1024.0 * 1024.0 * 1024.0)) + " GB"
                }
            }
            return lenStr
        }
    }
}