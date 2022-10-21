package it.polimi.mobile.design.custom_layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import it.polimi.mobile.design.custom_objects.Exercise
import it.polimi.mobile.design.custom_views.MinimizedExerciseView
import it.polimi.mobile.design.helpers.Constant


class WorkoutLayout(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private val distanceY = 80.toPx()
    private val fakePadding = 80.toPx()

    private var exercises: List<Exercise> = listOf()
        set(exercises) {
            field = exercises
            // TODO
        }

    init {

        setWillNotDraw(false)
        exercises = listOf(Exercise(), Exercise(), Exercise(), Exercise(), Exercise(), Exercise())
        post{drawExercises()}
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLines(canvas)
    }

    private fun drawExercises() {

        val dataPointSize = Constant.DATA_BUTTON_SIZE
        for (exerciseIndex in exercises.indices) {

            val exerciseView = MinimizedExerciseView(context)

            exerciseView.id = View.generateViewId()

            val params = LayoutParams(dataPointSize, dataPointSize)
            params.leftMargin = (fakePadding / 2) + (exerciseIndex % 2) * (width - 2 * fakePadding) +
                    ((fakePadding / 2) - dataPointSize) + dataPointSize / 2
            params.topMargin = (fakePadding / 2) + exerciseIndex * distanceY -
                    (Constant.DATA_BUTTON_SIZE / 2)

            addView(exerciseView, params)
        }

    }

    private fun drawLines(canvas: Canvas) {

        val linesPaint = Paint()
        linesPaint.strokeWidth = 7.5f
        linesPaint.color = Color.WHITE
        linesPaint.style = Paint.Style.STROKE
        linesPaint.strokeCap = Paint.Cap.ROUND

        for (exerciseIndex in 0 until exercises.size - 1) {
            canvas.drawLine(
                fakePadding + (exerciseIndex % 2) * (width - 2f  * fakePadding),
                (fakePadding / 2) + exerciseIndex * distanceY.toFloat(),
                fakePadding + ((exerciseIndex + 1) % 2) * (width - 2f  * fakePadding),
                (fakePadding / 2) + (exerciseIndex + 1) * distanceY.toFloat(),
                linesPaint
            )
        }

    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}