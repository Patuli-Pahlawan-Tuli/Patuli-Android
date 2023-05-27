package com.puxxbu.PatuliApp.ui.fragments.lesson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.itemLesson
import com.puxxbu.PatuliApp.databinding.FragmentLessonBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonAdapter


class LessonFragment : Fragment() {

    private val TAG = "LessonFragment"
    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLessonBinding.inflate(inflater, container, false)


        setupView()
        setupAction()


        return binding.root
    }

    private fun setupAction() {

    }

    private fun setupView() {
        val recyclerView : RecyclerView = binding.rvLessons

        val items = itemLesson
        val adapter = LessonAdapter(items)

        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager


    }

    companion object {

    }
}