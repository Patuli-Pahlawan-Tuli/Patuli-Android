package com.puxxbu.PatuliApp.ui.fragments.lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.alphabethItemList
import com.puxxbu.PatuliApp.databinding.ActivityDetailLessonBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonListAdapter
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonNavAdapter
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
        binding.apply {
            val type = intent.getStringExtra(EXTRA_TYPE)
            var number = intent.getIntExtra(EXTRA_NUMBER,0)
            val size = intent.getIntExtra(EXTRA_SIZE,0)
            Toast.makeText(this@DetailLessonActivity, "$number", Toast.LENGTH_SHORT).show()

            lessonViewModel.setNumber(number)

            lessonViewModel.pageNumber.observe(this@DetailLessonActivity) {
                btnPrevious.isEnabled = number > 1
                btnNext.isEnabled = number < size
            }

            lessonViewModel.getSessionData().observe(this@DetailLessonActivity) {user ->
                if (type != null) {
//
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
                    .load(it.data.imageUrl)
                    .fitCenter()
                    .into(ivLessonPhoto)
                topAppBar.title = it.data.lessonName

            }


            fabNavigationPage.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialog_lesson_nav, null)
                val rvDialog = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_lessons)

                val recyclerView : RecyclerView = rvDialog

                lessonViewModel.lessonData.observe(this@DetailLessonActivity) {
                    val adapter = LessonNavAdapter(alphabethItemList)
                    recyclerView.adapter = adapter
                }

                val layoutManager = GridLayoutManager(this@DetailLessonActivity,5)
                recyclerView.layoutManager = layoutManager


                val builder = MaterialAlertDialogBuilder(this@DetailLessonActivity)
                builder.setView(dialogView)
                val dialog = builder.create()

                dialog.show()
            }


        }


        supportActionBar?.hide()
    }


}