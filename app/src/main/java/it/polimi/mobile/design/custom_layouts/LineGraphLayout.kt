package it.polimi.mobile.design.custom_layouts

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.content.res.Resources
import androidx.constraintlayout.widget.ConstraintLayout
import it.polimi.mobile.design.custom_objects.DataPoint

class LineGraphLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    lateinit var dataPoints: List<DataPoint>

    init {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private fun setDefaultParams() {
        // context.theme.resolveAttribute(R.attr.colorOnPrimary, colorOnPrimary, true)
    }

    private fun drawDataPoints() {
        if (dataPoints.isNotEmpty()) {

        }
    }

    private fun drawLines(canvas: Canvas) {

    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}