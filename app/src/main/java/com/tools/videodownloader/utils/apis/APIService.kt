package com.tools.videodownloader.utils.apis

import com.tools.videodownloader.models.WallModelPixabay
import com.tools.videodownloader.models.WallpaperModel
import com.tools.videodownloader.models.ringtone.RingPreviewModel
import com.tools.videodownloader.models.ringtone.RingtoneModel
import retrofit2.Call
import retrofit2.http.*

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

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST
    fun getMediaUrlFacebook(
        @Header("X-RapidAPI-Key") rapid_key: String,
        @Header("X-RapidAPI-Host") rapid_host: String,
        @Field("URL") url: String
    ): Call<FBModel>

    @GET("index")
    fun getMediaUrlInstagram(
        @Header("X-RapidAPI-Key") rapid_key: String,
        @Header("X-RapidAPI-Host") rapid_host: String,
        @Query("url") mediaLink: String
    ): Call<InstaModel>
}