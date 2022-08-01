package com.example.allviddownloader.utils.apis

import android.app.Activity
import android.util.Log
import com.example.allviddownloader.models.FullDetailModel
import com.example.allviddownloader.models.StoryModel
import com.example.allviddownloader.models.TwitterResponse
import com.example.allviddownloader.utils.isNullOrEmpty
import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class CommonClassForAPI {
    companion object {
        private var CommonClassForAPI: CommonClassForAPI? = null
        private var mActivity: Activity? = null

        fun getInstance(activity: Activity?): CommonClassForAPI? {
            if (CommonClassForAPI == null) {
                CommonClassForAPI = CommonClassForAPI()
            }
            mActivity = activity
            return CommonClassForAPI
        }
    }

    fun callResult(
        disposableObserver: DisposableObserver<JsonObject>,
        str: String?,
        str2: String?
    ) {
        var str2 = str2
        if (isNullOrEmpty(str2)) {
            str2 = ""
        }
        RestClient.getInstance(mActivity).getService().callResult(
            str,
            str2,
            "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+"
        )?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<JsonObject?> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(jsonObject: JsonObject) {
                    disposableObserver.onNext(jsonObject)
                }

                override fun onError(th: Throwable) {
                    disposableObserver.onError(th)
                }

                override fun onComplete() {
                    disposableObserver.onComplete()
                }
            })
    }

    fun callTwitterApi(disposableObserver: DisposableObserver<Any>, str: String?, str2: String?) {
        RestClient.getInstance(mActivity).getService().callTwitter(str, str2)
            ?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<TwitterResponse?> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(twitterResponse: TwitterResponse) {
                    disposableObserver.onNext(twitterResponse)
                }

                override fun onError(th: Throwable) {
                    disposableObserver.onError(th)
                }

                override fun onComplete() {
                    disposableObserver.onComplete()
                }
            })
    }

    fun getStories(disposableObserver: DisposableObserver<StoryModel>, str: String?) {
        Log.e("TAG", "getStories: $str")
        if (isNullOrEmpty(str)) {
            return
        }
        RestClient.getInstance(mActivity).getService().getStoriesApi(
            "https://i.instagram.com/api/v1/feed/reels_tray/",
            str,
            "\"Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+\""
        )?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<StoryModel?> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(storyModel: StoryModel) {
                    Log.e("TAG", "onNext: ")
                    disposableObserver.onNext(storyModel)
                }

                override fun onError(th: Throwable) {
                    disposableObserver.onError(th)
                }

                override fun onComplete() {
                    disposableObserver.onComplete()
                }
            })
    }

    fun getFullDetailFeed(
        disposableObserver: DisposableObserver<FullDetailModel>,
        str: String,
        str2: String?
    ) {
        val service: APIServices = RestClient.getInstance(mActivity).getService()
        service.getFullDetailInfoApi(
            "https://i.instagram.com/api/v1/users/$str/full_detail_info?max_id=",
            str2,
            "\"Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+\""
        )?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<FullDetailModel?> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(fullDetailModel: FullDetailModel) {
                    disposableObserver.onNext(fullDetailModel)
                }

                override fun onError(th: Throwable) {
                    disposableObserver.onError(th)
                }

                override fun onComplete() {
                    disposableObserver.onComplete()
                }
            })
    }
}