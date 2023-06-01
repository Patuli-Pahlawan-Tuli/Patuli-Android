package com.puxxbu.PatuliApp.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityMainBinding
import com.puxxbu.PatuliApp.ui.fragments.camera.CameraFragment
import com.puxxbu.PatuliApp.ui.fragments.camera.PermissionsFragment
import com.puxxbu.PatuliApp.ui.fragments.lesson.LessonFragment
import com.puxxbu.PatuliApp.ui.fragments.profile.ProfileFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeViewModel: MainViewModel by viewModel()

    private val fragmentManager = supportFragmentManager
    private val permissionsFragment = PermissionsFragment()
    private val cameraFragment = CameraFragment()
    private val lessonFragment = LessonFragment()
    private val profileFragment = ProfileFragment()

    private var isLogin : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        Log.d("HomeActivity", "onCreate: DIBUAT")

        val containerFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        Log.d("HomeActivity", "onCreate: $containerFragment")
        if (containerFragment !is CameraFragment ){
            fragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container,
                    cameraFragment,
                    CameraFragment::class.java.simpleName
                )
                .commit()

        }

        homeViewModel.getSessionData().observe(this) {
            // Tampilkan PermissionsFragment jika user belum memberikan izin
            if (!checkCameraPermission() && !checkFilePermission()) {
                fragmentManager.beginTransaction()
                    .add(
                        R.id.fragment_container,
                        permissionsFragment,
                        PermissionsFragment::class.java.simpleName
                    )
                    .commit()
            }

            binding.bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.item_1 -> {
                        // Tampilkan CameraFragment
                        fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, cameraFragment, CameraFragment::class.java.simpleName)
                            .commit()
                        true
                    }
                    R.id.item_3 ->{
                        fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, lessonFragment, LessonFragment::class.java.simpleName)
                            .commit()
                        true
                    }
                    R.id.item_4 -> {
                        // Tampilkan ProfileFragment
                        fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, profileFragment, ProfileFragment::class.java.simpleName)
                            .commit()
                        true
                    }
                    else -> false
                }
            }
        }

        supportActionBar?.hide()

        setContentView(binding.root)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkFilePermission(): Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        // Hapus Fragment pada onDestroy()
//        fragmentManager.beginTransaction()
//            .remove(permissionsFragment)
//            .remove(cameraFragment)
//            .remove(profileFragment)
//            .commit()
//    }

}