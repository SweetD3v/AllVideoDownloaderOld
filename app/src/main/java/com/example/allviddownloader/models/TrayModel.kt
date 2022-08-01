package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TrayModel : Serializable {
    @SerializedName("id")
    public var id: String? = null

    @SerializedName("items")
    public var items: ArrayList<ItemModel?>? = null

    @SerializedName("media_count")
    public var media_count = 0

    @SerializedName("user")
    public var user: UserModel? = null
}