package com.puxxbu.PatuliApp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        if (checkPermissions()) {
            proceedApplication()
        } else {
            requestPermissions()
        }


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA_AND_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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

    fun proceedApplication(){
        homeViewModel.getSessionData().observe(this) {
            Handler(Looper.getMainLooper()).postDelayed({
                // Pindahkan ke halaman berikutnya
                if (it.isLogin == true) {
                    i = Intent(this, HomeActivity::class.java)
                    startActivity(i)
                } else {
                    i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }
                // Tutup tampilan splash screen
                finish()
            }, 2000)
        }
    }

    private fun checkPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CAMERA_AND_STORAGE)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    }


}

