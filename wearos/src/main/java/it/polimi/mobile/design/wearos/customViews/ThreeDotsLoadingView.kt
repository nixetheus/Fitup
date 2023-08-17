package it.polimi.mobile.design.wearos.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ThreeDotsLoadingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val dotRadius = 4f
    private val dotSpacing = 12.5f
    private val dotColor = Color.WHITE

    private val dotPaint = Paint().apply {
        color = dotColor
        style = Paint.Style.FILL
    }

    private var direction = 1
    private var currentDot = 0
    private var animationRunning = false

    init {
        startAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        for (i in 0 until 3) {
            val dotX = centerX - dotSpacing + i * dotSpacing
            val alpha = if (i == currentDot) 255 else 125
            val radius = if (i == currentDot) {dotRadius * 1.5f} else dotRadius
            dotPaint.alpha = alpha
            canvas.drawCircle(dotX, centerY, radius, dotPaint)
        }

        if (animationRunning) {
            currentDot = (currentDot + 1) % 3
            postInvalidateDelayed(500)
        }
    }

    private fun startAnimation() {
        animationRunning = true
        currentDot = 0
        direction = 1
        postInvalidate()
    }

    fun stopAnimation() {
        animationRunning = false
        currentDot = 0
        direction = 1
        invalidate()
    }
}
