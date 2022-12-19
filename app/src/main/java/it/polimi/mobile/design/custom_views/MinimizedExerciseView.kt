package it.polimi.mobile.design.custom_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import it.polimi.mobile.design.helpers.Constant
import kotlin.math.sqrt

class MinimizedExerciseView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val dataPointSize = Constant.DATA_BUTTON_SIZE

    init {
        setOnClickListener {
            Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show()
        }
        background = ColorDrawable(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas) {

        val pointPaint = Paint()
        pointPaint.strokeWidth = dataPointSize.toFloat() / 10
        pointPaint.strokeCap = Paint.Cap.ROUND

        pointPaint.color = Color.BLACK
        pointPaint.style = Paint.Style.FILL
        canvas.drawCircle((dataPointSize / 2f), (dataPointSize / 2f),
            pointPaint.strokeWidth * 2.75f, pointPaint)

        pointPaint.color = Color.WHITE
        pointPaint.style = Paint.Style.STROKE
        canvas.drawCircle((dataPointSize / 2f), (dataPointSize / 2f),
            pointPaint.strokeWidth * 2.75f, pointPaint)
    }
}