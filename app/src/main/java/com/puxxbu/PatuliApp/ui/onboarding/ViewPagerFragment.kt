package com.puxxbu.PatuliApp.ui.onboarding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.FragmentProfileBinding
import com.puxxbu.PatuliApp.databinding.FragmentViewPagerBinding
import com.puxxbu.PatuliApp.ui.onboarding.screen.FirstScreen
import com.puxxbu.PatuliApp.ui.onboarding.screen.SecondScreen
import com.puxxbu.PatuliApp.ui.onboarding.screen.ThirdScreen
import com.puxxbu.PatuliApp.ui.onboarding.viewpager.ViewPagerAdapter

class ViewPagerFragment : Fragment() {
    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        Log.d("ViewPagerFragment", "onCreateView: ")

        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPagerOB.adapter = adapter

        return binding.root
    }
}