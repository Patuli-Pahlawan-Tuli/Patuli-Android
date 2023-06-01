package com.puxxbu.PatuliApp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.puxxbu.PatuliApp.BuildConfig
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivitySplashBinding
import com.puxxbu.PatuliApp.ui.home.HomeActivity
import com.puxxbu.PatuliApp.ui.home.HomeViewModel
import com.puxxbu.PatuliApp.utils.Event
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var i: Intent
    private val PERMISSIONS_REQUEST_CAMERA_AND_STORAGE = 1001

    private lateinit var binding: ActivitySplashBinding

    private val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }


    private val modelUrls = listOf(
        BuildConfig.URL_ABJAD,
        BuildConfig.URL_MOBILE

        )

    private var downloadCount: Int = 0
    private val modelNames = listOf(
        "abjad_fulltrain_quantized_metadata.tflite",
        "lite-model_ssd_mobilenet_v1_1_metadata_2.tflite"
    )


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        showLoading()


        supportActionBar?.hide()

        homeViewModel.setDownloadCount(0)
        homeViewModel.isDownloaded.value = Event(false)


        if (!checkPermissions()) {
            homeViewModel.setPermissionResponse(false)
            requestPermissions()
        } else {
            homeViewModel.setPermissionResponse(true)
        }

        homeViewModel.permissionResponse.observe(this) {
            if (it) {
                downloadModels()
            }
        }

        homeViewModel.isDownloaded.observe(this) {
            it.getContentIfNotHandled()?.let { downloaded ->
                if (downloaded) {
                    homeViewModel.downloadCount.observe(this) {
                        if (it == downloadCount) {
                            homeViewModel.setProceed(Event(true))
                            homeViewModel.isLoading.value = false
                        }
                    }
                }
            }
        }


        homeViewModel.isProceed.observe(this) {
            it.getContentIfNotHandled()?.let { proceed ->
                if (proceed) {
                    Log.d("SplashActivity", "onCreate: proceed home")
                    proceedApplication()
                }
            }
        }
    }

    private fun downloadModels() {

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val requiredModelUrl = mutableListOf<String>()

        checkModelFile(modelNames, requiredModelUrl)

        if (requiredModelUrl.isEmpty()) {
            Log.d("SplashActivity", "proceedApplication:tdk butuh download ")
            homeViewModel.setProceed(Event(true))
            return
        }

        homeViewModel.isLoading.value = true
        val downloadIds = mutableListOf<Long>()
        for (url in requiredModelUrl) {
            val request = DownloadManager.Request(url.toUri())
            request
                .setDestinationInExternalFilesDir(this, "models", url.substringAfterLast("/"))
                .setTitle("Model Download")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Downloading model")
            val downloadId = downloadManager.enqueue(request)
            downloadIds.add(downloadId)
            Toast.makeText(this, "Downloading model", Toast.LENGTH_SHORT).show()
        }


        val onCompleteReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                var allModelsDownloaded = false
                while (!allModelsDownloaded) {
                    allModelsDownloaded = downloadIds.all { isModelDownloaded(it) }
                    if (!allModelsDownloaded) {
                        Thread.sleep(1000)
                    }
                }
                Toast.makeText(this@SplashActivity, "Models downloaded", Toast.LENGTH_SHORT)
                    .show()
                homeViewModel.incrementDownloadCount()
            }
        }

        registerReceiver(onCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    @SuppressLint("Range")
    private fun isModelDownloaded(downloadId: Long): Boolean {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).query(query)
        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            return status == DownloadManager.STATUS_SUCCESSFUL
        }
        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA_AND_STORAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Permissions granted, proceed with camera or file operation
//                        proceedApplication()
                        homeViewModel.setPermissionResponse(true)
                    } else {
                        // Permissions denied, handle accordingly
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Aplikasi tidak mendapat izin")
                        builder.setMessage(" Anda harus memberikan izin untuk menggunakan aplikasi ini.")
                        builder.setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                            finish()
                        }
                        builder.show()

                    }
                } else {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                    ) {
//                        proceedApplication()
                        homeViewModel.setPermissionResponse(true)
                    } else {
                        // Permissions denied, handle accordingly
                        homeViewModel.setPermissionResponse(false)
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Aplikasi tidak mendapat izin")
                        builder.setMessage(" Anda harus memberikan izin untuk menggunakan aplikasi ini.")
                        builder.setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                            finish()
                        }
                        builder.show()

                    }
                }

            }
        }
    }

    fun proceedApplication() {

        homeViewModel.getSessionData().observe(this) {
            Handler(Looper.getMainLooper()).postDelayed({
                // Pindahkan ke halaman berikutnya
                if (it.isLogin == true) {
                    i = Intent(this, HomeActivity::class.java)
                    startActivity(i)
                    Log.d("SplashActivity", " Home isLogin: ${it.isLogin}")
                } else {
                    i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    Log.d("SplashActivity", "Main isLogin: ${it.isLogin}")
                }
                // Tutup tampilan splash screen
                finish()
            }, 2000)
        }
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_VIDEO
                    ) == PackageManager.PERMISSION_GRANTED) &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        readImagePermission
                    ) == PackageManager.PERMISSION_GRANTED)
        }

    }


    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionsTiramisu()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CAMERA_AND_STORAGE)
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissionsTiramisu() {
        requestPermissions(REQUIRED_PERMISSIONS_TIRAMISU, PERMISSIONS_REQUEST_CAMERA_AND_STORAGE)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val REQUIRED_PERMISSIONS_TIRAMISU = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS
        )

    }

    private fun isModelFileExists(fileName: String): Boolean {
        val file = File(getExternalFilesDir("models"), fileName)
        Log.d("SplashActivity", "isModelFileExists: ${file.path}")
        return file.exists()
    }

    private fun checkModelFile(modelNames: List<String>, requiredModelUrl: MutableList<String>) {
        for (modelName in modelNames) {
            val isModelExists = isModelFileExists(modelName)
            if (!isModelExists) {
                requiredModelUrl.add(modelUrls[modelNames.indexOf(modelName)])
                downloadCount++
            }
        }
        homeViewModel.isDownloaded.value = Event(true)
    }

    private fun showLoading() {
        homeViewModel.isLoading.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }


}