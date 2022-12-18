package com.video.tools.videodownloader.ui.activities

import android.os.Bundle
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityPrivacyPolicyBinding
import com.video.tools.videodownloader.utils.adjustInsetsBoth
import com.video.tools.videodownloader.utils.bottomMargin
import com.video.tools.videodownloader.utils.topMargin
import java.io.BufferedReader
import java.io.InputStreamReader

class PrivacyPolicyActivity : FullScreenActivity() {
    val binding by lazy { ActivityPrivacyPolicyBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            toolbar.txtTitle.text = getString(R.string.privacy_policy)
            adjustInsetsBoth(this@PrivacyPolicyActivity, {
                toolbar.rlMain.topMargin = it
            }, {
                rlMain.bottomMargin = it
            })

            txtPolicy.text = readPolicy()
        }
    }

    private fun readPolicy(): String {
        val inputStream = resources.openRawResource(
            resources.getIdentifier(
                "privacy_policy",
                "raw", packageName
            )
        )
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line = reader.readLine()
        val sb = StringBuilder()
        while (line != null) {
            line = reader.readLine()
            sb.append(line)
            sb.append("\n")
        }
        return sb.toString()
    }
}