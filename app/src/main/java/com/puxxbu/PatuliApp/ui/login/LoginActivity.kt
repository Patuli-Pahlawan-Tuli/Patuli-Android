package com.puxxbu.PatuliApp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.UserDataModel
import com.puxxbu.PatuliApp.databinding.ActivityLoginBinding
import com.puxxbu.PatuliApp.databinding.DialogFailedBinding
import com.puxxbu.PatuliApp.databinding.DialogSuccessBinding
import com.puxxbu.PatuliApp.ui.main.MainActivity
import com.puxxbu.PatuliApp.ui.register.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        showLoading()
        binding.btnLogin.setOnClickListener {
            val email = binding.tietEmail.text.toString()
            val password = binding.tietPassword.text.toString()
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            when {
                email.isEmpty() -> {
                    binding.tilEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.tilPassword.error = "Masukkan password"
                }
                else -> {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
                        loginViewModel.postLogin(email, password)

                    } else {
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            binding.tilEmail.error = getString(R.string.error_email_not_valid)
                        }
                    }

                    loginCheck()

                }
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            Log.d("RegisterActivity", "Login")
        }
    }



    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun loginCheck() {

        loginViewModel.responseMessage.observe(this) {
            it.getContentIfNotHandled()?.let {
                Log.d("LoginActivity", it)
                if (it == "success") {
                    saveSession()
                    showDialogSuccess("Login Berhasil")

                } else {
                    showDialogFailed("Login Gagal", it)
                }
            }
        }


    }

    private fun saveSession() {
        loginViewModel.loginResponse.observe(this) {
            Log.d("LoginActivity", it.data.name)

            val userData = UserDataModel(
                it.data.name,
                it.data.id,
                it.data.token,
                true
            )
            loginViewModel.saveSession(userData)
        }


    }


    private fun showLoading() {
        loginViewModel.isLoading.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showDialogSuccess(message : String){
        val dialogView = DialogSuccessBinding.inflate(layoutInflater)
        val okButton = dialogView.okButton
        val tvTitle = dialogView.dialogTitle

        val builder = MaterialAlertDialogBuilder(this@LoginActivity)
        builder.setView(dialogView.root)

        val dialog = builder.create()
        okButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        tvTitle.text = message

        dialog.show()
    }

    private fun showDialogFailed(message : String, desc : String){
        val dialogView = DialogFailedBinding.inflate(layoutInflater)
        val okButton = dialogView.okButton
        val tvTitle = dialogView.dialogTitle

        val builder = MaterialAlertDialogBuilder(this@LoginActivity)
        builder.setView(dialogView.root)

        val dialog = builder.create()
        okButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.dialogDesc.text = desc
        tvTitle.text = message

        dialog.show()
    }

}