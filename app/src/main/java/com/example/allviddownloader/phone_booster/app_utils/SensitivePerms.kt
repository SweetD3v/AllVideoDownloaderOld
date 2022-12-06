package com.example.allviddownloader.phone_booster.app_utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.example.allviddownloader.phone_booster.models.AppModel
import com.example.allviddownloader.utils.formatSize
import java.io.File

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

fun getAllAppsPermissions(ctx: Context): MutableList<AppModel> {
    val permissionsList = getInstalledApps(ctx, false).toMutableList()
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
    return permissionsList
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