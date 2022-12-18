package com.video.tools.videodownloader.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.video.tools.videodownloader.adapters.StoriesListAdapter
import com.video.tools.videodownloader.adapters.UserListAdapter
import com.video.tools.videodownloader.databinding.ActivityInstagramBinding
import com.video.tools.videodownloader.interfaces.Instagram_Story_Click
import com.video.tools.videodownloader.interfaces.UserListInterface
import com.video.tools.videodownloader.models.*
import com.video.tools.videodownloader.utils.*
import com.video.tools.videodownloader.models.*
import com.video.tools.videodownloader.utils.apis.CommonClassForAPI
import com.video.tools.videodownloader.utils.*
import com.video.tools.videodownloader.widgets.SliderLayoutManager
import io.reactivex.observers.DisposableObserver
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class InstagramActivity : AppCompatActivity(), Instagram_Story_Click {
    val binding by lazy { ActivityInstagramBinding.inflate(layoutInflater) }

    var commonClassForAPI: CommonClassForAPI? = null
    var downloadDataArrayList: MutableList<DownloadData> = mutableListOf()
    private var storyItemModelList: MutableList<ItemModel> = mutableListOf()
    var storyModels: MutableList<TrayModel> = mutableListOf()
    var paths = mutableListOf<String>()
    lateinit var storiesListAdapter: StoriesListAdapter
    var position: Int = 0
    var urlDownload: String? = null

    lateinit var permissionsList: MutableList<String>
    var permissionsStr = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var permissionsCount = 0

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    var alertDialog: AlertDialog? = null

    private fun showPermissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required")
            .setMessage("Some permissions are needed to be allowed to use this app without any problems.")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
        if (alertDialog == null) {
            alertDialog = builder.create()
            if (alertDialog?.isShowing == false) {
                alertDialog?.show()
            }
        }
    }

    private fun askForPermissions(permissionsList: MutableList<String>) {
        val newPermissionStr = ArrayList<String>(permissionsList.size)
        for (i in newPermissionStr.indices) {
            newPermissionStr[i] = permissionsList[i]
        }
        if (newPermissionStr.isNotEmpty()) {
            permissionsLauncher.launch(newPermissionStr.toTypedArray())
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showPermissionDialog()
        }
    }


    val permissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            object : ActivityResultCallback<Map<String, Boolean>> {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onActivityResult(result: Map<String, Boolean>?) {
                    val list: ArrayList<Boolean> = ArrayList(result!!.values)
                    permissionsList = ArrayList()
                    permissionsCount = 0
                    for (i in 0 until list.size) {
                        if (shouldShowRequestPermissionRationale(permissionsStr.get(i))) {
                            permissionsList.add(permissionsStr.get(i))
                        } else if (!hasPermission(this@InstagramActivity, permissionsStr.get(i))) {
                            permissionsCount++
                        }
                    }
                    if (permissionsList.size > 0) {
                        //Some permissions are denied and can be asked again.
                        askForPermissions(permissionsList)
                    } else if (permissionsCount > 0) {
                        //Show alert dialog
                        showPermissionDialog()
                    } else {
                        //All permissions granted. Do your stuff 🤞
                        downloadSingleImage(urlDownload)
                    }
                }
            })

    val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult?) {
                    result?.apply {
                        if (this.resultCode == RESULT_OK)
                            intent.getStringExtra("key")
                        if (PrefsManager.newInstance(this@InstagramActivity)
                                .getBoolean(PrefsManager.ISINSTALOGIN, false)
                        ) {
                            callStoriesApi()
                            return
                        }
                    }
                }
            })

    private val storyObserver: DisposableObserver<StoryModel> =
        object : DisposableObserver<StoryModel>() {
            override fun onNext(storyModel: StoryModel) {
                binding.rvUsersList.visibility = View.VISIBLE
                binding.prLoadingBar.visibility = View.GONE
                try {
                    storyModels = mutableListOf()
                    storyModels = storyModel.tray!!
                    val sliderLayoutManager = SliderLayoutManager(this@InstagramActivity)
                    sliderLayoutManager.callback = object :
                        SliderLayoutManager.OnItemSelectedListener {
                        override fun onItemSelected(layoutPosition: Int) {
                            Log.e(
                                "TAG",
                                "onNext: $layoutPosition"
                            )
                        }
                    }
                    binding.rvUsersList.layoutManager = sliderLayoutManager
                    binding.rvUsersList.isNestedScrollingEnabled = false
                    LinearSnapHelper().attachToRecyclerView(binding.rvUsersList)
                    val padding: Int =
                        getScreenWidth(this@InstagramActivity) / 2 - dpToPx(
                            this@InstagramActivity,
                            32
                        )
                    binding.rvUsersList.setPadding(padding, 0, padding, 0)
                    for (i in storyModels.indices.reversed()) {
                        if (storyModels[i].user == null) storyModels.removeAt(i)
                    }
                    Log.e("TAG", "onNext: ${storyModels.size}")
                    val userListAdapter = UserListAdapter(this@InstagramActivity, storyModels)
                    userListAdapter.setClick(object : UserListInterface {
                        override fun userListClick(i: Int, trayModel: TrayModel?) {
                            binding.rvUsersList.smoothScrollToPosition(i)
                            callStoriesDetailApi(
                                storyModels[i].user!!.pk.toString()
                            )
                        }
                    })
                    binding.rvUsersList.adapter = userListAdapter
                    binding.rvUsersList.smoothScrollToPosition(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("TAG", "storyObserver: ${e.localizedMessage}")
                }
            }

            override fun onError(th: Throwable) {
                binding.prLoadingBar.visibility = View.GONE
                th.printStackTrace()
            }

            override fun onComplete() {
                binding.prLoadingBar.visibility = View.GONE
            }
        }

    private val storyDetailObserver: DisposableObserver<FullDetailModel> =
        object : DisposableObserver<FullDetailModel>() {
            override fun onNext(fullDetailModel: FullDetailModel) {
                binding.rvUsersList.visibility = View.VISIBLE
                binding.prLoadingBar.visibility = View.GONE
                try {
                    storyItemModelList = fullDetailModel.reel_feed!!.items!!
                    storiesListAdapter =
                        StoriesListAdapter(
                            this@InstagramActivity,
                            storyItemModelList,
                            this@InstagramActivity
                        )
                    binding.rvStoriesList.adapter = storiesListAdapter
                    storiesListAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("TAG", "storyDetailObserver: ${e.localizedMessage}")
                }
            }

            override fun onError(th: Throwable) {
                binding.prLoadingBar.visibility = View.GONE
                th.printStackTrace()
            }

            override fun onComplete() {
                binding.prLoadingBar.visibility = View.GONE
            }
        }

    private fun callStoriesDetailApi(str: String) {
        try {
            if (!NetworkState.isOnline()) {

            } else if (commonClassForAPI != null) {
                binding.prLoadingBar.visibility = View.VISIBLE
                val commonClassForAPI2 = commonClassForAPI
                val disposableObserver: DisposableObserver<FullDetailModel> = storyDetailObserver

                commonClassForAPI2!!.getFullDetailFeed(
                    disposableObserver,
                    str,
                    "ds_user_id=" + PrefsManager.newInstance(this)
                        .getString(PrefsManager.USERID, "").toString()
                            + "; sessionid=" + PrefsManager.newInstance(this)
                        .getString(PrefsManager.SESSIONID, "")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "callStoriesDetailApi: ${e.localizedMessage}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        commonClassForAPI = CommonClassForAPI.getInstance(this)

        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.apply {
            rvStoriesList.layoutManager = gridLayoutManager
            rvStoriesList.isNestedScrollingEnabled = false
            gridLayoutManager.orientation = RecyclerView.VERTICAL

            if (PrefsManager.newInstance(this@InstagramActivity)
                    .getBoolean(PrefsManager.ISINSTALOGIN, false)
            ) {
                callStoriesApi()
            } else {
                loginLauncher.launch(Intent(this@InstagramActivity, InstaLoginActivity::class.java))
            }
        }
    }

    private fun callStoriesApi() {
        try {
            if (!NetworkState.isOnline()) {
            } else if (commonClassForAPI != null) {
                Log.e("TAG", "callStoriesApi:")
                binding.prLoadingBar.visibility = View.VISIBLE
                val commonClassForAPI2 = commonClassForAPI
                val disposableObserver: DisposableObserver<StoryModel> = storyObserver
                commonClassForAPI2?.getStories(
                    disposableObserver,
                    "ds_user_id=" + PrefsManager.newInstance(this)
                        .getString(PrefsManager.USERID, "").toString()
                            + "; sessionid=" + PrefsManager.newInstance(this)
                        .getString(PrefsManager.SESSIONID, "")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "callStoriesApi: ${e.localizedMessage}")
        }
    }

    override fun clickInsta(pos: Int) {
        position = pos
        if (storyItemModelList[pos].media_type == 2) {
            val url: String? = storyItemModelList[pos].video_versions?.get(0)?.url
            urlDownload = url

            object : AsyncTaskRunner<String, String>(this) {
                override fun doInBackground(params: String?): String? {
//                    for (i in downloadDataArrayList.indices) {
//                        var input: InputStream? = null
//                        var output: OutputStream? = null
//                        var connection: HttpURLConnection? = null
//                        try {
//                            val url = URL(downloadDataArrayList[i].getUrl())
//                            connection = url.openConnection() as HttpURLConnection
//                            connection.connect()
//                            if (connection!!.responseCode != HttpURLConnection.HTTP_OK) {
//                                return ("Server returned HTTP " + connection.responseCode
//                                        + " " + connection.responseMessage)
//                            }
//                            val fileLength = connection.contentLength
//                            input = connection.inputStream
//                            val filename: File = File(
//                                AppUtils.RootDirectoryInstaShow,
//                                downloadDataArrayList[i].getName()
//                            )
//                            output = FileOutputStream(filename)
//                            val data = ByteArray(4096)
//                            var total: Long = 0
//                            var count: Int
//                            while (input.read(data).also { count = it } != -1) {
//                                if (isCancelled()) {
//                                    input.close()
//                                    return null
//                                }
//                                total += count.toLong()
//                                if (fileLength > 0) publishProgress(i)
//                                output.write(data, 0, count)
//                            }
//                            refreshGallery(filename.path)
//                        } catch (e: java.lang.Exception) {
//                            return e.toString()
//                        } finally {
//                            try {
//                                output?.close()
//                                input?.close()
//                            } catch (ignored: IOException) {
//                            }
//                            connection?.disconnect()
//                        }
//                    }
                    return null
                }

                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)
                }
            }.execute(url!!, false)
            return
        }

        val urlSingle: String? = storyItemModelList[pos].image_versions2?.candidates?.get(0)?.url
        urlDownload = urlSingle

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionsList = mutableListOf()
                permissionsList.addAll(permissionsStr)
                askForPermissions(permissionsList)
            } else downloadSingleImage(urlSingle)
        } else if (!hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            and !hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            permissionsList = mutableListOf()
            permissionsList.addAll(permissionsStr)
            askForPermissions(permissionsList)
        } else downloadSingleImage(urlSingle)
    }

    private fun downloadSingleImage(urlSingle: String?) {
        object : AsyncTaskRunner<String, String>(this) {
            override fun doInBackground(params: String?): String? {
                var input: InputStream? = null
                var output: OutputStream? = null
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(urlSingle)
                    connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                        return ("Server returned HTTP " + connection.responseCode
                                + " " + connection.responseMessage)
                    }
                    val fileLength = connection.contentLength
                    input = connection.inputStream
                    var filename = File(
                        RootDirectoryInstaShow,
                        "story_" + storyItemModelList[position].id.toString() + ".png"
                    )
                    output = FileOutputStream(filename)
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        if (this.isShutdown()) {
                            input.close()
                            return null
                        }
                        total += count.toLong()
//                        if (fileLength > 0) publishProgress((total * 100 / fileLength).toInt())
                        output.write(data, 0, count)
                    }
                } catch (e: java.lang.Exception) {
                    return e.toString()
                } finally {
                    try {
                        output?.close()
                        input?.close()
                    } catch (ignored: IOException) {
                    }
                    connection?.disconnect()
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
            }
        }.execute(urlSingle!!, true)
    }
}