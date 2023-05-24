package com.puxxbu.PatuliApp.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.databinding.FragmentProfileBinding
import com.puxxbu.PatuliApp.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {


    private val TAG = "ProfileFragment"
    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val fragmentProfileBinding get() = _fragmentProfileBinding!!

    private val profileViewModel: ProfileViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)


        setupData()
        setupView()
        return fragmentProfileBinding.root
    }

    private fun setupData() {
        profileViewModel.getSessionData().observe(viewLifecycleOwner) {
            if (it.isLogin) {
                profileViewModel.getProfile(it.token)
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentProfileBinding = null
    }

    fun setupView() {


        profileViewModel.profileData.observe(viewLifecycleOwner) {
            fragmentProfileBinding.apply {
                tietName.setText(it.data.name)
                tietEmail.setText(it.data.email)
                Glide.with(requireContext()).load(it.data.imageUrl).into(ivProfilePicture)
            }
        }

        fragmentProfileBinding.apply {
            tietPassword.setOnClickListener {
                val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
                startActivity(intent)
            }
        }




    }


}