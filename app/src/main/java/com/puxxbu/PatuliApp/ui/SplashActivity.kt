package com.puxxbu.PatuliApp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.ui.home.HomeActivity
import com.puxxbu.PatuliApp.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 3000 // 3 detik
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var i: Intent
    private val PERMISSIONS_REQUEST_CAMERA_AND_STORAGE = 1001

    private val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        if (checkPermissions()){
            proceedApplication()
        }else{
            requestPermissions()
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (checkPermissionsTiramisu()){
//                proceedApplication()
//            }else{
//                requestPermissionsTiramisu()
//            }
//        } else {
//            if (checkPermissions()){
//                proceedApplication()
//            }else{
//                requestPermissions()
//            }
//
//        }

//        if (checkPermissions()) {
//            proceedApplication()
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                requestPermissionsTiramisu()
//            } else {
//                requestPermissions()
//            }
//        }


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
                        grantResults[3] == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Permissions granted, proceed with camera or file operation
                        proceedApplication()
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
                        // Permissions granted, proceed with camera or file operation
                        proceedApplication()
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
            return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED)
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

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun checkPermissionsTiramisu(): Boolean {
//        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED)
//    }

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
            Manifest.permission.READ_MEDIA_VIDEO

        )

    }


}

