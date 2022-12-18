package com.video.tools.videodownloader.ui.activities

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.video.tools.videodownloader.databinding.ActivityStatusMakerBinding

class StatusMakerActivity : AppCompatActivity() {
    val binding by lazy { ActivityStatusMakerBinding.inflate(layoutInflater) }

    private var mGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
//            startCrop(it)
        }
    }

    private var mGetPicture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult? ->
        result?.data?.let { uri ->
            uri.data?.let { it ->
//                startCrop(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}