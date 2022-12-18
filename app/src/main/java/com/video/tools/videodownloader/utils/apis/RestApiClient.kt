package com.video.tools.videodownloader.utils.apis

import com.video.tools.videodownloader.AllVidApp
import com.video.tools.videodownloader.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RestApiClient {
    companion object {
        private val BASE_URL_INSTA = AllVidApp.getInstance().getString(R.string.base_url_insta)
        private val BASE_URL_FB = AllVidApp.getInstance().getString(R.string.base_url_fb)

        enum class SOCIAL_TYPE {
            INSTA,
            FB
        }

        var socialType: SOCIAL_TYPE? = SOCIAL_TYPE.INSTA

        fun getInstance(socialType: SOCIAL_TYPE): RestApiClient {
            Companion.socialType = socialType
            return RestApiClient()
        }
    }

    val retrofit: Retrofit =
        if (socialType == SOCIAL_TYPE.INSTA)
            Retrofit.Builder()
                .baseUrl(BASE_URL_INSTA)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        else Retrofit.Builder()
            .baseUrl(BASE_URL_FB)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    var service: APIService = retrofit.create(APIService::class.java)
}