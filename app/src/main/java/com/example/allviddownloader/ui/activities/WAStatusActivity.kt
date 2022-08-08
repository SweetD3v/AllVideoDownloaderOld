package com.example.allviddownloader.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.allviddownloader.databinding.ActivityWastatusBinding
import com.example.allviddownloader.ui.fragments.WAImagesFragment
import com.example.allviddownloader.ui.fragments.WAVideoFragment
import com.google.android.material.tabs.TabLayoutMediator


class WAStatusActivity : AppCompatActivity() {
    val binding by lazy { ActivityWastatusBinding.inflate(layoutInflater) }
    private val PERMISSIONS = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )
    private val tabTitles = arrayOf("Photos", "Videos")

    val permissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            object : ActivityResultCallback<Map<String, Boolean>> {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onActivityResult(result: Map<String, Boolean>?) {
                    var granted = true
                    if (result != null) {
                        for (b in result.values) {
                            if (!b) {
                                granted = false
                                break
                            }
                        }
                    } else granted = false

                    if (granted) {
                        setupViewPager()
                    }
                }
            })

    val statusFileResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent = result.data!!
                Log.d("HEY: ", data.data.toString())
                contentResolver.takePersistableUriPermission(
                    data.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViewPager() {
        binding.run {
            viewPagerStatus.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPagerStatus.adapter = FragmentsAdapter(this@WAStatusActivity)
            viewPagerStatus.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }
            })

            TabLayoutMediator(tabLayout, viewPagerStatus) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResume: " + arePermissionDenied())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                openDocTreeStatus()
            } else {
                permissionsLauncher.launch(PERMISSIONS)
            }
        } else {
//            AppUtils.APP_DIR = getExternalFilesDir(getString(R.string.app_name))!!.path
//            Log.d("App Path", AppUtils.APP_DIR.toString())
            setupViewPager()
        }
    }

    private fun arePermissionDenied(): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            return contentResolver.persistedUriPermissions.size <= 0
        }
        for (str in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(applicationContext, str) != 0) {
                return true
            }
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun openDocTreeStatus() {
        Log.e("TAG", "requestPermissionQ: ")
        val createOpenDocumentTreeIntent =
            (getSystemService(STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
        val replace: String =
            (createOpenDocumentTreeIntent.getParcelableExtra<Parcelable>(DocumentsContract.EXTRA_INITIAL_URI) as Uri?).toString()
                .replace("/root/", "/document/")
        val parse: Uri =
            Uri.parse("$replace%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")
        Log.d("URI", parse.toString())
        createOpenDocumentTreeIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse)
        statusFileResultLauncher.launch(createOpenDocumentTreeIntent)
    }


    inner class FragmentsAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> {
                    return WAImagesFragment.newInstance()
                }
                1 -> {
                    return WAVideoFragment.newInstance()
                }
            }
            return WAImagesFragment.newInstance()
        }
    }

}