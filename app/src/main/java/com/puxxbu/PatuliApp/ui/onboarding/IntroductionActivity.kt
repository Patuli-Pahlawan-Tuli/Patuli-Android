package com.puxxbu.PatuliApp.ui.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityCameraBinding
import com.puxxbu.PatuliApp.databinding.ActivityIntroductionBinding
import com.puxxbu.PatuliApp.ui.fragments.camera.CameraFragment

class IntroductionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroductionBinding

    private val fragmentManager = supportFragmentManager
    private val viewPagerFragment = ViewPagerFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, viewPagerFragment, ViewPagerFragment::class.java.simpleName)
            .commit()
        supportActionBar?.hide()
        setContentView(binding.root)
    }
}