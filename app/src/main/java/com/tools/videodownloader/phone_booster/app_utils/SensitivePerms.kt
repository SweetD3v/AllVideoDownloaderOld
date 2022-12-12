package com.tools.videodownloader.phone_booster.app_utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tools.videodownloader.phone_booster.models.AppModel
import com.tools.videodownloader.utils.formatSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.ceil
import kotlin.math.floor

val sensitivePerms = arrayListOf(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.READ_CONTACTS,
    Manifest.permission.READ_PHONE_STATE
)

val batteryPerms = arrayListOf(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

fun getAllAppsPermissions(
    activity: AppCompatActivity,
    appsList: (MutableList<AppModel>) -> Unit
) {
    activity.lifecycleScope.launch(Dispatchers.Main) {
        var permissionsList: MutableList<AppModel>
        withContext(Dispatchers.IO) {
            permissionsList = getInstalledApps(activity, false).toMutableList()
            permissionsList.forEach { appModel ->
                var isSensitive = false
                for (permission in appModel.permissions) {
                    if (sensitivePerms.contains(permission)) {
                        isSensitive = true
                        break
                    }
                }
                appModel.isSensitive = isSensitive
            }
        }
        appsList(permissionsList)
    }
}

fun getInstalledApps(ctx: Context, showSystem: Boolean): List<AppModel> {
    val arrayList: MutableList<AppModel> = ArrayList()
    val packageManager = ctx.packageManager
    val installedPackages: List<PackageInfo> =
        packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
    val size = installedPackages.size
    var i = 0
    while (i < size) {
        val packageInfo: PackageInfo = installedPackages[i]
        if (!showSystem) {
            val applicationInfo = packageManager.getApplicationInfo(packageInfo.packageName, 0)
            if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                val sourceDir = applicationInfo.sourceDir
                if (!sourceDir.startsWith("/vendor/", false)) {
                    val appModel = AppModel()
                    appModel.appName =
                        packageInfo.applicationInfo.loadLabel(packageManager).toString()
                    val packageName: String = packageInfo.packageName
                    if (packageName.isNotEmpty()) {
                        appModel.packageName = packageName
                        val versionName: String = packageInfo.versionName
                        if (versionName.isNotEmpty()) {
                            appModel.versionName = versionName
                        }
                        appModel.icon = packageInfo.applicationInfo.loadIcon(packageManager)

                        val file = File(applicationInfo.sourceDir)
                        val size: Long = file.length()

                        Log.e("TAG", "getInstalledAppsSize: $size")

                        appModel.appSize = size.formatSize()
                        appModel.sizeLong = size

                        val permInfo: PackageInfo = packageManager.getPackageInfo(
                            applicationInfo.packageName,
                            PackageManager.GET_PERMISSIONS
                        )
                        if (permInfo.requestedPermissions != null) {
                            appModel.permissions = permInfo.requestedPermissions
                        }
                        arrayList.add(appModel)
                    }
                }
            }
        }
        i++
    }
    arrayList.sortedBy { it.appName }
    return arrayList
}

fun getCleanableSize(activity: AppCompatActivity, size: (Long) -> Unit) {
    var totalSize = 0L
    activity.lifecycleScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            val permissionsList = getInstalledApps(activity, false).toMutableList()
            permissionsList.forEach { appModel ->
                totalSize = permissionsList.sumOf { it.sizeLong }
            }
        }
        size(totalSize)
    }
}

fun getStorageOccupiedSize(): Long {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    val totalSize = stat.blockCountLong * stat.blockSizeLong
    val freeSize = stat.availableBytes
    return totalSize - freeSize
}

fun getStorageFreeSize(): Long {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    val freeSize = stat.availableBytes
    return freeSize
}

fun getStorageOccupiedSizePercent(): String {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    val totalSize = stat.blockCountLong * stat.blockSizeLong
    val freeSize = stat.availableBytes
    return "${floor(((totalSize - freeSize) * 100.0 / totalSize)).toInt()} %"
}

fun getStorageFreeSizePercent(): String {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    val totalSize = stat.blockCountLong * stat.blockSizeLong
    val freeSize = stat.availableBytes
    return "${ceil((freeSize * 100.0 / totalSize)).toInt()} %"
}