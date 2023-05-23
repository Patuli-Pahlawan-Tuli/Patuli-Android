package com.puxxbu.PatuliApp.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityHomeBinding
import com.puxxbu.PatuliApp.ui.OnboardingActivity
import com.puxxbu.PatuliApp.ui.fragments.camera.CameraFragment
import com.puxxbu.PatuliApp.ui.fragments.camera.PermissionsFragment
import com.puxxbu.PatuliApp.ui.fragments.profile.ProfileFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeViewModel by viewModel()

    private val fragmentManager = supportFragmentManager
    private val permissionsFragment = PermissionsFragment()
    private val cameraFragment = CameraFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        homeViewModel.getSessionData().observe(this) {
            if (!it.isLogin) {
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            } else {
                // Tampilkan PermissionsFragment jika user belum memberikan izin
                if (!checkCameraPermission()) {
                    fragmentManager.beginTransaction()
                        .add(
                            R.id.fragment_container,
                            permissionsFragment,
                            PermissionsFragment::class.java.simpleName
                        )
                        .commit()
                } else {
                    // Tampilkan CameraFragment jika user telah memberikan izin
                    fragmentManager.beginTransaction()
                        .add(
                            R.id.fragment_container,
                            cameraFragment,
                            CameraFragment::class.java.simpleName
                        )
                        .commit()
                }

                binding.bottomNavigation.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.item_1 -> {
                            // Tampilkan CameraFragment
                            fragmentManager.beginTransaction()
                                .hide(profileFragment)
                                .hide(permissionsFragment)
                                .show(cameraFragment)
                                .commit()
                            true
                        }
                        R.id.item_4 -> {
                            // Tampilkan ProfileFragment
                            fragmentManager.beginTransaction()
                                .hide(cameraFragment)
                                .hide(permissionsFragment)
                                .show(profileFragment)
                                .commit()
                            true
                        }
                        else -> false
                    }
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


    override fun onDestroy() {
        super.onDestroy()
        // Hapus Fragment pada onDestroy()
        fragmentManager.beginTransaction()
            .remove(permissionsFragment)
            .remove(cameraFragment)
            .remove(profileFragment)
            .commit()
    }

}