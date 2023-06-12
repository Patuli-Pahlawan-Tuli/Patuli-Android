package com.puxxbu.PatuliApp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.puxxbu.PatuliApp.PatuliApp
import com.puxxbu.PatuliApp.databinding.ActivityOnboardingBinding
import com.puxxbu.PatuliApp.ui.login.LoginActivity
import com.puxxbu.PatuliApp.ui.main.MainActivity
import com.puxxbu.PatuliApp.ui.onboarding.IntroductionActivity
import com.puxxbu.PatuliApp.ui.register.RegisterActivity

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private var isFinished : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = PatuliApp.context.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        isFinished = sharedPreferences.getBoolean("Finished", false)

        if (!isFinished){
            val intent = Intent(this, IntroductionActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


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