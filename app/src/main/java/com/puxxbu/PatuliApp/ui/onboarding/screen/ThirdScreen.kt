package com.puxxbu.PatuliApp.ui.onboarding.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.FragmentSecondScreenBinding
import com.puxxbu.PatuliApp.databinding.FragmentThirdScreenBinding
import com.puxxbu.PatuliApp.ui.OnBoardingActivity
import com.puxxbu.PatuliApp.ui.main.MainActivity

class ThirdScreen : Fragment() {
    private var _binding: FragmentThirdScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var i: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentThirdScreenBinding.inflate(inflater, container, false)

        binding.finish.setOnClickListener {
            i = Intent(requireContext(), OnBoardingActivity::class.java)
            startActivity(i)
            onBoardingFinished()
            activity?.finish()
        }

        return binding.root
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

}