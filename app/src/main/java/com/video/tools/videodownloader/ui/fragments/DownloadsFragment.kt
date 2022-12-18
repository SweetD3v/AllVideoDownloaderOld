package com.video.tools.videodownloader.ui.fragments

import android.app.Activity
import android.content.Context.STORAGE_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.video.tools.videodownloader.databinding.FragmentsDownloadsBinding
import com.google.android.material.tabs.TabLayoutMediator

class DownloadsFragment : BaseFragment<FragmentsDownloadsBinding>() {
    private val tabTitles = arrayOf("Photos", "Videos")
    override val binding by lazy { FragmentsDownloadsBinding.inflate(layoutInflater) }

    private val PERMISSIONS = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )

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
                }
            })

    val statusFileResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent = result.data!!
                Log.d("HEY: ", data.data.toString())
                ctx.contentResolver.takePersistableUriPermission(
                    data.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Toast.makeText(ctx, "Success", Toast.LENGTH_SHORT).show()
            }
        }

    companion object {
        open fun newInstance(): DownloadsFragment {
            return DownloadsFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        if (allPermissionsGranted()) {
            onPermissionGranted()
        } else {
            permissionRequest.launch(permissions.toTypedArray())
        }
    }

    override fun onPermissionGranted() {
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.run {
            viewPagerStatus.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPagerStatus.adapter = FragmentsAdapter(this@DownloadsFragment)
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

    private fun arePermissionDenied(): Boolean {
        for (str in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(ctx, str) != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun openDocTreeStatus() {
        Log.e("TAG", "requestPermissionQ: ")
        val createOpenDocumentTreeIntent =
            (ctx.getSystemService(STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
        val replace: String =
            (createOpenDocumentTreeIntent.getParcelableExtra<Parcelable>(DocumentsContract.EXTRA_INITIAL_URI) as Uri?).toString()
                .replace("/root/", "/document/")
        val parse: Uri =
            Uri.parse("$replace%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")
        Log.d("URI", parse.toString())
        createOpenDocumentTreeIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse)
        statusFileResultLauncher.launch(createOpenDocumentTreeIntent)
    }

    inner class FragmentsAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> {
                    return AllImagesFragment.newInstance()
                }
                1 -> {
                    return AllVideosFragment.newInstance()
                }
            }
            return AllImagesFragment.newInstance()
        }
    }

    override fun onBackPressed() {

    }
}