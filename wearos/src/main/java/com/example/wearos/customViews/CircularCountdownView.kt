package com.example.wearos.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.lang.Float

class CircularCountdownView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private var progress = 100

    val colorAccent = TypedValue()
    val colorPrimary = TypedValue()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get colors
        context.theme.resolveAttribute (androidx.appcompat.R.attr.colorPrimary, colorPrimary, true)
        context.theme.resolveAttribute (androidx.appcompat.R.attr.colorAccent, colorAccent, true)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Float.min(centerX, centerY) / 1.4f

        // Draw circles
        paint.strokeWidth = 50f
        paint.style = Paint.Style.STROKE

        paint.color = colorPrimary.data
        canvas.drawCircle(centerX, centerY, radius, paint)

        paint.color = colorAccent.data
        val startAngle = -90f
        val sweepAngle = (progress / 100f) * 360f
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, startAngle, sweepAngle, true, paint)

        // Fill center
        paint.style = Paint.Style.FILL
        paint.color = colorPrimary.data
        canvas.drawCircle(centerX, centerY, radius - paint.strokeWidth/2f, paint)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }
}