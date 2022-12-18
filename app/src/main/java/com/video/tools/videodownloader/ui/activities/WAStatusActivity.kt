package com.video.tools.videodownloader.ui.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityWastatusBinding
import com.video.tools.videodownloader.ui.fragments.WADownloadsFragment
import com.video.tools.videodownloader.ui.fragments.WAImagesFragment
import com.video.tools.videodownloader.ui.fragments.WAVideoFragment
import com.video.tools.videodownloader.utils.adjustInsets
import com.google.android.material.tabs.TabLayoutMediator


class WAStatusActivity : FullScreenActivity() {
    val binding by lazy { ActivityWastatusBinding.inflate(layoutInflater) }

    //    private val PERMISSIONS = mutableListOf(
//        Manifest.permission.READ_EXTERNAL_STORAGE
//    ).apply {
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//    }
    private val tabTitles = arrayOf("Status", "Downloads")

    val imgFragment = WAImagesFragment.newInstance()
    val vidFragment = WAVideoFragment.newInstance()
    val savedFragment = WADownloadsFragment.newInstance()

//    val permissionsLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { result ->
//            var granted = true
//            if (result != null) {
//                for (b in result.values) {
//                    if (!b) {
//                        granted = false
//                        break
//                    }
//                }
//            } else granted = false
//
//        }

//    val statusFileResultLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val data: Intent = result.data!!
//                Log.d("HEY: ", data.data.toString())
//                contentResolver.takePersistableUriPermission(
//                    data.data!!,
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION
//                )
//                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        if (NetworkState.isOnline())
//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )

        binding.toolbar.rlMain.adjustInsets(this)
        binding.toolbar.root.background = ContextCompat.getDrawable(
            this@WAStatusActivity,
            R.drawable.top_bar_gradient_green
        )
        binding.toolbar.txtTitle.text = getString(R.string.whatsapp_status)
        binding.toolbar.imgBack.setOnClickListener {
            onBackPressed()
        }

//        if (!allPermissionsGranted() || contentResolver.persistedUriPermissions.size <= 0) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
//                && contentResolver.persistedUriPermissions.size <= 0
//            ) {
//                openDocTreeStatus()
//            } else {
//                if (!allPermissionsGranted())
//                    permissionsLauncher.launch(PERMISSIONS.toTypedArray())
//                else setupViewPager()
//            }
//        } else {
        setupViewPager()
//        }
    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerStatus.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPagerStatus.adapter = FragmentsAdapter(this@WAStatusActivity)

            TabLayoutMediator(tabLayout, viewPagerStatus) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()
        }
    }

//    private fun allPermissionsGranted() = PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun arePermissionDenied(): Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            return contentResolver.persistedUriPermissions.size <= 0
//        }
//        for (str in PERMISSIONS) {
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    str
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return true
//            }
//        }
//        return false
//    }

//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun openDocTreeStatus() {
//        Log.e("TAG", "requestPermissionQ: ")
//        val createOpenDocumentTreeIntent =
//            (getSystemService(STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
//        val replace: String =
//            (createOpenDocumentTreeIntent.getParcelableExtra<Parcelable>(DocumentsContract.EXTRA_INITIAL_URI) as Uri?).toString()
//                .replace("/root/", "/document/")
//        val parse: Uri =
//            Uri.parse("$replace%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")
//        Log.d("URI", parse.toString())
//        createOpenDocumentTreeIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse)
//        statusFileResultLauncher.launch(createOpenDocumentTreeIntent)
//    }

    inner class FragmentsAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> {
                    return imgFragment
                }
                1 -> {
                    return savedFragment
                }
            }
            return imgFragment
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
//        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}