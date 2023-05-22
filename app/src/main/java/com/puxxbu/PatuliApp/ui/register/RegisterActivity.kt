package com.puxxbu.PatuliApp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityRegisterBinding
import com.puxxbu.PatuliApp.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.prefs.Preferences
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val registerViewModel : RegisterViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        showLoading()
        binding.btnRegister.setOnClickListener {
            val name = binding.tietName.text.toString()
            val email = binding.tietEmail.text.toString()
            val password = binding.tietPassword.text.toString()
            val confirmationPasword = binding.tietConfirmPassword.text.toString()

            binding.tietName.error = null
            binding.tietEmail.error = null
            binding.tietPassword.error = null
            binding.tietConfirmPassword.error = null

            when{
                name.isEmpty() -> {
                    binding.tietName.error = getString(R.string.error_name_empty)
                }
                email.isEmpty() -> {
                    binding.tietEmail.error = getString(R.string.error_email_empty)
                }
                password.isEmpty() -> {
                    binding.tietPassword.error = getString(R.string.error_password_empty)
                }
                confirmationPasword.isEmpty() -> {
                    binding.tietConfirmPassword.error = getString(R.string.error_password_empty)
                }
                password != confirmationPasword -> {
                    binding.tietConfirmPassword.error = getString(R.string.error_password_not_match)
                }
                else -> {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8){
                        registerViewModel.register(name, email, password, confirmationPasword)
                        registerCheck()
                    } else {
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            binding.tietEmail.error = getString(R.string.error_email_not_valid)
                        }
                        if (password.length < 8){
                            binding.tietPassword.error = getString(R.string.error_password_not_valid)
                        }
                    }
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun registerCheck() {
        registerViewModel.responseMessage.observe(this) {
            it.getContentIfNotHandled()?.let {
                Log.d("RegisterActivity", it)
                if (it == "Account Created") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.register_success)
                    builder.setPositiveButton("OK") { dialog, which ->
                        finish()
                    }
                    builder.show()

                } else {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Register Gagal")
                    builder.setMessage(it)
                    builder.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.show()

                }
            }
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun showLoading() {
        registerViewModel.isLoading.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}