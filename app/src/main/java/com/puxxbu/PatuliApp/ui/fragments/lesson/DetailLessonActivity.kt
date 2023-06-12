package com.puxxbu.PatuliApp.ui.fragments.lesson

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.databinding.ActivityDetailLessonBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.viewmodel.LessonViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailLessonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailLessonBinding
    private val lessonViewModel : LessonViewModel by viewModel()

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_NUMBER = "extra_number"
        const val EXTRA_SIZE = "extra_size"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLessonBinding.inflate(layoutInflater)

        setupView()
        setupAction()
        setContentView(binding.root)
    }

    private fun setupAction() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupView() {

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        binding.apply {
            val type = intent.getStringExtra(EXTRA_TYPE)
            var number = intent.getIntExtra(EXTRA_NUMBER,0)
            val size = intent.getIntExtra(EXTRA_SIZE,0)

            lessonViewModel.setNumber(number)


            lessonViewModel.pageNumber.observe(this@DetailLessonActivity) {
                btnPrevious.isEnabled = number > 1
                btnNext.isEnabled = number < size
            }



            lessonViewModel.getSessionData().observe(this@DetailLessonActivity) {user ->
                if (type != null) {
                    lessonViewModel.getDetailLessonData(user.token, type, number)
                }
                if (type != null) {
                    lessonViewModel.getDetailLessonData(user.token, type, number)

                    lessonViewModel.pageNumber.observe(this@DetailLessonActivity){
                        btnNext.setOnClickListener {


                            if (number < size) {
                                lessonViewModel.setNumber(number++)
                                lessonViewModel.getDetailLessonData(user.token, type, number)
                            }
                        }

                        btnPrevious.setOnClickListener {

                            if (number > 0) {
                                lessonViewModel.setNumber(number--)
                                lessonViewModel.getDetailLessonData(user.token, type, number)
                            }
                        }
                    }

                }else{
                    Toast.makeText(this@DetailLessonActivity, "Error", Toast.LENGTH_SHORT).show()
                }

            }

            lessonViewModel.detailLessonData.observe(this@DetailLessonActivity) {
                Glide.with(this@DetailLessonActivity)
                    .load(it.data[0].imageUrl)
                    .placeholder(circularProgressDrawable)
                    .into(ivLessonPhoto)
                topAppBar.title = it.data[0].lessonName
                var lessonName = when(it.data[0].lessonType){
                    "abjad" -> "Huruf ${it.data[0].lessonName}"
                    "angka" -> "Angka ${it.data[0].lessonName}"
                    "kata" -> "${it.data[0].lessonName}"
                    else -> {}
                }
                tvLessonName.text = lessonName.toString()

            }




        }


        supportActionBar?.hide()
    }


}