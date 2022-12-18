package com.video.tools.videodownloader.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import com.video.tools.videodownloader.models.Status
import com.google.android.material.snackbar.Snackbar
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AppUtils {
    companion object {
        val USER_AGENT = "User-Agent"
        var APP_DIR: String? = null
        val MICRO_KIND = 3
        val MINI_KIND = 1
        val STATUS_DIRECTORY =
            File(Environment.getExternalStorageDirectory().absolutePath + File.separator.toString() + "WhatsApp/Media/.Statuses")

        fun copyFile(status: Status, context: Context?, relativeLayout: RelativeLayout?) {
            val str: String
            val file = File(APP_DIR)
            if (!file.exists() && !file.mkdirs()) {
                Snackbar.make(relativeLayout!!, "Something went wrong", -1).show()
            }
            val format: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            str = if (status.isVideo) {
                "VID_$format.mp4"
            } else {
                "IMG_$format.jpg"
            }
            val file2 = File(file.toString() + File.separator + str)
            try {
                if (status.isApi30) {
                    return
                }
                FileUtils.copyFile(status.file, file2)
                file2.setLastModified(System.currentTimeMillis())
                SingleMediaScanner(context, file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun showNotification(
            context: Context,
            relativeLayout: RelativeLayout,
            file: File,
            status: Status
        ) {
//            if (Build.VERSION.SDK_INT >= 26) {
//                makeNotificationChannel(context)
//            }
            val uriForFile: Uri = FileProvider.getUriForFile(
                context,
                "a.gautham.statusdownloader.provider",
                File(file.absolutePath)
            )
            val intent = Intent("android.intent.action.VIEW")
            if (status.isVideo) {
                intent.setDataAndType(uriForFile, "video/*")
            } else {
                intent.setDataAndType(uriForFile, "image/*")
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            val activity = PendingIntent.getActivity(context, 0, intent, 0)
//            val builder: NotificationCompat.Builder =
//                NotificationCompat.Builder(context, CHANNEL_NAME)
//            val contentTitle: NotificationCompat.Builder =
//                builder.setSmallIcon(R.drawable.ic_file_download_black).setContentTitle(file.name)
//            contentTitle.setContentText("File Saved to$APP_DIR").setAutoCancel(true)
//                .setContentIntent(activity)
//            (context.getSystemService("notification") as NotificationManager).notify(
//                Random().nextInt(),
//                builder.build()
//            )
            Snackbar.make(relativeLayout, "Saved to $APP_DIR", 0).show()
        }

        fun download(
            downloadPath: String?,
            destinationPath: String,
            context: Context,
            fileName: String
        ) {
            Toast.makeText(context, "Downloading started...", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse(downloadPath)
            val request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(fileName)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                destinationPath + fileName
            )
            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
        }

    }
}