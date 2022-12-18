package com.video.tools.videodownloader.interfaces

import com.video.tools.videodownloader.models.TrayModel

interface UserListInterface {
    fun userListClick(i: Int, trayModel: TrayModel?)
}