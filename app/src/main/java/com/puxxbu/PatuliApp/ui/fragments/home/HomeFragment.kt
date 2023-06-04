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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        homeViewModel.getSessionData().observe(viewLifecycleOwner) {
            Log.d(TAG, "setupData: GET PROFILE")
            homeViewModel.getProfile(it.token)
        }
    }

    private fun setupAction() {

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
                    tvQuizProgress.text = getString(R.string.quiz_progress, "0", "3")
                }
            }else{

//                homeViewModel.logout()
//
//                Toast.makeText(requireContext(), "Session Expired", Toast.LENGTH_LONG).show()
//
//                val intentLogout = Intent(requireContext(), LoginActivity::class.java)
//                intentLogout.flags =
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                requireActivity().supportFragmentManager.beginTransaction()
//                    .remove(this@HomeFragment).commit()
//                startActivity(intentLogout)

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
            binding.progressBarProfile.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}