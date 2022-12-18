package com.video.tools.videodownloader.utils.apis

import com.video.tools.videodownloader.models.FullDetailModel
import com.video.tools.videodownloader.models.StoryModel
import com.video.tools.videodownloader.models.TwitterResponse
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.*

interface APIServices {
    @GET
    public fun callResult(
        @Url str: String?,
        @Header("Cookie") str2: String?,
        @Header("User-Agent") str3: String?
    ): Observable<JsonObject?>?

    @FormUrlEncoded
    @POST
    public fun callTwitter(
        @Url str: String?,
        @Field("id") str2: String?
    ): Observable<TwitterResponse?>?

    @GET
    public fun getFullDetailInfoApi(
        @Url str: String?,
        @Header("Cookie") str2: String?,
        @Header("User-Agent") str3: String?
    ): Observable<FullDetailModel?>?

    @GET
    public fun getStoriesApi(
        @Url str: String?,
        @Header("Cookie") str2: String?,
        @Header("User-Agent") str3: String?
    ): Observable<StoryModel?>?

    
}