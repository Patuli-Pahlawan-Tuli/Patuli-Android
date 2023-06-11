package com.puxxbu.PatuliApp.ui.fragments.quiz

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityQuizBinding
import com.puxxbu.PatuliApp.databinding.DialogCloseConfirmationBinding
import com.puxxbu.PatuliApp.ui.fragments.quiz.camera.QuizCameraFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private val quizViewModel : QuizViewModel by viewModel()

    private val fragmentManager = supportFragmentManager
    private val cameraFragment = QuizCameraFragment()


    companion object {
        const val EXTRA_QUIZ_DIFFICULTY = "extra_type"
        const val EXTRA_NUMBER = "extra_number"
        const val EXTRA_LEVEL = "extra_level"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)

        setupView()
        setupAction()

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        showDialogClose()
    }



    private fun setupAction() {
        binding.topAppBar.setNavigationOnClickListener {
            showDialogClose()
        }

    }

    private fun setupView() {
        supportActionBar?.hide()
        showLoading()
        val difficulty = intent.getStringExtra(EXTRA_QUIZ_DIFFICULTY)
        val level = intent.getIntExtra(EXTRA_LEVEL, 0)


        quizViewModel.setNumber(intent.getIntExtra(EXTRA_NUMBER, 1))
        binding.apply {



            quizViewModel.getSessionData().observe(this@QuizActivity) {user ->
                if (difficulty != null) {
                    quizViewModel.quizNumber.observe(this@QuizActivity) {number ->
                        quizViewModel.getQuizData(user.token, difficulty, level, number)
                        progressBar.progress = number
                    }
                }
            }

            quizViewModel.quizData.observe(this@QuizActivity) {quiz ->
                var tipeKuis = ""
                when(quiz.data[0].quizDifficulty){
                    "Beginner" -> {
                        tipeKuis = "Pemula"
                    }
                    "Intermediate" -> {
                        tipeKuis = "Menengah"
                    }
                    "Expert" -> {
                        tipeKuis = "Mahir"
                    }
                }
                topAppBar.setTitle(getString(R.string.app_bar_quiz, tipeKuis))
                tvQuizProgress.text = getString(R.string.quiz_progress_number, quiz.data[0].quizNumber.toString(), "5")
                tvQuestionNumber.text = getString(R.string.quiz_number, quiz.data[0].quizNumber.toString())
                tvQuestion.text = quiz.data[0].quiz
            }
        }

        quizViewModel.quizData.observe(this@QuizActivity) {quiz ->

//            addQuizDataPreferences(quiz.data[0])


            Log.d("QuizActivity", "setupViewJawaban: ${quiz.data[0].answer}")


            Log.d("QuizActivity", "setupView: start fragment")
            fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, cameraFragment, QuizCameraFragment::class.java.simpleName)
                .commit()
        }


        Log.d("QuizActivity", "setupViewNumber: ${intent.getIntExtra(EXTRA_NUMBER, 1)}")




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

    private fun showLoading() {
        quizViewModel.isLoading.observe(this) {
            if (it) {
                binding.apply {
                    loading1.visibility = View.VISIBLE
                    tvQuizProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }
            } else {
                binding.apply {
                    loading1.visibility = View.GONE
                    tvQuizProgress.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

}