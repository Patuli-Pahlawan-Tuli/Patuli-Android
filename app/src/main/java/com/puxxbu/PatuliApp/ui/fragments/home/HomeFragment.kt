package com.puxxbu.PatuliApp.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.itemLesson
import com.puxxbu.PatuliApp.data.model.quizList
import com.puxxbu.PatuliApp.databinding.FragmentHomeBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.adapter.LessonAdapter
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
                    if (it.data.completedQuiz == 3) {
                        icStar.setImageResource(R.drawable.ic_baseline_star_24)
                    } else {
                        icStar.setImageResource(R.drawable.ic_baseline_star_outline_24)
                    }
                    progressBar.progress = it.data.completedQuiz
                    Log.d(TAG, "setupView homefragment: ${it.data.completedQuiz}")
                }
                quizList.forEach { item ->
                    if (it.data.completedQuiz < item.completed_quiz_req) {
                        item.is_enabled = false
                        Log.d("SplashActivity", "setupView: ${item.quiz_title} ${item.is_enabled}")
                    }
                    item.subQuiz.forEachIndexed { index, subQuiz ->
                        if (it.data.completedSubQuiz < subQuiz.unlock_requirement) {
                            item.subQuiz[index].is_enabled = false
                        }
                    }
                    Log.d("SplashActivity", "setupView: ${item.completed_quiz_req} ${it.data.completedQuiz}")
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
                    tvLevel.text = getString(R.string.level_user, it.data.accountLevel.toString())
                    levelBar.progress = it.data.accountExp % 100
                    tvQuizProgress.text = getString(R.string.quiz_progress, it.data.completedQuiz.toString(), "3")
                    progressBar.progress = it.data.completedQuiz
                }
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