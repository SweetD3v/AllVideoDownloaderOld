package com.example.allviddownloader.utils.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestApi {
    companion object {
        private val BASE_URL_WALLPAPER = "https://api.pexels.com/"
        public val API_KEY_WALLPAPERS = "563492ad6f91700001000001c8bc8907dfbd452680e58db8be4a3fab"
        private val BASE_URL_WALLPAPER_PIXABAY = "https://pixabay.com/"
        public val API_KEY_WALLPAPERS_PIXABAY = "5219035-eeb94b847e428b10ae7b42c0c"

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_WALLPAPER_PIXABAY)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var service: APIService = retrofit.create(APIService::class.java)
    }
}