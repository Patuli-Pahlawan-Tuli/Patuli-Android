package com.puxxbu.PatuliApp.ui.fragments.camera

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import com.puxxbu.PatuliApp.R
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.tabs.TabLayout
import com.puxxbu.PatuliApp.databinding.FragmentCameraBinding
import com.puxxbu.PatuliApp.utils.ObjectDetectorHelper
import kotlinx.coroutines.*
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), ObjectDetectorHelper.DetectorListener {

    private val TAG = "CameraFragment"

    private var _binding: FragmentCameraBinding? = null

    private val binding
        get() = _binding!!

    private val _resultResponse = MutableLiveData<String>()
    val resultResponse: MutableLiveData<String> = _resultResponse

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var job: Job? = null
    private val logDelay = 1000L // Delay in milliseconds
    private var dataResult : String = ""

    private var isFragmentActive = false

    val handler = Handler(Looper.getMainLooper())

    private var isFrontCamera = false


    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    override fun onResume() {
        val permissionsFragment = PermissionsFragment()
        val fragmentManager = parentFragmentManager
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
//        if (!PermissionsFragment.hasPermissions(requireContext())) {
//            fragmentManager.beginTransaction()
//                .replace(
//                    R.id.fragment_container,
//                    permissionsFragment,
//                    PermissionsFragment::class.java.simpleName
//                )
//                .commit()
//        }

        if (job?.isCancelled == true) {
            startLogging(binding.tvResult)
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
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        val tvHasil = binding.tvResult
        tvHasil.setText("Hasil")
        startLogging(tvHasil)
        binding.ivRefresh.setOnClickListener {
            dataResult = ""
        }

        binding.topAppBar.setNavigationOnClickListener {
            activity?.finish()
        }

        binding.fabSwitchCamera.setOnClickListener {
            isFrontCamera = !isFrontCamera
            setUpCamera(if (isFrontCamera) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let{
                    when(it.position){
                        0 -> {
                            objectDetectorHelper.currentModel = 0
                            updateControlsUi()
                        }
                        1 -> {
                            objectDetectorHelper.currentModel = 1
                            updateControlsUi()
                        }
                        2 -> {
                            objectDetectorHelper.currentModel = 2
                            updateControlsUi()
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

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

        // Attach listeners to UI control widgets
//        initBottomSheetControls()
    }

    private fun showMenu(v : View, @MenuRes menuRes: Int){
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.option_1 -> {
                    objectDetectorHelper.currentModel = 0
                    updateControlsUi()
                    true
                }

                R.id.option_2 -> {
                    objectDetectorHelper.currentModel = 1
                    updateControlsUi()
                    true
                }

                else -> false
            }
        }
        popup.show()
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
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()


        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
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

    fun startLogging(textView: TextView) {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                Log.d(TAG, "TES ${resultResponse.value}")
                if (resultResponse.value != "No Result" && resultResponse.value != null) {
                    dataResult += "${resultResponse.value} "
                }
                textView.text = dataResult
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

    companion object{
        private const val BACK_CAMERA = 0
        private const val FRONT_CAMERA = 1
    }
}
