package com.puxxbu.PatuliApp.ui.fragments.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityChangePasswordBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val profileViewModel: ProfileViewModel by viewModel()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupView()
        setupAction()
    }


    private fun setupAction() {
        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
        }

        profileViewModel.getSessionData().observe(this) {
           changePassword(it.token)
            Log.d("ChangePasswordActivity", "changePassword: $it.token")
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun changePassword(token : String){
        binding.btnChangePassword.setOnClickListener {
            Log.d("ChangePasswordActivity", "changePassword: $token")
            val oldPassword = binding.tietOldPassword.text.toString()
            val newPassword = binding.tietNewPassword.text.toString()
            val confirmPassword = binding.tietConfirmPassword.text.toString()
            when {
                oldPassword.isEmpty() -> {
                    binding.tilOldPassword.error = "Masukkan password lama"
                }
                newPassword.isEmpty() -> {
                    binding.tilNewPassword.error = "Masukkan password baru"
                }
                confirmPassword.isEmpty() -> {
                    binding.tilConfirmPassword.error = "Masukkan konfirmasi password"
                }
                newPassword != confirmPassword -> {
                    binding.tilConfirmPassword.error = "Password tidak sama"
                }
                else -> {
                    profileViewModel.changePassword(token, oldPassword, newPassword, confirmPassword)
                    profileViewModel.editPasswordResponse.observe(this) {
                        it.getContentIfNotHandled()?.let {
                            Log.d("ChangePasswordActivity", "changePassword: $it")
                            if (it.error == false) {
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Berhasil")
                                builder.setMessage("Password berhasil diubah")
                                builder.setPositiveButton("OK") { dialog, which ->
                                    resetText()
                                    finish()
                                }
                                builder.show()
                            } else {
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Gagal")
                                builder.setMessage("Password gagal diubah \n ${it.message}")
                                builder.setPositiveButton("OK") { dialog, which ->
                                    resetText()
                                }
                                builder.show()
                            }
                        }


                    }
                }
            }
        }
    }

    private fun resetText(){
        binding.tietOldPassword.setText("")
        binding.tietNewPassword.setText("")
        binding.tietConfirmPassword.setText("")
    }
}