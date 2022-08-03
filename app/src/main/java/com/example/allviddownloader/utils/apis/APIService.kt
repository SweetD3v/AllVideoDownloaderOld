package com.example.allviddownloader.utils.apis

import com.example.allviddownloader.models.WallModelPixabay
import com.example.allviddownloader.models.WallpaperModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface APIService {

    @GET("v1/curated")
    fun getAllWallpapers(
        @Header("Authorization") api_key: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<WallpaperModel>

    @GET("api")
    fun getAllWallpapersPixabay(
        @Query("key") api_key: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<WallModelPixabay>
}