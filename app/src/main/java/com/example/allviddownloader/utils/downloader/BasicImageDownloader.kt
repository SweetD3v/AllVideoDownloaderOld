package com.example.allviddownloader.utils.downloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.example.allviddownloader.utils.AsyncTaskRunner
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class BasicImageDownloader(var ctx: Context) {
    private var mImageLoaderListener: OnImageLoaderListener? = null
    private val mUrlsInProgress: MutableSet<String> = HashSet()
    private val TAG = this.javaClass.simpleName

    fun BasicImageDownloader(@NonNull listener: OnImageLoaderListener?) {
        mImageLoaderListener = listener
    }

    interface OnImageLoaderListener {
        fun onError(error: ImageError?)
        fun onProgressChange(percent: Int)
        fun onComplete(result: Bitmap?)
    }

    @Throws(IOException::class)
    fun saveImageToExternal(imgUrl: String, file: File) {
        if (!file.exists())
            file.mkdirs()
        object : AsyncTaskRunner<String, String>(ctx) {
            override fun doInBackground(params: String?): String {
                val url = URL(imgUrl)
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                Log.e("TAG", "saveImageToExternal: ${image.width}")

                //Create Path to save Image
                val path = file //Creates app specific folder
                path.mkdirs()
                val imgName = "IMG_${System.currentTimeMillis()}"
                val imageFile = File(path, "$imgName.png") // Imagename.png
                val out = FileOutputStream(imageFile)
                try {
                    image.compress(Bitmap.CompressFormat.PNG, 100, out) // Compress Image
                    out.flush()
                    out.close()

                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.

                    return imageFile.absolutePath
                } catch (e: java.lang.Exception) {
                    throw IOException()
                }
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                result?.let {
                    Toast.makeText(ctx, "Photo saved!", Toast.LENGTH_SHORT).show()
                    MediaScannerConnection.scanFile(
                        ctx,
                        arrayOf(it),
                        null,
                        object : MediaScannerConnection.OnScanCompletedListener {
                            override fun onScanCompleted(path: String?, uri: Uri?) {
                                Log.i("ExternalStorage", "Scanned $path:")
                                Log.i("ExternalStorage", "-> uri=$uri")
                            }
                        })
                }
            }

        }.execute(imgUrl, true)
    }

    @Throws(IOException::class)
    fun saveImageToTemp(
        imgUrl: String,
        file: File,
        showProgress: Boolean,
        bitmap: (Bitmap) -> Unit,
        uri: (Uri) -> Unit
    ) {
        if (!file.exists())
            file.mkdirs()
        object : AsyncTaskRunner<String, String>(ctx) {
            override fun doInBackground(params: String?): String {
                val url = URL(imgUrl)
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                Log.e("TAG", "saveImageToExternal: ${image.width}")

                //Create Path to save Image
                val path = file //Creates app specific folder
                path.mkdirs()
                val imgName = "IMG_${System.currentTimeMillis()}"
                val imageFile = File(path, "$imgName.png") // Imagename.png
                val out = FileOutputStream(imageFile)
                try {
                    image.compress(Bitmap.CompressFormat.PNG, 100, out) // Compress Image
                    out.flush()
                    out.close()

                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.

                    return imageFile.absolutePath
                } catch (e: java.lang.Exception) {
                    throw IOException()
                }
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                result?.let {
                    val bmp = BitmapFactory.decodeFile(it)
                    bitmap(bmp)
                    uri(FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", File(it)))
                }
            }

        }.execute(imgUrl, showProgress)
    }

    @Throws(IOException::class)
    fun saveVideoToExternal(imgUrl: String) {
        val path = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "GGEZ"
        ) //Creates app specific folder
        path.mkdirs()
        val imgName = "VID_${System.currentTimeMillis()}"
        val imageFile = File(path, "$imgName.mp4") // Imagename.png

        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null


        object : AsyncTaskRunner<String?, String>(ctx) {
            override fun doInBackground(params: String?): String? {
                try {
                    val url = URL(imgUrl)
                    connection = url.openConnection() as HttpURLConnection
                    connection?.connect()
                    if (connection?.responseCode != HttpURLConnection.HTTP_OK) {
                        return ("Server returned HTTP " + connection?.responseCode
                                + " " + connection?.responseMessage)
                    }
                    val fileLength = connection!!.contentLength
                    input = connection?.inputStream

                    output = FileOutputStream(imageFile)
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input?.read(data).also { count = it!! } != -1) {
                        if (isShutdown()) {
                            input?.close()
                            return null
                        }
                        total += count.toLong()
//                        if (fileLength > 0) publishProgress((total * 100 / fileLength).toInt())
                        output?.write(data, 0, count)
                    }
                } catch (e: Exception) {
                    return e.toString()
                } finally {
                    try {
                        output?.close()
                        input?.close()
                    } catch (ignored: IOException) {
                    }
                    connection?.disconnect()
                }

                return imageFile.absolutePath
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.e("TAG", "onPostExecute: ${result}")
            }

        }.execute(imgUrl, true)


//        val out = FileOutputStream(imageFile)
//        try {
//            image.compress(Bitmap.CompressFormat.PNG, 100, out) // Compress Image
//            out.flush()
//            out.close()

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
//            MediaScannerConnection.scanFile(
//                ctx,
//                arrayOf(imageFile.absolutePath),
//                null,
//                object : MediaScannerConnection.OnScanCompletedListener {
//                    override fun onScanCompleted(path: String?, uri: Uri?) {
//                        Log.i("ExternalStorage", "Scanned $path:")
//                        Log.i("ExternalStorage", "-> uri=$uri")
//                    }
//                })
//        } catch (e: java.lang.Exception) {
//            throw IOException()
//        }
    }


    fun download(@NonNull imageUrl: String, displayProgress: Boolean) {
        if (mUrlsInProgress.contains(imageUrl)) {
            Log.w(
                TAG, "a download for this url is already running, " +
                        "no further download will be started"
            )
            return
        }

        var error: ImageError? = null
        object : AsyncTaskRunner<Void?, Bitmap?>(ctx) {
            override fun onPreExecute() {
                super.onPreExecute()
                mUrlsInProgress.add(imageUrl)
            }

            override fun shutdown() {
                super.shutdown()
                mUrlsInProgress.remove(imageUrl)
                mImageLoaderListener!!.onError(error)
            }

            protected fun onProgressUpdate(vararg values: Int) {
                mImageLoaderListener!!.onProgressChange(values[0])
            }

            override fun doInBackground(params: Void?): Bitmap? {
                var bitmap: Bitmap? = null
                var connection: HttpURLConnection? = null
                var is1: InputStream? = null
                var out: ByteArrayOutputStream? = null
                try {
                    connection = URL(imageUrl).openConnection() as HttpURLConnection
                    if (displayProgress) {
                        connection.connect()
                        val length: Int = connection.getContentLength()
                        if (length <= 0) {
                            error =
                                ImageError("Invalid content length. The URL is probably not pointing to a file")
                                    .setErrorCode(ImageError.ERROR_INVALID_FILE)
                            shutdown()
                        }
                        is1 = BufferedInputStream(connection.getInputStream(), 8192)
                        out = ByteArrayOutputStream()
                        val bytes = ByteArray(8192)
                        var count: Int
                        var read: Long = 0
                        while (is1.read(bytes).also { count = it } != -1) {
                            read += count.toLong()
                            out.write(bytes, 0, count)
//                            publishProgress((read * 100 / length).toInt())
                        }
                        bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
                    } else {
                        is1 = connection.getInputStream()
                        bitmap = BitmapFactory.decodeStream(is1)
                    }
                } catch (e: Throwable) {
                    if (!this.isShutdown()) {
                        error = ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION)
                        shutdown()
                    }
                } finally {
                    try {
                        if (connection != null) connection.disconnect()
                        if (out != null) {
                            out.flush()
                            out.close()
                        }
                        if (is1 != null) is1.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return bitmap
            }

        }.execute(null, false)
    }

    interface OnBitmapSaveListener {
        fun onBitmapSaved()
        fun onBitmapSaveError(error: ImageError?)
    }


    fun writeToDisk(
        @NonNull imageFile: File, @NonNull image: Bitmap,
        @NonNull listener: OnBitmapSaveListener,
        @NonNull format: Bitmap.CompressFormat?, shouldOverwrite: Boolean
    ) {
        if (imageFile.isDirectory()) {
            listener.onBitmapSaveError(
                ImageError(
                    "the specified path points to a directory, " +
                            "should be a file"
                ).setErrorCode(ImageError.ERROR_IS_DIRECTORY)
            )
            return
        }
        if (imageFile.exists()) {
            if (!shouldOverwrite) {
                listener.onBitmapSaveError(
                    ImageError(
                        "file already exists, " +
                                "write operation cancelled"
                    ).setErrorCode(ImageError.ERROR_FILE_EXISTS)
                )
                return
            } else if (!imageFile.delete()) {
                listener.onBitmapSaveError(
                    ImageError(
                        "could not delete existing file, " +
                                "most likely the write permission was denied"
                    )
                        .setErrorCode(ImageError.ERROR_PERMISSION_DENIED)
                )
                return
            }
        }
        val parent: File = imageFile.getParentFile()
        if (!parent.exists() && !parent.mkdirs()) {
            listener.onBitmapSaveError(
                ImageError("could not create parent directory")
                    .setErrorCode(ImageError.ERROR_PERMISSION_DENIED)
            )
            return
        }
        try {
            if (!imageFile.createNewFile()) {
                listener.onBitmapSaveError(
                    ImageError("could not create file")
                        .setErrorCode(ImageError.ERROR_PERMISSION_DENIED)
                )
                return
            }
        } catch (e: IOException) {
            listener.onBitmapSaveError(ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION))
            return
        }

        var error: ImageError? = null
        object : AsyncTaskRunner<Void?, Void?>(ctx) {
            override fun shutdown() {
                super.shutdown()
                listener.onBitmapSaveError(error)
            }

            override fun doInBackground(params: Void?): Void? {
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(imageFile)
                    image.compress(format, 100, fos)
                } catch (e: IOException) {
                    error = ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION)
                    shutdown()
                } finally {
                    if (fos != null) {
                        try {
                            fos.flush()
                            fos.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                listener.onBitmapSaved()
            }

        }.execute(null, false)
    }

    fun readFromDisk(@NonNull imageFile: File): Bitmap? {
        return if (!imageFile.exists() || imageFile.isDirectory()) null else BitmapFactory.decodeFile(
            imageFile.getAbsolutePath()
        )
    }

    interface OnImageReadListener {
        fun onImageRead(bitmap: Bitmap?)
        fun onReadFailed()
    }

    fun readFromDiskAsync(@NonNull imageFile: File, @NonNull listener: OnImageReadListener) {
        object : AsyncTaskRunner<String?, Bitmap?>(ctx) {
            override fun doInBackground(params: String?): Bitmap? {
                return BitmapFactory.decodeFile(params)
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                result?.let { bitmap ->
                    listener.onImageRead(bitmap)
                } ?: run {
                    listener.onReadFailed()
                }
            }
        }.execute(imageFile.absolutePath, false)
    }

    class ImageError : Throwable {
        var errorCode = 0
            private set

        constructor(@NonNull message: String?) : super(message) {}
        constructor(@NonNull error: Throwable) : super(error.message, error.cause) {
            stackTrace = error.stackTrace
        }

        fun setErrorCode(code: Int): ImageError {
            errorCode = code
            return this
        }

        companion object {
            const val ERROR_GENERAL_EXCEPTION = -1
            const val ERROR_INVALID_FILE = 0
            const val ERROR_DECODE_FAILED = 1
            const val ERROR_FILE_EXISTS = 2
            const val ERROR_PERMISSION_DENIED = 3
            const val ERROR_IS_DIRECTORY = 4
        }
    }
}