package it.polimi.mobile.design.custom_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import it.polimi.mobile.design.R
import it.polimi.mobile.design.helpers.Constant

class MinimizedExerciseView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val radius = Constant.EXERCISE_VIEW_R
    private val insideColor = TypedValue()

    init {
        setOnClickListener {
            setFinishedColor()
            invalidate()
        }
        background = ColorDrawable(Color.TRANSPARENT)
        context.theme.resolveAttribute (androidx.appcompat.R.attr.colorPrimary, insideColor, true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle3D(canvas)
    }

    private fun drawCircle3D(canvas: Canvas) {

        val accentColor = TypedValue()
        context.theme.resolveAttribute (androidx.appcompat.R.attr.colorAccent, accentColor, true)

        val colorOnPrimary = TypedValue()
        context.theme.resolveAttribute (R.attr.colorOnPrimary, colorOnPrimary, true)

        val colorPrimary = TypedValue()
        context.theme.resolveAttribute (androidx.appcompat.R.attr.colorPrimary, colorPrimary, true)


        val pointPaint = Paint()
        pointPaint.strokeWidth = radius / 20f
        pointPaint.strokeCap = Paint.Cap.ROUND

        pointPaint.color = insideColor.data
        pointPaint.style = Paint.Style.FILL
        canvas.drawOval(RectF(pointPaint.strokeWidth, pointPaint.strokeWidth,
            radius.toFloat() - pointPaint.strokeWidth, radius / 2f - pointPaint.strokeWidth),
            pointPaint)

        pointPaint.color = colorOnPrimary.data
        pointPaint.style = Paint.Style.STROKE
        canvas.drawOval(RectF(pointPaint.strokeWidth, pointPaint.strokeWidth,
            radius.toFloat() - pointPaint.strokeWidth, radius / 2f - pointPaint.strokeWidth),
            pointPaint)
    }
    private fun setFinishedColor() {
        context.theme.resolveAttribute (androidx.appcompat.R.attr.colorAccent, insideColor, true)
    }
}