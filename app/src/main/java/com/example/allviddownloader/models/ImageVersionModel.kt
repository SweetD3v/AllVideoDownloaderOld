package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageVersionModel : Serializable {
    @SerializedName("candidates")
    public val candidates: MutableList<CandidatesModel>? = null
}