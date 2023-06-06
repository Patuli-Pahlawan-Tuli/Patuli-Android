package com.puxxbu.PatuliApp.ui.fragments.quiz.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityCameraBinding
import com.puxxbu.PatuliApp.ui.fragments.camera.CameraFragment

class QuizCameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private val fragmentManager = supportFragmentManager
    private val cameraFragment = CameraFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, cameraFragment, CameraFragment::class.java.simpleName)
            .commit()
        supportActionBar?.hide()
        setContentView(binding.root)
    }
}