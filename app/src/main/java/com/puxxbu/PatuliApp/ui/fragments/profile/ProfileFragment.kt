package com.puxxbu.PatuliApp.ui.fragments.profile

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.databinding.ActivityLoginBinding
import com.puxxbu.PatuliApp.databinding.DialogLoadingBinding
import com.puxxbu.PatuliApp.databinding.DialogSuccessBinding
import com.puxxbu.PatuliApp.databinding.FragmentProfileBinding
import com.puxxbu.PatuliApp.ui.OnBoardingActivity
import com.puxxbu.PatuliApp.utils.reduceFileImage
import com.puxxbu.PatuliApp.utils.uriToFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.math.ceil
import kotlin.math.max

class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"
    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val binding get() = _fragmentProfileBinding!!

    private var getFile: File? = null
    private var progressDialog: AlertDialog? = null
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
                Glide.with(requireContext())
                    .load(selectedImg)
                    .into(binding.ivProfilePicture)
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
                val intentLogout = Intent(requireContext(), OnBoardingActivity::class.java)
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
        showDialogLoading() // Menampilkan dialog progress/loading sebelum mengupload foto
        profileViewModel.editProfilePicture(token, file)
        profileViewModel.editPicResponse.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                Log.d(TAG, "editProfilePicture: $it")
                hideLoading() // Menyembunyikan dialog progress/loading setelah pengiriman foto selesai
                showDialog("Ubah Foto Profil")
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)

        val viewTreeObserver = binding.ivProfilePicture.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: foto ganti")
                Log.d(TAG, "onGlobalLayout: $getFile ")
                if (getFile != null) {
                    val file = getFile as File
                    if (file.length() > 2 * 1024 * 1024) {
                        Toast.makeText(requireContext(), "File terlalu besar (maksimal 2 MB)", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d(TAG, "onGlobalLayout: foto upload")
                        val reducedFile = reduceFileImage(file)
                        val requestImageFile = reducedFile.asRequestBody(
                            "image/jpeg".toMediaTypeOrNull()
                        )
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "file",
                            reducedFile.name,
                            requestImageFile
                        )
                        profileViewModel.getSessionData().observe(viewLifecycleOwner) {
                            editProfilePicture(it.token, imageMultipart)
                        }
                    }
                }
                binding.ivProfilePicture.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun showDialogLoading() {


        val dialogView = DialogLoadingBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setCancelable(false)
        builder.setView(dialogView.root)
        progressDialog = builder.create()

        progressDialog?.show()
    }

    private fun hideLoading() {
        // Menyembunyikan alert dialog
        progressDialog?.dismiss()
    }

    private fun reduceFileImage(file: File): File {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth
        val scaleFactor = calculateScaleFactor(imageWidth, imageHeight, 1920, 1080)

        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor
        val resizedBitmap = BitmapFactory.decodeFile(file.path, options)
        val resizedFile = File.createTempFile("resized_", ".jpg")
        resizedFile.outputStream().use {
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
        }
        return resizedFile
    }

    private fun calculateScaleFactor(imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int): Int {
        var scaleFactor = 1
        if (imageWidth > targetWidth || imageHeight > targetHeight) {
            val widthRatio = imageWidth.toFloat() / targetWidth.toFloat()
            val heightRatio = imageHeight.toFloat() / targetHeight.toFloat()
            scaleFactor = ceil(max(widthRatio, heightRatio)).toInt()
        }
        return scaleFactor
    }

    private fun showLoading() {
        profileViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showDialog(message : String){
        val dialogView = DialogSuccessBinding.inflate(layoutInflater)
        val okButton = dialogView.okButton
        val tvTitle = dialogView.dialogTitle

        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView.root)

        val dialog = builder.create()
        okButton.setOnClickListener {
            dialog.dismiss()
        }
        tvTitle.text = message

        dialog.show()
    }


}