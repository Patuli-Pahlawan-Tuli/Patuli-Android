package com.puxxbu.PatuliApp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.ui.home.HomeActivity
import com.puxxbu.PatuliApp.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 3000 // 3 detik
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var i: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

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


}

