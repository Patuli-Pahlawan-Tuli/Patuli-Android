package com.puxxbu.PatuliApp.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.puxxbu.PatuliApp.R
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.*
import kotlin.math.max

class OverlayViewFront(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<Detection> = LinkedList<Detection>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = ContextCompat.getColor(context!!, R.color.md_theme_dark_onPrimaryContainer)
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.md_theme_dark_onPrimary)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val boundingBox = result.boundingBox


            val top = boundingBox.top * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor
            var left = boundingBox.left * scaleFactor
            var right = boundingBox.right * scaleFactor

            // Flip horizontal
            left = canvas.width.toFloat() - (left*0.75f)
            right = canvas.width.toFloat() - (right)
            Log.d("OverlayViewFront", "left: $left, width: ${canvas.width}, right: $right")

            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            // Draw text for detected object
            val drawableText =
                result.categories[0].label + " " +
                        String.format("%.2f", result.categories[0].score)

            // Hitung posisi teks
            textPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            val textX = left - textWidth
            val textY = top + bounds.height()

            canvas.drawRect(
                left,
                top,
                left - textWidth + Companion.BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + Companion.BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            canvas.drawText(drawableText, textX, textY, textPaint)
        }
    }

    fun setResults(
        detectionResults: MutableList<Detection>,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResults

        // PreviewView is in FILL_START mode. So we need to scale up the bounding box to match with
        // the size that the captured images will be displayed.
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}