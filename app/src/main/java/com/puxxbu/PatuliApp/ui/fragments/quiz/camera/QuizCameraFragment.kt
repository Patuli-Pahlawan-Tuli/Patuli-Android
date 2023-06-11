package com.puxxbu.PatuliApp.ui.fragments.quiz.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.data.model.quizList
import com.puxxbu.PatuliApp.databinding.DialogQuizDoneBinding
import com.puxxbu.PatuliApp.databinding.DialogSuccessBinding
import com.puxxbu.PatuliApp.databinding.FragmentQuizCameraBinding
import com.puxxbu.PatuliApp.ui.fragments.quiz.QuizActivity
import com.puxxbu.PatuliApp.ui.fragments.quiz.QuizViewModel
import com.puxxbu.PatuliApp.utils.ObjectDetectorHelper
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QuizCameraFragment : Fragment(), ObjectDetectorHelper.DetectorListener {

    private val TAG = "CameraFragment"

    private var _binding: FragmentQuizCameraBinding? = null
    private val quizViewModel : QuizViewModel by viewModel()

    private val binding
        get() = _binding!!



    private val _resultResponse = MutableLiveData<String>()
    val resultResponse: MutableLiveData<String> = _resultResponse

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var sharedPreferences: SharedPreferences
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var job: Job? = null
    private val logDelay = 1500L // Delay in milliseconds
    private var dataResult : String = ""
    private var answerKey : String = ""
    private var quizDifficulty : String = ""
    private var quizNumber : Int = 0
    private var languageType : String = ""
    private var maxNumber = 5
    private var subQuiz = 0

    private var isFragmentActive = false

    val handler = Handler(Looper.getMainLooper())

    private var isFrontCamera = false

    private var isAnswered = false



    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    override fun onResume() {

        super.onResume()

        if (job?.isCancelled == true) {
            startLogging()
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }

    override fun onDestroyView() {

        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        objectDetectorHelper.clearObjectDetector()
        _binding = null
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
        job?.cancel()
        isFragmentActive = false

        // Shut down our background executor

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizCameraBinding.inflate(inflater, container, false)
//









        isFragmentActive = true

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        objectDetectorHelper = ObjectDetectorHelper(
            context = requireContext(),
            objectDetectorListener = this
        )
        sharedPreferences = requireContext().getSharedPreferences("quiz_data_preferences", Context.MODE_PRIVATE)

        Log.d("QuizCameraFragment", "onViewCreated: DIBUATCAMERA}")
        // Mengambil nilai-nilai dari SharedPreferences
//        quizDifficulty =
//            sharedPreferences.getString(QuizCameraFragment.EXTRA_QUIZ_DIFFICULTY, "").toString()
//        quizNumber = sharedPreferences.getInt(QuizCameraFragment.EXTRA_NUMBER, 0)
//        languageType = sharedPreferences.getString(QuizCameraFragment.EXTRA_TYPE, "").toString()
//        answerKey= sharedPreferences.getString(QuizCameraFragment.EXTRA_ANSWER, "").toString()



        Log.d("QuizCameraFragment", "onViewCreated: $answerKey")
        quizViewModel.quizData.observe(viewLifecycleOwner) {
            quizDifficulty = it.data[0].quizDifficulty
            quizNumber = it.data[0].quizNumber
            languageType = it.data[0].languageType
            answerKey = it.data[0].answer
            subQuiz = it.data[0].subQuiz
            Log.d("QuizCameraFragment", "onCreateView: $answerKey")

            when(languageType){
                "abjad" -> {
                    objectDetectorHelper.currentModel = 0
                    updateControlsUi()
                }
                "angka" -> {
                    objectDetectorHelper.currentModel = 1
                    updateControlsUi()
                }
                "kata" -> {
                    objectDetectorHelper.currentModel = 2
                    updateControlsUi()
                }
            }


        }

        startLogging()





        // Attach listeners to UI control widgets
//        initBottomSheetControls()
    }


    private fun updateControlsUi() {
        // Needs to be cleared instead of reinitialized because the GPU
        // delegate needs to be initialized on the thread using it when applicable
        objectDetectorHelper.clearObjectDetector()
        binding.overlay.clear()
    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera(cameraPosition : Int) {
        if (isFragmentActive && lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) ) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener(
                {
                    // CameraProvider
                    cameraProvider = cameraProviderFuture.get()

                    // Build and bind the camera use cases
                    bindCameraUseCases(cameraPosition)
                },
                ContextCompat.getMainExecutor(requireContext())
            )
        }
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases(cameraPosition : Int) {

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            when(cameraPosition){
                0 -> CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                1 -> CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
                else -> CameraSelector.DEFAULT_BACK_CAMERA
            }
        // Preview. Only using the 4:3 ratio because this is the closest to our models


        preview =
            Preview.Builder()
                .setTargetResolution(Size(1080, 1920))
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()


        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetResolution(Size(1080, 1920))
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBuffer.isInitialized) {
                            // The image rotation and RGB image buffer are initialized only once
                            // the analyzer has started running
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }

                        detectObjects(image)
                    }
                }

        binding.viewFinder.scaleX = 1f

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = binding.viewFinder.display.rotation
    }

    // Update UI after objects have been detected. Extracts original image height/width
    // to scale and place bounding boxes properly through OverlayView
    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {

        if (isFragmentActive && lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) && activity != null) {
            handler.post{
                activity?.runOnUiThread {
                    binding.overlay.setResults(
                        results ?: LinkedList<Detection>(),
                        imageHeight,
                        imageWidth
                    )

                    _resultResponse.value = "No Result"
                    if (results != null) {
                        for (result in results) {
                            _resultResponse.value = result.categories[0].label
                            Log.d(TAG, "onResults: Results: ${result.categories[0].label} ")
                        }
                    }
                    // Force a redraw
                    binding.overlay.invalidate()
                }
            }

        }


    }

    fun startLogging() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                Log.d(TAG, "TES ${resultResponse.value}")
                Log.d(TAG, "TES ${resultResponse.value?.lowercase()} $answerKey")
                if (resultResponse.value != "No Result" && resultResponse.value != null) {
                   if (checkAnswer(resultResponse.value!!.lowercase(), answerKey.lowercase()) && !isAnswered){
                       if (quizNumber == maxNumber){
                           showDialogQuizDone()
                       }else{
                           showDialog("Jawaban Benar")
                       }
                       isAnswered = true
                   }
                }

                delay(logDelay)
            }
        }
    }


    override fun onError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInitialized() {
        if (isFragmentActive && lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) ) {
            objectDetectorHelper.setupObjectDetector()
            // Initialize our background executor
            cameraExecutor = Executors.newSingleThreadExecutor()



            // Wait for the views to be properly laid out
            binding.viewFinder.post {
                // Set up the camera and its use cases
                setUpCamera(FRONT_CAMERA)
            }

            binding.progressCircular.visibility = View.GONE
        }
    }

    private fun  checkAnswer(answer : String, keyAnswer : String) : Boolean{
        return answer == keyAnswer
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


        dialog.setOnDismissListener {
            val intent = Intent(context, QuizActivity::class.java)
            intent.putExtra(QuizActivity.EXTRA_QUIZ_DIFFICULTY, quizDifficulty)
            Log.d(TAG, "showDialog: $quizNumber")
            var nextNumber = quizNumber + 1
            Log.d(TAG, "showDialog: $nextNumber")

            intent.putExtra(QuizActivity.EXTRA_NUMBER, nextNumber)
            intent.putExtra(QuizActivity.EXTRA_LEVEL, subQuiz)
            job?.cancel()
            startActivity(intent)
            activity?.finish()
            dialog.dismiss()
        }
        tvTitle.text = message


        dialog.show()

    }

    private fun showDialogQuizDone(){
        val dialogView = DialogQuizDoneBinding.inflate(layoutInflater)
        val okButton = dialogView.okButton
        val tvTitle = dialogView.dialogTitle

        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView.root)

        buttonStateForQuiz()

        quizViewModel.getSessionData().observe(viewLifecycleOwner) {
            quizViewModel.updateSubQuizProgress(it.token, subQuiz)
            quizViewModel.updateUserExperience(it.token, 50)
        }

        val dialog = builder.create()
        okButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            job?.cancel()
            activity?.finish()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun buttonStateForQuiz(){
        when (quizDifficulty.lowercase()) {
            "intermediate" -> subQuiz += 2
            "expert" -> subQuiz += 4
        }

        Log.d(TAG, "showDialog: $subQuiz")
        when(subQuiz){
            2 -> {
                quizList[1].is_enabled = true
            }
            4 -> {
                quizList[2].is_enabled = true
            }
        }

        when(subQuiz){
            1 -> quizList[0].subQuiz[1].is_enabled = true
            2 -> quizList[1].subQuiz[0].is_enabled = true
            3 -> quizList[1].subQuiz[1].is_enabled = true
            4 -> quizList[2].subQuiz[0].is_enabled = true
            5 -> quizList[2].subQuiz[1].is_enabled = true
        }
    }



    companion object{
        private const val BACK_CAMERA = 0
        private const val FRONT_CAMERA = 1
        const val EXTRA_QUIZ_DIFFICULTY = "extra_quiz_difficulty"
        const val EXTRA_NUMBER = "extra_number"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_ANSWER = "extra_answer"
        const val EXTRA_SUB_QUIZ = "extra_sub_quiz"
    }
}
