package com.internet.speed_meter

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import java.util.*

class SpeedMeterService : Service() {
    private val timer by lazy { Timer() }
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "traffic_service"

    private val notificationLayout by lazy {
        RemoteViews(
            packageName,
            R.layout.net_speed_notification_view
        )
    }

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val builder by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(
                    Icon.createWithBitmap(
                        ImageUtils.createBitmapFromString(
                            this,
                            "0",
                            "KB"
                        )
                    )
                )
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(false)
                .setAutoCancel(true)
                .setCustomContentView(notificationLayout)
                .setContentIntent(createPendingIntent())
        } else {
            Notification.Builder(this)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentIntent(createPendingIntent())
        }
    }

    override
    fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_ID, "Traffic Status Service")
        }

        startForeground(NOTIFICATION_ID, builder.build())

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val downloadSpeed = TrafficUtils.getNetworkSpeed()
                saveToDB(downloadSpeed)
                updateNotification(downloadSpeed)
            }
        }, 0, 500)
    }

    fun saveToDB(downloadSpeed: String) {
        val speed: String =
            (downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ") + 1)).toString()
        val units: String = (downloadSpeed.subSequence(
            downloadSpeed.indexOf(" ") + 1,
            downloadSpeed.length
        )).toString()

        val toBytes = TrafficUtils.convertToBytes(speed.toFloat(), units)
//        Log.e("TAG", "showInternetSpeed: $toBytes")
    }

    private fun updateNotification(downloadSpeed: String) {
        val speed = downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ") + 1)
        val units = downloadSpeed.subSequence(downloadSpeed.indexOf(" ") + 1, downloadSpeed.length)

        val bitmap = ImageUtils.createBitmapFromString(this, speed.toString(), units.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val icon = Icon.createWithBitmap(bitmap)
            builder.setSmallIcon(icon)
        }

        notificationLayout.setTextViewText(R.id.custom_notification_speed_tv, "$downloadSpeed/s")
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("traffic_service", "Traffic Status Service")
        }
        startForeground(NOTIFICATION_ID, builder.build())
        return START_STICKY
    }

    private fun createPendingIntent(): PendingIntent? {
        val intent =
            Intent(this, Class.forName("com.tools.videodownloader.ui.activities.MainActivity"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_LOW
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(chan)
        return channelId
    }
}