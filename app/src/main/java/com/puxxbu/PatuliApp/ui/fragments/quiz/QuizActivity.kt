package com.puxxbu.PatuliApp.ui.fragments.quiz

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.api.response.quiz.DataItem
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
        val difficulty = intent.getStringExtra(EXTRA_QUIZ_DIFFICULTY)

        quizViewModel.setNumber(intent.getIntExtra(EXTRA_NUMBER, 1))
        binding.apply {



            quizViewModel.getSessionData().observe(this@QuizActivity) {user ->
                if (difficulty != null) {
                    quizViewModel.quizNumber.observe(this@QuizActivity) {number ->
                        quizViewModel.getQuizData(user.token, difficulty, number)
                        progressBar.progress = number
                    }
                }
            }

            quizViewModel.quizData.observe(this@QuizActivity) {quiz ->
                topAppBar.setTitle(getString(R.string.app_bar_quiz, quiz.data[0].quizDifficulty))
                tvQuizProgress.text = getString(R.string.quiz_progress_number, quiz.data[0].quizNumber.toString(), "5")
                tvQuestionNumber.text = getString(R.string.quiz_number, quiz.data[0].quizNumber.toString())
                tvQuestion.text = quiz.data[0].quiz
            }
        }

        quizViewModel.quizData.observe(this@QuizActivity) {quiz ->

            addQuizDataPreferences(quiz.data[0])


            Log.d("QuizActivity", "setupViewJawaban: ${quiz.data[0].answer}")


            Log.d("QuizActivity", "setupView: start fragment")
            fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, cameraFragment, QuizCameraFragment::class.java.simpleName)
                .commit()
        }


        Log.d("QuizActivity", "setupViewNumber: ${intent.getIntExtra(EXTRA_NUMBER, 1)}")




    }

    private fun addQuizDataPreferences(quizData : DataItem){
        val sharedPreferences = getSharedPreferences("quiz_data_preferences", Context.MODE_PRIVATE)

        Log.d("QuizActivity", "addQuizDataPreferences: ${quizData.answer} ${quizData.quizNumber}")
        val editor = sharedPreferences.edit()

        editor.putString(QuizCameraFragment.EXTRA_QUIZ_DIFFICULTY, quizData.quizDifficulty)
        editor.putInt(QuizCameraFragment.EXTRA_NUMBER, quizData.quizNumber)
        editor.putString(QuizCameraFragment.EXTRA_TYPE, quizData.languageType)
        editor.putString(QuizCameraFragment.EXTRA_ANSWER, quizData.answer)

        editor.apply()
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