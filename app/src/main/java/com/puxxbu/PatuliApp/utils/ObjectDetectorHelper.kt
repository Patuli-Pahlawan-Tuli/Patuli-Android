package com.puxxbu.PatuliApp.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.puxxbu.PatuliApp.PatuliApp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector
import java.io.File

class ObjectDetectorHelper(

    val context: Context,
    val objectDetectorListener: DetectorListener,
    var currentModel: Int = 0
) {
    var threshold: Float = 0.7f
    var numThreads: Int = 2
    var maxResults: Int = 1

    private val _resultResponse = MutableLiveData<MutableList<Detection>?>()
    val resultResponse: MutableLiveData<MutableList<Detection>?> = _resultResponse

    private val TAG = "ObjectDetectionHelper"

    // For this example this needs to be a var so it can be reset on changes. If the ObjectDetector
    // will not change, a lazy val would be preferable.
    private var objectDetector: ObjectDetector? = null
    private var gpuSupported = false

    init {

        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable: Boolean ->
            val optionsBuilder =
                TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            objectDetectorListener.onInitialized()
        }.addOnFailureListener{
            objectDetectorListener.onError("TfLiteVision failed to initialize: "
                    + it.message)
        }
    }

    fun clearObjectDetector() {
        objectDetector = null
    }

    // Initialize the object detector using current settings on the
    // thread that is using it. CPU and NNAPI delegates can be used with detectors
    // that are created on the main thread and used on a background thread, but
    // the GPU delegate needs to be used on the thread that initialized the detector
    fun setupObjectDetector() {

        val sharedPreferences = PatuliApp.context.getSharedPreferences("model_type", Context.MODE_PRIVATE)
        val modelType = sharedPreferences.getInt("model_type", 0)


        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG, "setupObjectDetector: TfLiteVision is not initialized yet")
            return
        }

        when(modelType){
            1 -> threshold = 0.7f
            else -> threshold = 0.75f
        }

        // Create the base options for the detector using specifies max results and score threshold
        val optionsBuilder =
            ObjectDetector.ObjectDetectorOptions.builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)

        // Set general detection options, including number of used threads
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        // Use the specified hardware for running the model. Default to CPU

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())





        Log.d("model_type", modelType.toString())
        val modelName = when (modelType) {
            1 -> when (currentModel) {
                MODEL_ABJAD -> "abjad_lite.tflite"
                MODEL_ANGKA -> "angka_lite.tflite"
                MODEL_KATA -> "kata_lite.tflite"
                else -> "abjad_lite.tflite"
            }
            else -> when (currentModel) {
                MODEL_ABJAD -> "abjad.tflite"
                MODEL_ANGKA -> "angka.tflite"
                MODEL_KATA -> "kata.tflite"
                else -> "abjad.tflite"
            }
        }

        Log.d("model_name", modelName)

        val file = File(context.getExternalFilesDir("models"), modelName)

        try {
            objectDetector =
            ObjectDetector.createFromFileAndOptions(file,optionsBuilder.build())
        } catch (e: Exception) {
            objectDetectorListener.onError(
                "Object detector failed to initialize. See error logs for details"
            )
            Log.e(TAG, "TFLite failed to load model with error: " + e.message)
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG, "detect: TfLiteVision is not initialized yet")
            return
        }

        if (objectDetector == null) {
            setupObjectDetector()
        }

        var inferenceTime = SystemClock.uptimeMillis()

        val imageProcessor = ImageProcessor.Builder().add(Rot90Op(-imageRotation / 90)).build()
        // Preprocess the image and convert it into a TensorImage for detection.
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        objectDetectorListener.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width)

        _resultResponse.postValue(results)
    }



    interface DetectorListener {
        fun onInitialized()
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
        const val MODEL_ABJAD = 0
        const val MODEL_ANGKA = 1
        const val MODEL_KATA = 2

    }
}