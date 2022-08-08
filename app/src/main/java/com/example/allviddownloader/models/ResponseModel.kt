package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseModel : Serializable {
    @SerializedName("graphql")
    public val graphql: Graphql? = null
}