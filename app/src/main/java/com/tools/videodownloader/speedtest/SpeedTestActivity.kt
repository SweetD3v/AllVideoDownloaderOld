package com.tools.videodownloader.speedtest

import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivitySpeedTestBinding
import com.tools.videodownloader.speedtest.test.HttpDownloadTest
import com.tools.videodownloader.speedtest.test.HttpUploadTest
import com.tools.videodownloader.speedtest.test.PingTest
import com.tools.videodownloader.ui.activities.FullScreenActivity
import com.tools.videodownloader.utils.adjustInsets
import com.tools.videodownloader.utils.gone
import com.tools.videodownloader.utils.visible
import java.text.DecimalFormat

class SpeedTestActivity : FullScreenActivity() {
    val binding by lazy { ActivitySpeedTestBinding.inflate(layoutInflater) }

    private val dec = DecimalFormat("#.##")

    /**
     * The View.
     */
    var view: View? = null

    /**
     * The Position.
     */
    var position = 0

    /**
     * The Last position.
     */
    var lastPosition = 0

    /**
     * The Get speed test hosts handler.
     */
    var getSpeedTestHostsHandler: GetSpeedTestHostsHandler? = null

    /**
     * The Temp black list.
     */
    var tempBlackList: HashSet<String> = HashSet()

    /**
     * The Upload addr.
     */
    var uploadAddr: String? = null

    /**
     * The Info.
     */
    var info: List<String>? = null

    /**
     * The Distance.
     */
    var distance = 0.0

    /**
     * The Is internet present.
     */
    var isInternetPresent = false

    /**
     * The Cd.
     */
    var cd: ConnectionDetector? = null
    lateinit var sharedPref: SharedPreferences

    private var i = 0f
    private var j: Float = 0f
    private var k: Float = 0f
    private var testing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(
            "SPEED_TEST_PREFS", MODE_PRIVATE
        )

        binding.run {
            toolbar.rlMain.adjustInsets(this@SpeedTestActivity)
            toolbar.txtTitle.text = getString(R.string.internet_speed_test)

            toolbar.imgBack.setOnClickListener { onBackPressed() }

            startSpeedTest.setOnClickListener {
                startSpeedTest.gone()
                imgSpeedMeter.visible()
                imgSpeedMeterHande.visible()
                testSpeed()
            }

            startSpeedTestAgain.setOnClickListener {
                imgSpeedMeter.visible()
                imgSpeedMeterHande.visible()
                startSpeedTestAgain.gone()

                txtPing.text = "Ping - ms"
                txtDownloadSpeed.text = "- mbps"
                txtUploadSpeed.text = "- mbps"

                testSpeed()
            }
        }

