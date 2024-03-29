package it.polimi.mobile.design.custom_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import it.polimi.mobile.design.R
import it.polimi.mobile.design.helpers.Constant
import kotlin.math.sqrt

class LineGraphDataView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var dataValue = 0f
    private val dataPointSize = Constant.DATA_BUTTON_SIZE

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas) {

        // Get color
        val colorOnPrimary = TypedValue()
        context.theme.resolveAttribute (R.attr.colorOnPrimary, colorOnPrimary, true)

        // Get color
        val accentColor = TypedValue()
        context.theme.resolveAttribute (androidx.appcompat.R.attr.colorAccent, accentColor, true)

        val pointPaint = Paint()
        pointPaint.strokeWidth = dataPointSize.toFloat() / 12.5f
        pointPaint.strokeCap = Paint.Cap.ROUND

        pointPaint.color = colorOnPrimary.data
        pointPaint.style = Paint.Style.STROKE
        canvas.drawCircle((dataPointSize / 2f), (dataPointSize / 2f),
            pointPaint.strokeWidth * sqrt(2f), pointPaint)

        pointPaint.color = accentColor.data
        pointPaint.style = Paint.Style.FILL
        canvas.drawCircle((dataPointSize / 2f), (dataPointSize / 2f),
            pointPaint.strokeWidth * sqrt(2f), pointPaint)
    }
}