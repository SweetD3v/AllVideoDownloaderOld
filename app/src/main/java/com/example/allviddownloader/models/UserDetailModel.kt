package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserDetailModel : Serializable {
    @SerializedName("user")
    public val user: User? = null
}
