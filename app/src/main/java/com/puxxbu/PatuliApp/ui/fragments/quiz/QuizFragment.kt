package com.puxxbu.PatuliApp.ui.fragments.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.itemLesson
import com.puxxbu.PatuliApp.data.model.quizList
import com.puxxbu.PatuliApp.databinding.DialogQuizInfoBinding
import com.puxxbu.PatuliApp.databinding.DialogSuccessBinding
import com.puxxbu.PatuliApp.databinding.FragmentHomeBinding
import com.puxxbu.PatuliApp.databinding.FragmentQuizBinding
import com.puxxbu.PatuliApp.ui.fragments.home.HomeViewModel
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonAdapter
import com.puxxbu.PatuliApp.ui.fragments.quiz.adapter.QuizAdapter
import com.puxxbu.PatuliApp.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class QuizFragment : Fragment() {
    private val TAG = "HomeFragment"
    private var _binding : FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val quizViewModel: QuizViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        setupData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupView()
        setupAction()


    }

    private fun setupData() {
        quizViewModel.getSessionData().observe(viewLifecycleOwner) {
            Log.d(TAG, "setupData: GET PROFILE")
            quizViewModel.getProfile(it.token)
        }
    }

    private fun setupAction() {
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.info-> {
                    showDialogInformation()
                    true
                }
                else -> false
            }
        }

    }

    private fun setupView() {
        val recyclerView : RecyclerView = binding.rvQuiz
        val items = quizList
        val adapter = QuizAdapter(items)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager


    }


    private fun showDialogInformation(){
        val dialogView = DialogQuizInfoBinding.inflate(layoutInflater)
        val okButton = dialogView.okButton

        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView.root)

        val dialog = builder.create()
        okButton.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }



}