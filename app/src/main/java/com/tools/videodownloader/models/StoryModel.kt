package com.tools.videodownloader.models

import androidx.core.app.NotificationCompat
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StoryModel : Serializable {
    @SerializedName(NotificationCompat.CATEGORY_STATUS)
    public val status: String? = null

    @SerializedName("tray")
    public val tray: ArrayList<TrayModel>? = null
}