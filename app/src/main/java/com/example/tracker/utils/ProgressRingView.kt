package com.example.tracker.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.min

class ProgressRingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var maxProgress = 100f
    private val strokeWidth = 30f
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = strokeWidth
        backgroundPaint.color = Color.parseColor("#E0E0E0")

        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = strokeWidth
        progressPaint.color = Color.parseColor("#4CAF50")
        progressPaint.strokeCap = Paint.Cap.ROUND

        textPaint.color = Color.BLACK
        textPaint.textSize = 100f
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) / 2f - strokeWidth

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Рисуем серый фон
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Рисуем цветной прогресс
        val sweepAngle = 360f * (progress / maxProgress)
        canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint)

        // Рисуем текст внутри
        val percentText = "${(progress / maxProgress * 100).toInt()}%"
        canvas.drawText(percentText, centerX, centerY + 35f, textPaint)
    }

    fun setProgressWithAnimation(targetProgress: Float, max: Float) {
        this.maxProgress = max
        val animator = ValueAnimator.ofFloat(0f, targetProgress)
        animator.duration = 1500 // 1.5 секунды
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            progress = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }
}