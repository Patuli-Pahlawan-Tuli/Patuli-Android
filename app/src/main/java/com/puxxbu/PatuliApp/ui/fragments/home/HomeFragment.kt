package com.puxxbu.PatuliApp.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.itemLesson
import com.puxxbu.PatuliApp.databinding.FragmentHomeBinding
import com.puxxbu.PatuliApp.ui.OnBoardingActivity
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonAdapter
import com.puxxbu.PatuliApp.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupData()
        setupView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupAction()
    }

    private fun setupData() {
        homeViewModel.getSessionData().observe(viewLifecycleOwner) {
            Log.d(TAG, "setupData: GET PROFILE")
            homeViewModel.getProfile(it.token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupAction() {

    }

    override fun onResume() {
        super.onResume()
        homeViewModel.profileData.observe(viewLifecycleOwner) {
            if (it.data != null ) {
                binding.apply {
                    tvQuizProgress.text = getString(R.string.quiz_progress, it.data.completedQuiz.toString(), "3")
                    progressBar.progress = it.data.completedQuiz
                    Log.d(TAG, "setupView homefragment: ${it.data.completedQuiz}")
                }
            }
        }
    }

    private fun setupView() {
        showLoading()
        homeViewModel.profileData.observe(viewLifecycleOwner) {
            if (it.data != null ) {
                binding.apply {
                    var name = it.data.name.split(" ")
                    tvWelcome.text = getString(R.string.welcome_name, name[0])
                    Glide.with(requireContext())
                        .load(it.data.imageUrl)
                        .into(ivProfilePicture)
                    tvQuizProgress.text = getString(R.string.quiz_progress, it.data.completedQuiz.toString(), "3")
                    progressBar.progress = it.data.completedQuiz
                }
            }else{

            }
        }




        val recyclerView : RecyclerView = binding.rvLessons

        val items = itemLesson
        val adapter = LessonAdapter(items)

        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
    }


    private fun showLoading() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.apply {
                    progressBarProfile.visibility = View.VISIBLE
                    tvWelcome.visibility = View.GONE
                    tvQuizProgress.visibility = View.GONE
                }
            } else {
                binding.progressBarProfile.visibility = View.GONE
                binding.apply {
                    tvWelcome.visibility = View.VISIBLE
                    tvQuizProgress.visibility = View.VISIBLE
                }
            }
        }
    }
}