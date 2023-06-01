package com.puxxbu.PatuliApp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.puxxbu.PatuliApp.databinding.ActivityOnboardingBinding
import com.puxxbu.PatuliApp.ui.login.LoginActivity
import com.puxxbu.PatuliApp.ui.register.RegisterActivity

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Log.d("MainActivity", "Login Button Clicked")
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            Log.d("MainActivity", "Register Button Clicked")
        }
    }

    private fun setupView() {
//        val windowInsetsController =
//            WindowCompat.getInsetsController(window, window.decorView) ?: return
//        windowInsetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

        supportActionBar?.hide()
    }

}