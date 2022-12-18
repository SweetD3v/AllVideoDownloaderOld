package com.video.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageVersionModel : Serializable {
    @SerializedName("candidates")
    public val candidates: MutableList<CandidatesModel>? = null
}