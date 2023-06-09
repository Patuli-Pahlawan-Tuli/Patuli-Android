package com.puxxbu.PatuliApp.ui.fragments.lesson

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puxxbu.PatuliApp.databinding.ActivityLessonListBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonListAdapter
import com.puxxbu.PatuliApp.ui.fragments.lesson.viewmodel.LessonViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LessonListActivity : AppCompatActivity() {

    private val TAG = "DetailLessonActivity"
    private lateinit var binding: ActivityLessonListBinding

    private val lessonViewModel : LessonViewModel by viewModel()

    companion object {
        const val EXTRA_LESSON_NAME = "extra_lesson_name"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonListBinding.inflate(layoutInflater)

        setupData()
        setupView()
        setupAction()

        setContentView(binding.root)
    }

    private fun setupData() {

        lessonViewModel.getSessionData().observe(this) {
            getLessonData(it.token, intent.getStringExtra(EXTRA_LESSON_NAME)?.lowercase() ?: "")
        }

    }


    private fun setupAction() {

        binding.topAppBar.setNavigationOnClickListener {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }

    }

    private fun getLessonData(token :String, type :String) {
        lessonViewModel.getLessonListData(token, type)
    }
    private fun setupView() {
        showLoading()

        binding.topAppBar.title = intent.getStringExtra(EXTRA_LESSON_NAME)

        val recyclerView : RecyclerView = binding.rvLessons

        lessonViewModel.lessonData.observe(this) {
            val adapter = LessonListAdapter(it.data)
            recyclerView.adapter = adapter
        }

        val layoutManager = GridLayoutManager(this,2)
        recyclerView.layoutManager = layoutManager


        supportActionBar?.hide()

    }

    private fun showLoading() {
        lessonViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}