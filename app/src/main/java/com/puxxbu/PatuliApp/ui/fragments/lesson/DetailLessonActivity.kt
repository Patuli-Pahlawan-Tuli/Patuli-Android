package com.puxxbu.PatuliApp.ui.fragments.lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
        setContentView(binding.root)
    }

    private fun setupView() {
        binding.apply {
            val id = intent.getStringExtra(EXTRA_ID)
            val type = intent.getStringExtra(EXTRA_TYPE)
            val number = intent.getIntExtra(EXTRA_NUMBER,0)
            val size = intent.getIntExtra(EXTRA_SIZE,0)

            lessonViewModel.getSessionData().observe(this@DetailLessonActivity) {
                if (type != null) {
                    lessonViewModel.getDetailLessonData(it.token, type, number)
                }else{
                    Toast.makeText(this@DetailLessonActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            lessonViewModel.detailLessonData.observe(this@DetailLessonActivity) {
                Glide.with(this@DetailLessonActivity)
                    .load(it.data.imageUrl)
                    .fitCenter()
                    .into(ivLessonPhoto)
                topAppBar.title = it.data.lessonName

            }
        }


        supportActionBar?.hide()
    }


}