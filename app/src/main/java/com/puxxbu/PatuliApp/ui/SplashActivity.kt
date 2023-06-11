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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.BuildConfig
import com.puxxbu.PatuliApp.PatuliApp.Companion.context
import com.puxxbu.PatuliApp.data.api.response.file.Data
import com.puxxbu.PatuliApp.databinding.ActivitySplashBinding
import com.puxxbu.PatuliApp.databinding.DialogChooseModelBinding
import com.puxxbu.PatuliApp.ui.main.MainActivity
import com.puxxbu.PatuliApp.ui.main.MainViewModel
import com.puxxbu.PatuliApp.utils.Event
import com.puxxbu.PatuliApp.utils.calculateHash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val homeViewModel: MainViewModel by viewModel()
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
        BuildConfig.URL_ANGKA,
        BuildConfig.URL_KATA,
    )

    private val modelLiteUrls = listOf(
        BuildConfig.URL_ABJAD_LITE,
        BuildConfig.URL_ANGKA_LITE,
        BuildConfig.URL_KATA_LITE,
    )

    private var downloadCount: Int = 0
    private var isDownloaded: Boolean = false
    private var modelType : Int = 0

    private val modelNames = listOf(
        "abjad.tflite",
        "angka.tflite",
        "kata.tflite",
    )

    private val modelLiteNames = listOf(
        "abjad_lite.tflite",
        "angka_lite.tflite",
        "kata_lite.tflite",
    )


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setIsDownloaded()
        showLoading()
        homeViewModel.setDownloadResponse(false)

        homeViewModel.getSessionData().observe(this) {
            homeViewModel.getHashFileData(it.token)
            Log.d("SplashActivity", "hash masuk: ")
        }
        homeViewModel.fileHashResponse.observe(this) {
            Log.d("SplashActivity", "hash disave: ${it.data}")
            saveDataToSharedPreferences(it.data)
            homeViewModel.permissionResponse.observe(this) {
                if (it) {
                    Log.d("SplashActivity", "onCreate: permission granted")
                    if (isDownloaded){
                        downloadModels()
                    }else{
                        showDialogChooseModel()
                    }
                }
            }

        }


        supportActionBar?.hide()

        homeViewModel.setDownloadCount(0)
        homeViewModel.isDownloaded.value = Event(false)


        if (!checkPermissions()) {
            homeViewModel.setPermissionResponse(false)
            requestPermissions()
        } else {
            homeViewModel.setPermissionResponse(true)
        }






        homeViewModel.isDownloaded.observe(this) {
            it.getContentIfNotHandled()?.let { downloaded ->
                if (downloaded) {
                    homeViewModel.downloadCount.observe(this) {
                        if (it == downloadCount) {
                            Log.d("SplashActivity", "onCreate: $downloadCount")
//                            Toast.makeText(
//                                this@SplashActivity,
//                                "Models downloaded",
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
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
Log.d("SplashActivity", "downloadModels: ")
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val requiredModelUrl = mutableListOf<String>()
        Log.d("SplashActivity", "onCreate: show dialog $modelType")

        val modelUsed = when (modelType) {
            1 -> modelLiteNames
            else -> modelNames
        }
        checkModelFile(modelUsed, requiredModelUrl)

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
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setDescription("Downloading model")
            val downloadId = downloadManager.enqueue(request)
            downloadIds.add(downloadId)
//            Toast.makeText(this, "Downloading model", Toast.LENGTH_SHORT).show()
        }


        val onCompleteReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                var allModelsDownloaded = false
                while (!allModelsDownloaded) {
                    allModelsDownloaded = downloadIds.all { isModelDownloaded(it) }
                    if (!allModelsDownloaded) {
                        Log.d("SplashActivity", "onReceive: belum semua download")
                        Thread.sleep(300)
                    }
                }

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
        var isStartActivityExecuted = false

        homeViewModel.setLogin(Event(false))

        homeViewModel.getSessionData().observe(this) {

            // Pindahkan ke halaman berikutnya
            if (it.isLogin == true) {

                homeViewModel.getProfile(it.token)
                homeViewModel.profileData.observe(this) {
                    if (it.data != null) {
                        i = Intent(this, MainActivity::class.java)

                    } else {
                        i = Intent(this, OnBoardingActivity::class.java)

                    }
                    homeViewModel.setLogin(Event(true))

                }
                Log.d("SplashActivity", " Home isLogin: ${it.isLogin}")
            } else {
                i = Intent(this, OnBoardingActivity::class.java)
                homeViewModel.setLogin(Event(true))
                Log.d("SplashActivity", "Main isLogin: ${it.isLogin}")
            }

            homeViewModel.isLogin.observe(this) {
                it.getContentIfNotHandled()?.let { isLogin ->
                    if (isLogin && !isStartActivityExecuted) {
                        Log.d("SplashActivity", "Main start activity")
                        startActivity(i)
                        isStartActivityExecuted = true
                        finish()
                    }
                }
            }


        }

        Handler(Looper.getMainLooper()).postDelayed({
            CoroutineScope(Dispatchers.Main).launch {
            }
        }, 3000)


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
        val sharedPreferences =
            context.getSharedPreferences("hash_shared_pref", Context.MODE_PRIVATE)
        val file = File(getExternalFilesDir("models"), fileName)
        Log.d("SplashActivity", "isModelFileExists: ${file.exists()}")
        if (file.exists()) {

            val hash = calculateHash(file)
            var expectedHash = ""

            if (modelType == 1){
                Log.d("SplashActivity", "isModelFileExists: Model Lite")
                when (fileName) {
                    "abjad_lite.tflite" -> expectedHash = sharedPreferences.getString("abjad_lite", "").toString()
                    "angka_lite.tflite" -> expectedHash = sharedPreferences.getString("angka_lite", "").toString()
                    "kata_lite.tflite" -> expectedHash = sharedPreferences.getString("kata_lite", "").toString()
                }
            }else{
                Log.d("SplashActivity", "isModelFileExists: Model Normal")
                when (fileName) {
                    "abjad.tflite" -> expectedHash = sharedPreferences.getString("abjad", "").toString()
                    "angka.tflite" -> expectedHash = sharedPreferences.getString("angka", "").toString()
                    "kata.tflite" -> expectedHash = sharedPreferences.getString("kata", "").toString()
                }
            }


            Log.d("SplashActivity", "isModelFileExists hash diambil: ${expectedHash} \n ${hash}")
            if (hash != expectedHash) {
                file.delete()
                return false
            }
            return true
        } else {
            return false
        }

    }


    private fun checkModelFile(modelNames: List<String>, requiredModelUrl: MutableList<String>) {
        for (modelName in modelNames) {
            val isModelExists = isModelFileExists(modelName)
            Log.d("SplashActivity", "onCreate: show model name $modelName")


            val usedModelUrls = if (modelType == 1) modelLiteUrls else modelUrls

            if (!isModelExists) {
                requiredModelUrl.add(usedModelUrls[modelNames.indexOf(modelName)])
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

    fun saveDataToSharedPreferences(data: Data) {
        val sharedPreferences =
            context.getSharedPreferences("hash_shared_pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("abjad_lite", data.abjadLite)
        editor.putString("kata", data.kata)
        editor.putString("kata_lite", data.kataLite)
        editor.putString("abjad", data.abjad)
        editor.putString("angka", data.angka)
        editor.putString("angka_lite", data.angkaLite)

        editor.apply()
    }

    fun setIsDownloaded(){
        val sharedPreferences =
            context.getSharedPreferences("model_type", Context.MODE_PRIVATE)
        isDownloaded = sharedPreferences.getBoolean("is_model_saved", false)
        modelType = sharedPreferences.getInt("model_type", 0)
        Log.d("SplashActivity", "setIsDownloaded: $isDownloaded ")
    }

    fun saveModelPreferences(modelType: Int){
        val sharedPreferences =
            context.getSharedPreferences("model_type", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("model_type", modelType)
        editor.putBoolean("is_model_saved", true)
        editor.apply()
    }

    private fun showDialogChooseModel() {
        val dialogView = DialogChooseModelBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this).setView(dialogView.root)

        val dialog = builder.create()
        var isDismissedWithButton = false

        dialogView.btnNormal.setOnClickListener {
            setModelTypeAndDownload(0)
            isDismissedWithButton = true
            dialog.dismiss()
        }

        dialogView.btnLite.setOnClickListener {
            setModelTypeAndDownload(1)
            isDismissedWithButton = true
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            if (!isDismissedWithButton) {
                setModelTypeAndDownload(0)
            }
        }

        dialog.show()
    }

    private fun setModelTypeAndDownload(type: Int) {
        modelType = type
        saveModelPreferences(type)
        downloadModels()
    }

}