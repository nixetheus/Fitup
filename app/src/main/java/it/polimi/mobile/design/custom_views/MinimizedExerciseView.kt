package it.polimi.mobile.design.custom_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import it.polimi.mobile.design.helpers.Constant

class MinimizedExerciseView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val radius = Constant.EXERCISE_VIEW_R

    init {
        setOnClickListener {
            Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show()
        }
        background = ColorDrawable(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle3D(canvas)
    }

    private fun drawCircle3D(canvas: Canvas) {

        val pointPaint = Paint()
        pointPaint.strokeWidth = radius / 30f
        pointPaint.strokeCap = Paint.Cap.ROUND

        pointPaint.color = Color.BLACK
        pointPaint.style = Paint.Style.FILL
        canvas.drawOval(RectF(0f, radius / 4f, radius.toFloat(),
            radius * (3f / 4f)), pointPaint)

        pointPaint.color = Color.WHITE
        pointPaint.style = Paint.Style.STROKE
        canvas.drawOval(RectF(0f, radius / 4f, radius.toFloat(),
            radius * (3f / 4f)), pointPaint)
    }
}