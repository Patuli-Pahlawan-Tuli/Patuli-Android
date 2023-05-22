package com.puxxbu.PatuliApp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityHomeBinding
import com.puxxbu.PatuliApp.fragments.PermissionsFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        val fragmentManager = supportFragmentManager
        val permissionsFragment = PermissionsFragment()
        val fragment = fragmentManager.findFragmentByTag(PermissionsFragment::class.java.simpleName)

        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, permissionsFragment, PermissionsFragment::class.java.simpleName)
            .commit()



        //fullscreen
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

        supportActionBar?.hide()

        setContentView(binding.root)
    }

}