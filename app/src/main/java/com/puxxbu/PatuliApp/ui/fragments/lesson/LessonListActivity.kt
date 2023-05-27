package com.puxxbu.PatuliApp.ui.fragments.lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puxxbu.PatuliApp.data.model.detailLessonItem
import com.puxxbu.PatuliApp.databinding.ActivityDetailLessonBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonListAdapter

class LessonListActivity : AppCompatActivity() {

    private val TAG = "DetailLessonActivity"
    private lateinit var binding: ActivityDetailLessonBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLessonBinding.inflate(layoutInflater)

        setupView()
        setupAction()

        setContentView(binding.root)
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

    private fun setupView() {
        val recyclerView : RecyclerView = binding.rvLessons
        val items = detailLessonItem
        val adapter = LessonListAdapter(items)

        recyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(this,2)
        recyclerView.layoutManager = layoutManager


        supportActionBar?.hide()

    }
}