package com.puxxbu.PatuliApp.ui.fragments.lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puxxbu.PatuliApp.data.model.detailLessonItem
import com.puxxbu.PatuliApp.databinding.ActivityDetailLessonBinding
import com.puxxbu.PatuliApp.databinding.ActivityLessonListBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonListAdapter
import com.puxxbu.PatuliApp.ui.fragments.lesson.viewmodel.LessonViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LessonListActivity : AppCompatActivity() {

    private val TAG = "DetailLessonActivity"
    private lateinit var binding: ActivityLessonListBinding

    private val lessonViewModel : LessonViewModel by viewModel()


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
            getLessonData(it.token, "abjad")
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

        val recyclerView : RecyclerView = binding.rvLessons
        val items = detailLessonItem

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