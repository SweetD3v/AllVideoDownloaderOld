package com.tools.videodownloader.interfaces

import com.tools.videodownloader.models.TrayModel

interface UserListInterface {
    fun userListClick(i: Int, trayModel: TrayModel?)
}