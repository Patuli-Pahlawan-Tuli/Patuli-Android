package com.puxxbu.PatuliApp.ui.fragments.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.databinding.FragmentProfileBinding
import com.puxxbu.PatuliApp.ui.MainActivity
import com.puxxbu.PatuliApp.ui.OnboardingActivity
import com.puxxbu.PatuliApp.ui.SplashActivity
import com.puxxbu.PatuliApp.ui.login.LoginActivity
import com.puxxbu.PatuliApp.utils.reduceFileImage
import com.puxxbu.PatuliApp.utils.uriToFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"
    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val binding get() = _fragmentProfileBinding!!

    private var getFile: File? = null
    private val profileViewModel: ProfileViewModel by viewModel()


    private lateinit var launcherIntentGallery: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcherIntentGallery = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = uriToFile(selectedImg, requireContext())

                getFile = myFile
                binding.ivProfilePicture.setImageURI(selectedImg)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)


        Log.d(TAG, "onCreateView: profile fragment")

        setupView()
        setupAction()
        setupData()
        return binding.root
    }

    private fun setupAction() {
        binding.apply {
            ivProfilePicture.setOnClickListener {
                startGallery()
            }
            tvEditPhoto.setOnClickListener {
                startGallery()
            }

            btnLogout.setOnClickListener {
                profileViewModel.logout()
                lifecycleScope.launch {
                    delay(500) // menunggu 500ms
                    parentFragmentManager.popBackStack()
                }
                activity?.finishAffinity()
                activity?.finish()
                val intentLogout = Intent(requireContext(), MainActivity::class.java)
                intentLogout.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(this@ProfileFragment).commit()
                startActivity(intentLogout)
                Log.d(TAG, "setupAction: logout")
            }
        }


    }

    private fun setupData() {
        profileViewModel.getSessionData().observe(viewLifecycleOwner) {
            Log.d(TAG, "setupData: GET PROFILE")
            profileViewModel.getProfile(it.token)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentProfileBinding = null
        Log.d(TAG, "onDestroyView: profile fragment")
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    fun setupView() {



        showLoading()
        profileViewModel.profileData.observe(viewLifecycleOwner) {
            binding.apply {
                tietName.setText(it.data.name)
                tietEmail.setText(it.data.email)
                Glide.with(requireContext()).load(it.data.imageUrl).into(ivProfilePicture)
            }
        }

        binding.apply {
            tietPassword.setOnClickListener {
                val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
                startActivity(intent)
                Log.d(TAG, "setupView: change password")
            }
        }

    }

    private fun editProfilePicture(token: String, file: MultipartBody.Part) {
        profileViewModel.editProfilePicture(token, file)
        profileViewModel.editProfilePicResponse.observe(viewLifecycleOwner) {
            if (!it.error) {
                profileViewModel.profileErrorResponse.observe(viewLifecycleOwner) { message ->
                    message.getContentIfNotHandled()?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)

        val viewTreeObserver = binding.ivProfilePicture.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: foto ganti")
                Log.d(TAG, "onGlobalLayout: $getFile ")
                if (getFile != null) {
                    Log.d(TAG, "onGlobalLayout: foto upload")
                    val file = reduceFileImage(getFile as File)
                    val requestImageFile = file.asRequestBody(
                        "image/jpeg".toMediaTypeOrNull()
                    )
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        requestImageFile
                    )
                    profileViewModel.getSessionData().observe(viewLifecycleOwner) {

                        editProfilePicture(it.token, imageMultipart)

                    }
                }
                binding.ivProfilePicture.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun showLoading() {
        profileViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }


}