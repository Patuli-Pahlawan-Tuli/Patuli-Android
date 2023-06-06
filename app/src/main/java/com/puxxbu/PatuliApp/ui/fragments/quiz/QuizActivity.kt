package com.puxxbu.PatuliApp.ui.fragments.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityQuizBinding
import com.puxxbu.PatuliApp.databinding.DialogCloseConfirmationBinding
import com.puxxbu.PatuliApp.databinding.DialogFailedBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private val quizViewModel : QuizViewModel by viewModel()


    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_NUMBER = "extra_number"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)

        setupView()
        setupAction()

        setContentView(binding.root)
    }

    private fun setupAction() {
        binding.topAppBar.setNavigationOnClickListener {
            showDialogClose()
        }

    }

    private fun setupView() {
        supportActionBar?.hide()
        quizViewModel.setNumber(1)

        binding.apply {
            val type = intent.getStringExtra(EXTRA_TYPE)

            quizViewModel.getSessionData().observe(this@QuizActivity) {user ->
                if (type != null) {
                    quizViewModel.getQuizData(user.token, type, 1)
                }
            }

            btnCamera.setOnClickListener {



            }



            quizViewModel.quizData.observe(this@QuizActivity) {quiz ->
                topAppBar.setTitle(getString(R.string.app_bar_quiz, quiz.data[0].quizDifficulty))
                tvQuizProgress.text = getString(R.string.quiz_progress_number, quiz.data[0].quizNumber.toString(), "5")
                tvQuestionNumber.text = getString(R.string.quiz_number, quiz.data[0].quizNumber.toString())
                tvQuestion.text = quiz.data[0].quiz
            }
        }

    }

    private fun showDialogClose(){
        val dialogView = DialogCloseConfirmationBinding.inflate(layoutInflater)
        val okButton = dialogView.okButton
        val dismissButton = dialogView.btnDismiss

        val builder = MaterialAlertDialogBuilder(this@QuizActivity)
        builder.setView(dialogView.root)



        val dialog = builder.create()

        dialog.show()

        okButton.setOnClickListener {
            finish()
            dialog.dismiss()
        }

        dismissButton.setOnClickListener {
            dialog.dismiss()
        }



    }
}