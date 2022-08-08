package com.example.allviddownloader.utils.apis

import com.example.allviddownloader.models.WallModelPixabay
import com.example.allviddownloader.models.WallpaperModel
import com.example.allviddownloader.models.ringtone.RingPreviewModel
import com.example.allviddownloader.models.ringtone.RingtoneModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
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
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<WallModelPixabay>

    @GET("search/text?")
    fun getAllRingtones(
        @Query("token") token: String,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("page_size") perPage: Int
    ): Call<RingtoneModel>

    @GET("sounds/{slug}")
    fun getRingtoneFromId(
        @Path(value = "slug", encoded = true) id: Long,
        @Query("token") token: String
    ): Call<RingPreviewModel>
}