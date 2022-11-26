package com.internet.speed_meter

import android.content.Context
import android.net.TrafficStats
import android.net.wifi.WifiManager
import java.util.*

class TrafficUtils {

    companion object {
        val GB: Long = 1000000000
        val MB: Long = 1000000
        val KB: Long = 1000

        fun getNetworkSpeed(): String {

            var downloadSpeedOutput = ""
            var units = ""
            val mBytesPrevious = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()

            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            val mBytesCurrent = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()

            val mNetworkSpeed = mBytesCurrent - mBytesPrevious

            val mDownloadSpeedWithDecimals: Float

            if (mNetworkSpeed >= GB) {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / GB.toFloat()
                units = " GB"
            } else if (mNetworkSpeed >= MB) {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / MB.toFloat()
                units = " MB"

            } else {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / KB.toFloat()
                units = " KB"
            }


            downloadSpeedOutput = if (units != " KB" && mDownloadSpeedWithDecimals < 100) {
                String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals)
            } else {
                mDownloadSpeedWithDecimals.toInt().toString()
            }

            return (downloadSpeedOutput + units)

        }

        fun convertToBytes(value: Float, unit: String): Long {
            when (unit) {
                "KB" -> {
                    return (value.toLong()) * KB
                }
                "MB" -> {
                    return (value.toLong()) * MB
                }
                "GB" -> {
                    return (value.toLong()) * GB
                }
                else -> return 0
            }
        }

        fun isWifiConnected(context: Context): Boolean {
            val wifiMgr =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

            return if (wifiMgr!!.isWifiEnabled) { // Wi-Fi adapter is ON

                val wifiInfo = wifiMgr.connectionInfo

                wifiInfo.networkId != -1

            } else {
                false // Wi-Fi adapter is OFF
            }
        }

        fun getMetricData(bytes: Long): String {
            val dataWithDecimals: Float
            val units: String
            if (bytes >= GB) {
                dataWithDecimals = bytes.toFloat() / GB.toFloat()
                units = " GB"
            } else if (bytes >= MB) {
                dataWithDecimals = bytes.toFloat() / MB.toFloat()
                units = " MB"

            } else {
                dataWithDecimals = bytes.toFloat() / KB.toFloat()
                units = " KB"
            }


            val output = if (units != " KB" && dataWithDecimals < 100) {
                String.format(Locale.US, "%.1f", dataWithDecimals)
            } else {
                dataWithDecimals.toInt().toString()
            }

            return output + units
        }

    }
}