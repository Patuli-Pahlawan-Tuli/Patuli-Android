package com.puxxbu.PatuliApp.ui.fragments.quiz

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.PatuliApp
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.quizList
import com.puxxbu.PatuliApp.databinding.DialogQuizInfoBinding
import com.puxxbu.PatuliApp.databinding.DialogQuizIntroduceBinding
import com.puxxbu.PatuliApp.databinding.FragmentQuizBinding
import com.puxxbu.PatuliApp.ui.fragments.quiz.adapter.QuizAdapter
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
        val IS_FIRST_TIME = "IsFirstTime"

        val sharedPreferences = PatuliApp.context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(IS_FIRST_TIME, true)

        if (isFirstTime) {
            showDialogQuizIntroduce()
            sharedPreferences.edit().putBoolean(IS_FIRST_TIME, false).apply()
        }



        quizViewModel.getSessionData().observe(viewLifecycleOwner) {
            quizViewModel.getProfile(it.token)
        }

        quizViewModel.profileData.observe(viewLifecycleOwner) {

        }
    }

    private fun setupAction() {
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.info-> {
                    showDialogQuizIntroduce()
                    true
                }
                else -> false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        view?.invalidate()
        showRecycleView()
        Log.d(TAG, "onResume: refresh")
    }


    private fun setupView() {
       showRecycleView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun showRecycleView(){
        quizViewModel.profileData.observe(viewLifecycleOwner) {



            val recyclerView : RecyclerView = binding.rvQuiz
            val items = quizList
            val adapter = QuizAdapter(items)



            recyclerView.adapter = adapter
            recyclerView.invalidate()
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager
            adapter.notifyDataSetChanged()

        }
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

    private fun showDialogQuizIntroduce(){
        val dialogView = DialogQuizIntroduceBinding.inflate(layoutInflater)
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