package com.video.tools.videodownloader.utils.apis

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class RestClient() {
    private var retrofit: Retrofit? = null

    companion object {
        private var mActivity: Activity? = null
        private val restClient: RestClient = RestClient()
        open fun getInstance(activity: Activity?): RestClient {
            mActivity = activity
            return restClient
        }
    }

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val build: OkHttpClient =
            OkHttpClient.Builder().readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES).addInterceptor(
                    Interceptor { chain -> newRestClient(chain)!! })
                .addInterceptor(httpLoggingInterceptor).build()
        if (retrofit == null) {
            retrofit = Retrofit.Builder().baseUrl("https://www.instagram.com/")
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(build).build()
        }
    }

    @Throws(IOException::class)
    fun newRestClient(chain: Interceptor.Chain): Response? {
        var response: Response? = null
        try {
            response = chain.proceed(chain.request())
            if (response.code == 200) {
                Log.e("TAG", "newRestClient: ${response.body!!.string()}")
                try {
                    val jSONObject = JSONObject(response.body!!.string()).toString()
                    printMsg(jSONObject + "")
                    return response.newBuilder()
                        .body(ResponseBody.create(response.body!!.contentType(), jSONObject))
                        .build()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e2: SocketTimeoutException) {
            e2.printStackTrace()
        }
        return response
    }

    fun getService(): APIServices {
        return retrofit!!.create(APIServices::class.java) as APIServices
    }

    private fun printMsg(str: String) {
        val length = str.length / 4050
        var i = 0
        while (i <= length) {
            val i2 = i + 1
            val i3 = i2 * 4050
            if (i3 >= str.length) {
                Log.d("Response::", str.substring(i * 4050))
            } else {
                Log.d("Response::", str.substring(i * 4050, i3))
            }
            i = i2
        }
    }
}