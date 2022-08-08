package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Edge : Serializable {
    @SerializedName("node")
    public val node: Node? = null
}