        tempBlackList = HashSet()
        getSpeedTestHostsHandler =
            GetSpeedTestHostsHandler()
        getSpeedTestHostsHandler?.start()

    }

    private fun testSpeed() {
        var rotate: RotateAnimation? = null
//        tvBegin.setImageResource(R.drawable.ic_stop)
//        tvBlink!!.visibility = View.VISIBLE
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 650
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
//        tvBlink!!.startAnimation(anim)
        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler =
                GetSpeedTestHostsHandler()
            getSpeedTestHostsHandler!!.start()
        }
        Thread(Runnable {
//            try {
//                runOnUiThread { tvBlink!!.text = "Find the Best Server" }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            var timeCount = 600
            while (!getSpeedTestHostsHandler!!.isFinished) {
                timeCount--
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    Thread.currentThread().interrupt()
                }
                if (timeCount <= 0) {
                    try {
//                        runOnUiThread {
//                            tvBlink!!.clearAnimation()
//                            tvBlink!!.visibility = View.GONE
//                            tvBlink!!.text = "No Connection..."
////                            tvBegin.setImageResource(R.drawable.ic_play)
//                        }
                        getSpeedTestHostsHandler = null
                        return@Runnable
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val mapKey =
                getSpeedTestHostsHandler?.getMapKey() ?: HashMap()
            val mapValue =
                getSpeedTestHostsHandler?.getMapValue() ?: HashMap()
            val selfLat = getSpeedTestHostsHandler?.getSelfLat() ?: 0.0
            val selfLon = getSpeedTestHostsHandler?.getSelfLon() ?: 0.0
            var tmp = 19349458.0
            var dist = 0.0
            var findServerIndex = 0
            for (index in mapKey.keys) {
                if (tempBlackList.contains(mapValue[index]!![5])) {
                    continue
                }
                val source = Location("Source")
                source.latitude = selfLat
                source.longitude = selfLon
                val ls = mapValue[index]!!
                val dest = Location("Dest")
                dest.latitude = ls[0].toDouble()
                dest.longitude = ls[1].toDouble()
                distance = source.distanceTo(dest).toDouble()
                if (tmp > distance) {
                    tmp = distance
                    dist = distance
                    findServerIndex = index
                }
            }
            uploadAddr = mapKey[findServerIndex]
            info = mapValue[findServerIndex]
            distance = dist
            if (info != null) {
                if (info?.isNotEmpty() == true) {
//                    runOnUiThread {
//                        tvBlink!!.clearAnimation()
//                        tvBlink!!.visibility = View.VISIBLE
//                        tvBlink!!.text = String.format(
//                            "Hosted by %s (%s) [%s km]",
//                            info!![5],
//                            info!![3],
//                            DecimalFormat("#.##").format(distance / 1000)
//                        )
//                    }
                    runOnUiThread {
//                        tvPing.setText("0")
//                        tvDownload.setText("0")
//                        tvUpload.setText("0")
                        i = 0f
                        j = 0f
                        k = 0f
//                        ivPBDownload.setAlpha(0.5f)
//                        ivPBUpload.setAlpha(0.5f)
                    }
                    val pingRateList: MutableList<Double> =
                        ArrayList()
                    val downloadRateList: MutableList<Double> =
                        ArrayList()
                    val uploadRateList: MutableList<Double> =
                        ArrayList()
                    var pingTestStarted = false
                    var pingTestFinished = false
                    var downloadTestStarted = false
                    var downloadTestFinished = false
                    var uploadTestStarted = false
                    var uploadTestFinished = false
                    val pingTest =
                        PingTest(
                            info!![6].replace(
                                ":8080",
                                ""
                            ), 6
                        )
                    val downloadTest =
                        HttpDownloadTest(
                            uploadAddr!!.replace(
                                uploadAddr!!.split("/").toTypedArray()[uploadAddr!!.split("/")
                                    .toTypedArray().size - 1], ""
                            )
                        )
                    val uploadTest =
                        HttpUploadTest(
                            uploadAddr
                        )
                    while (true) {
                        if (!pingTestStarted) {
                            pingTest.start()
                            pingTestStarted = true
                        }
                        if (pingTestFinished && !downloadTestStarted) {
                            downloadTest.start()
                            downloadTestStarted = true
                        }
                        if (downloadTestFinished && !uploadTestStarted) {
                            uploadTest.start()
                            uploadTestStarted = true
                        }
                        if (pingTestFinished) {
                            if (pingTest.avgRtt == 0.0) {
                                Log.e("TAG", "")
                            } else {
                                try {
                                    runOnUiThread {
//                                        tickProgressMeasure.setmPUnit("ms")
//                                        tvPing.setText(
//                                            dec.format(pingTest.getAvgRtt()).toString() + ""
//                                        )
                                        Log.e(
                                            "TAG",
                                            "testSpeedPing1: ${
                                                dec.format(pingTest.avgRtt)
                                            }"
                                        )

                                        binding.txtPing.text =
                                            "${dec.format(pingTest.avgRtt)} ms"
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            pingRateList.add(pingTest.instantRtt)
                            try {
                                runOnUiThread(Runnable {
                                    Log.e("TAG", "i = $i")
//                                    tickProgressMeasure.setmPUnit("ms")
//                                    tvPing.setText(
//                                        dec.format(pingTest.getInstantRtt()).toString() + ""
//                                    )
                                    Log.e(
                                        "TAG",
                                        "testSpeedPing2: ${
                                            dec.format(pingTest.instantRtt)
                                        }"
                                    )
                                    Log.e("PING", "" + pingTest.instantRtt)
//                                    tickProgressMeasure.setProgress((pingTest.getInstantRtt() * 100) as Int)
                                    if (i == 0f) {
//                                        lcMeasure.clear()
//                                        lineDataSet.clear()
//                                        lineDataSet.setColor(Color.rgb(255, 207, 223))
//                                        lineData = LineData(lineDataSet)
//                                        lcMeasure.setData(lineData)
//                                        lcMeasure.invalidate()
                                    }
//                                    if (i > 10) {
//                                        val data: LineData = lcMeasure.getData()
//                                        val set =
//                                            data.getDataSetByIndex(0) as LineDataSet
//                                        if (set != null) {
//                                            data.addEntry(
//                                                Entry(
//                                                    i,
//                                                    (10 * pingTest.getInstantRtt()) as Float
//                                                ), 0
//                                            )
//                                            lcMeasure.notifyDataSetChanged()
//                                            lcMeasure.setVisibleXRange(0f, i)
//                                            lcMeasure.invalidate()
//                                        }
//                                    } else {
//                                        lcMeasure.setVisibleXRange(0f, 10f)
//                                        val data: LineData = lcMeasure.getData()
//                                        val set =
//                                            data.getDataSetByIndex(0) as LineDataSet
//                                        if (set != null) {
//                                            data.addEntry(
//                                                Entry(
//                                                    i,
//                                                    (10 * pingTest.getInstantRtt()) as Float
//                                                ), 0
//                                            )
//                                            lcMeasure.notifyDataSetChanged()
//                                            lcMeasure.invalidate()
//                                        }
//                                    }
                                    i++
                                })
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (pingTestFinished) {
                            if (downloadTestFinished) {
                                if (downloadTest.finalDownloadRate == 0.0) {
                                    Log.e("TAG", "")
                                } else {
                                    try {
                                        runOnUiThread {
                                            val mbps = sharedPref.getString(
                                                "UNIT",
                                                "Mbps"
                                            )
                                            Log.e("TAG", "testSpeedMBPS: $mbps")
//                                            tickProgressMeasure.setmPUnit(mbp)
                                            var downloadSpeed = "0"
                                            when (sharedPref.getString("UNIT", "Mbps")) {
                                                "MBps" ->
                                                    downloadSpeed =
                                                        dec.format(0.125 * downloadTest.finalDownloadRate)
                                                            .toString()
//                                                    tvDownload.setText(
//                                                    dec.format(0.125 * downloadTest.getFinalDownloadRate())
//                                                        .toString() + ""
//                                                )
                                                "kBps" ->
                                                    downloadSpeed =
                                                        dec.format(125 * downloadTest.finalDownloadRate)
                                                            .toString()
//                                                    tvDownload.setText(
//                                                    dec.format(125 * downloadTest.getFinalDownloadRate())
//                                                        .toString() + ""
//                                                )
                                                "Mbps" ->
                                                    downloadSpeed =
                                                        dec.format(downloadTest.finalDownloadRate)
                                                            .toString()
//                                                    tvDownload.setText(
//                                                    dec.format(downloadTest.getFinalDownloadRate())
//                                                        .toString() + ""
//                                                )
                                                "kbps" ->
                                                    downloadSpeed =
                                                        dec.format(1000 * downloadTest.finalDownloadRate)
                                                            .toString()
//                                                    tvDownload.setText(
//                                                    dec.format(1000 * downloadTest.getFinalDownloadRate())
//                                                        .toString() + ""
//                                                )
                                                else -> {}
                                            }

                                            Log.e(
                                                "TAG",
                                                "testSpeedDownloadSpeed: $downloadSpeed"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                val downloadRate: Double = downloadTest.instantDownloadRate
                                downloadRateList.add(downloadRate)
                                position = getPositionByRateNew(downloadRate)
                                try {
                                    runOnUiThread {
                                        Log.i("TAG", "j = $j")
//                                        tickProgressMeasure.setmPUnit(
//                                            sharedPref.getString(
//                                                "UNIT",
//                                                "Mbps"
//                                            )
//                                        )

                                        var downloadSpeedInstant = "0"
                                        when (sharedPref.getString("UNIT", "Mbps")) {
                                            "MBps" -> {
                                                downloadSpeedInstant =
                                                    dec.format(0.125 * downloadTest.instantDownloadRate)
                                                        .toString()
//                                                tvDownload.setText(
//                                                    dec.format(0.125 * downloadTest.getInstantDownloadRate())
//                                                        .toString() + ""
//                                                )
//                                                tickProgressMeasure.setProgress((0.125 * downloadTest.getInstantDownloadRate() * 100) as Int)
                                            }
                                            "kBps" -> {
                                                downloadSpeedInstant =
                                                    dec.format(125 * downloadTest.instantDownloadRate)
                                                        .toString()
//                                                tvDownload.setText(
//                                                    dec.format(125 * downloadTest.getInstantDownloadRate())
//                                                        .toString() + ""
//                                                )
//                                                tickProgressMeasure.setProgress((125 * downloadTest.getInstantDownloadRate() * 100) as Int)
                                            }
                                            "Mbps" -> {
                                                downloadSpeedInstant =
                                                    dec.format(downloadTest.instantDownloadRate)
                                                        .toString()
//                                                tvDownload.setText(
//                                                    dec.format(downloadTest.getInstantDownloadRate())
//                                                        .toString() + ""
//                                                )
//                                                tickProgressMeasure.setProgress((downloadTest.getInstantDownloadRate() * 100) as Int)
                                            }
                                            "kbps" -> {
                                                downloadSpeedInstant =
                                                    dec.format(1000 * downloadTest.instantDownloadRate)
                                                        .toString()
//                                                tvDownload.setText(
//                                                    dec.format(1000 * downloadTest.getInstantDownloadRate())
//                                                        .toString() + ""
//                                                )
//                                                tickProgressMeasure.setProgress((1000 * downloadTest.getInstantDownloadRate() * 100) as Int)
                                            }
                                            else -> {}
                                        }
                                        Log.e(
                                            "DOWNLOAD",
                                            "" + downloadTest.instantDownloadRate
                                        )
                                        Log.e(
                                            "TAG",
                                            "testSpeedDownloadSpeedInstant: $downloadSpeedInstant"
                                        )
//                                        if (j == 0f) {
//                                            ivPBDownload.setAlpha(1.0f)
//                                            ivPBUpload.setAlpha(0.5f)
//                                            lcMeasure.clear()
//                                            lineDataSet.clear()
//                                            lineDataSet.setColor(
//                                                Color.rgb(
//                                                    224,
//                                                    249,
//                                                    181
//                                                )
//                                            )
//                                            lineData = LineData(lineDataSet)
//                                            lcMeasure.setData(lineData)
//                                            lcMeasure.invalidate()
//                                        }
//                                        if (j > 100) {
//                                            val data: LineData = lcMeasure.getData()
//                                            val set =
//                                                data.getDataSetByIndex(0) as LineDataSet
//                                            if (set != null) {
//                                                data.addEntry(
//                                                    Entry(
//                                                        j,
//                                                        (1000 * downloadTest.getInstantDownloadRate()) as Float
//                                                    ), 0
//                                                )
//                                                lcMeasure.notifyDataSetChanged()
//                                                lcMeasure.setVisibleXRange(0f, j)
//                                                lcMeasure.invalidate()
//                                            }
//                                        } else {
//                                            lcMeasure.setVisibleXRange(0f, 100f)
//                                            val data: LineData = lcMeasure.getData()
//                                            val set =
//                                                data.getDataSetByIndex(0) as LineDataSet
//                                            if (set != null) {
//                                                data.addEntry(
//                                                    Entry(
//                                                        j,
//                                                        (1000 * downloadTest.getInstantDownloadRate()) as Float
//                                                    ), 0
//                                                )
//                                                lcMeasure.notifyDataSetChanged()
//                                                lcMeasure.invalidate()
//                                            }
//                                        }
                                        j++

                                        binding.txtDownloadSpeed.text =
                                            "$downloadSpeedInstant Mbps"
                                    }
                                    lastPosition = position

                                    rotate = RotateAnimation(
                                        lastPosition.toFloat(),
                                        position.toFloat(),
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    ).apply {
                                        interpolator = LinearInterpolator()
                                        duration = 100
                                    }
                                    binding.imgSpeedMeterHande.startAnimation(rotate)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        if (downloadTestFinished) {
                            if (uploadTestFinished) {
                                if (uploadTest.finalUploadRate == 0.0) {
                                    Log.e("TAG", "")
                                } else {
                                    try {
                                        runOnUiThread(Runnable {
//                                            tickProgressMeasure.setmPUnit(
//                                                sharedPref.getString(
//                                                    "UNIT",
//                                                    "Mbps"
//                                                )
//                                            )
                                            var uploadSpeed = "0"
                                            when (sharedPref.getString("UNIT", "Mbps")) {
                                                "MBps" ->
                                                    uploadSpeed = String.format(
                                                        "%.1f",
                                                        dec.format(0.125 * uploadTest.finalUploadRate)
                                                    )
//                                                    tvUpload.setText(
//                                                    String.format(
//                                                        "%.1f",
//                                                        dec.format(0.125 * uploadTest.getFinalUploadRate())
//                                                    )
//                                                    )
                                                "kBps" ->
                                                    uploadSpeed = String.format(
                                                        "%.1f",
                                                        dec.format(125 * uploadTest.finalUploadRate)
                                                    )
//                                                    tvUpload.setText(
//                                                    String.format(
//                                                        "%.1f",
//                                                        dec.format(125 * uploadTest.getFinalUploadRate())
//                                                    )
//                                                )
                                                "Mbps" ->
                                                    uploadSpeed = String.format(
                                                        "%.1f",
                                                        dec.format(uploadTest.finalUploadRate)
                                                    )
//                                                    tvUpload.setText(
//                                                    String.format(
//                                                        "%.1f",
//                                                        dec.format(uploadTest.getFinalUploadRate())
//                                                    )
//                                                )
                                                "kbps" ->
                                                    uploadSpeed = String.format(
                                                        "%.1f",
                                                        dec.format(1000 * uploadTest.finalUploadRate)
                                                    )
//                                                    tvUpload.setText(
//                                                    String.format(
//                                                        "%.1f",
//                                                        dec.format(1000 * uploadTest.getFinalUploadRate())
//                                                    )
//                                                )
                                                else -> Log.e("TAG", "ERROR")
                                            }
                                            Log.e("TAG", "testSpeedUpload: $uploadSpeed")
                                        })
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                val uploadRate: Double = uploadTest.instantUploadRate
                                uploadRateList.add(uploadRate)
                                position = getPositionByRateNew(uploadRate)
                                try {
                                    runOnUiThread {
//                                        tickProgressMeasure.setmPUnit(
//                                            sharedPref.getString(
//                                                "UNIT",
//                                                "Mbps"
//                                            )
//                                        )
                                        var uploadSpeedInstant = "0"
                                        when (sharedPref.getString("UNIT", "Mbps")) {
                                            "MBps" -> {
                                                uploadSpeedInstant =
                                                    dec.format(0.125 * uploadTest.instantUploadRate)
                                                        .toString()
//                                                tvUpload.setText(
//                                                    dec.format(0.125 * uploadTest.getInstantUploadRate())
//                                                        .toString() + ""
//                                                )
//                                                tickProgressMeasure.setProgress((0.125 * uploadTest.getInstantUploadRate() * 100) as Int)
                                            }
                                            "kBps" -> {
                                                uploadSpeedInstant =
                                                    dec.format(125 * uploadTest.instantUploadRate)
                                                        .toString()
//                                                tvUpload.setText(
//                                                    dec.format(125 * uploadTest.getInstantUploadRate())
//                                                        .toString() + ""
//                                                )
//                                                tickProgressMeasure.setProgress((125 * uploadTest.getInstantUploadRate() * 100) as Int)
                                            }
                                            "Mbps" -> {
                                                uploadSpeedInstant =
                                                    dec.format(uploadTest.instantUploadRate)
                                                        .toString()
//                                                tvUpload.setText(
//                                                    dec.format(uploadTest.getInstantUploadRate())
//                                                        .toString() + ""
//                                                )
//                                                tickProgressMeasure.setProgress((uploadTest.getInstantUploadRate() * 100) as Int)
                                            }
                                            "kbps" -> {
                                                uploadSpeedInstant =
                                                    dec.format(1000 * uploadTest.instantUploadRate)
                                                        .toString()
//                                                tvUpload.setText(
//                                                    dec.format(1000 * uploadTest.getInstantUploadRate())
//                                                        .toString() + ""
//                                                )
//                                                tickProgressMeasure.setProgress((1000 * uploadTest.getInstantUploadRate() * 100) as Int)
                                            }
                                            else -> Log.e("TAG", "ERROR")
                                        }

                                        Log.e(
                                            "TAG",
                                            "testSpeedUploadInstant: $uploadSpeedInstant"
                                        )

                                        Log.e("TAG", "k = $k")
                                        Log.e("UPLOAD", "" + uploadTest.instantUploadRate)
//                                        if (k == 0f) {
//                                            ivPBDownload.setAlpha(1.0f)
//                                            ivPBUpload.setAlpha(1.0f)
//                                            lcMeasure.clear()
//                                            lineDataSet.clear()
//                                            lineDataSet.setColor(
//                                                Color.rgb(
//                                                    145,
//                                                    174,
//                                                    210
//                                                )
//                                            )
//                                            lineData = LineData(lineDataSet)
//                                            lcMeasure.setData(lineData)
//                                            lcMeasure.invalidate()
//                                        }
//                                        if (k > 100) {
//                                            val data: LineData = lcMeasure.getData()
//                                            val set =
//                                                data.getDataSetByIndex(0) as LineDataSet
//                                            if (set != null) {
//                                                data.addEntry(
//                                                    Entry(
//                                                        k,
//                                                        (1000 * uploadTest.getInstantUploadRate()) as Float
//                                                    ), 0
//                                                )
//                                                lcMeasure.notifyDataSetChanged()
//                                                lcMeasure.setVisibleXRange(0f, k)
//                                                lcMeasure.invalidate()
//                                            }
//                                        } else {
//                                            lcMeasure.setVisibleXRange(0f, 100f)
//                                            val data: LineData = lcMeasure.getData()
//                                            val set =
//                                                data.getDataSetByIndex(0) as LineDataSet
//                                            if (set != null) {
//                                                data.addEntry(
//                                                    Entry(
//                                                        k,
//                                                        (1000 * uploadTest.getInstantUploadRate()) as Float
//                                                    ), 0
//                                                )
//                                                lcMeasure.notifyDataSetChanged()
//                                                lcMeasure.invalidate()
//                                            }
//                                        }
                                        k++

                                        binding.txtUploadSpeed.text = "$uploadSpeedInstant Mbps"
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                lastPosition = position

                                rotate = RotateAnimation(
                                    lastPosition.toFloat(),
                                    position.toFloat(),
                                    Animation.RELATIVE_TO_SELF,
                                    0.5f,
                                    Animation.RELATIVE_TO_SELF,
                                    0.5f
                                ).apply {
                                    interpolator = LinearInterpolator()
                                    duration = 100
                                }
                                binding.imgSpeedMeterHande.startAnimation(rotate)
                            }
                        }
                        if (pingTestFinished && downloadTestFinished && uploadTest.isFinished) {
                            binding.run {
                                imgSpeedMeter.post {
                                    imgSpeedMeter.gone()
                                    imgSpeedMeterHande.gone()
                                    startSpeedTestAgain.visible()
                                }
                            }
                            break
                        }
                        if (pingTest.isFinished) {
                            pingTestFinished = true
                        }
                        if (downloadTest.isFinished) {
                            downloadTestFinished = true
                        }
                        if (uploadTest.isFinished) {
                            uploadTestFinished = true
                        }
                        if (pingTestStarted && !pingTestFinished) {
                            try {
                                Thread.sleep(300)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                                Thread.currentThread().interrupt()
                            }
                        } else {
                            try {
                                Thread.sleep(100)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                                Thread.currentThread().interrupt()
                            }
                        }
                    }
//                    try {
//                        runOnUiThread({
//                            tvBegin.setImageResource(R.drawable.ic_play)
//                            LOGE("TAG", "test1")
//                            val sharedPrefHistory: SharedPreferences =
//                                getSharedPreferences(
//                                    "historydata", MODE_PRIVATE
//                                )
//                            val editor =
//                                sharedPrefHistory.edit()
//                            val _data = sharedPrefHistory.getString("DATA", "")
//                            if (_data != "") {
//                                LOGE("TAG", "1")
//                                val jsondata = JSONObject()
//                                try {
//                                    jsondata.put(
//                                        "date",
//                                        System.currentTimeMillis().toString()
//                                    )
//                                    jsondata.put("ping", tvPing.getText())
//                                    jsondata.put("download", tvDownload.getText())
//                                    jsondata.put("upload", tvUpload.getText())
//                                    LOGE("TAG", _data)
//                                    val js = JSONObject(_data)
//                                    val array =
//                                        js.getJSONArray(getString(R.string.history))
//                                    array.put(jsondata)
//                                    val new_data = JSONObject()
//                                    new_data.put(getString(R.string.history), array)
//                                    editor.remove("DATA")
//                                    editor.putString("DATA", new_data.toString())
//                                    editor.apply()
//                                } catch (e: JSONException) {
//                                    e.printStackTrace()
//                                }
//                            } else {
//                                LOGE("TAG", "2")
//                                val jsondata = JSONObject()
//                                try {
//                                    jsondata.put(
//                                        "date",
//                                        System.currentTimeMillis().toString()
//                                    )
//                                    jsondata.put("ping", tvPing.getText())
//                                    jsondata.put("download", tvDownload.getText())
//                                    jsondata.put("upload", tvUpload.getText())
//                                    val array = JSONArray()
//                                    array.put(jsondata)
//                                    val new_data = JSONObject()
//                                    new_data.put("History", array)
//                                    editor.putString("DATA", new_data.toString())
//                                    editor.apply()
//                                    testing = false
//                                } catch (e: JSONException) {
//                                    e.printStackTrace()
//                                }
//                            }
//                        })
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
                }
            } else {
//                tvBegin.setImageResource(R.drawable.ic_play)
                Log.e("TAG", "test2")
            }
        }).start()
    }

    private fun getPositionByRate(rate: Double): Int {
        if (rate <= 1) {
            return (rate * 30).toInt()
        } else if (rate <= 2) {
            return (rate * 3).toInt() + 30
        } else if (rate <= 3) {
            return (rate * 3).toInt() + 60
        } else if (rate <= 4) {
            return (rate * 3).toInt() + 90
        } else if (rate <= 5) {
            return (rate * 3).toInt() + 120
        } else if (rate <= 10) {
            return ((rate - 5) * 6).toInt() + 150
        } else if (rate <= 50) {
            return ((rate - 10) * 1.33).toInt() + 180
        } else if (rate <= 100) {
            return ((rate - 50) * 0.6).toInt() + 180
        }
        return 0
    }

    fun getPositionByRateNew(rate: Double): Int {
        if (rate <= 1) {
            return (rate * 30).toInt()

        } else if (rate <= 10) {
            return ((rate * 6) + 30).toInt()

        } else if (rate <= 30) {
            return (((rate - 10) * 3) + 90).toInt()

        } else if (rate <= 50) {
            return (((rate - 30) * 1.5) + 150).toInt()

        } else if (rate <= 100) {
            return (((rate - 50) * 1.2) + 180).toInt()
        }

        return 0
    }
}