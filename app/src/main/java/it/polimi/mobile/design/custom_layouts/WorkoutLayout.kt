package it.polimi.mobile.design.custom_layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import it.polimi.mobile.design.custom_views.MinimizedExerciseView
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.helpers.Constant


class WorkoutLayout(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private val distanceX = 150.toPx()
    var exercises: List<WorkoutExercise> = listOf()

    init {
        setWillNotDraw(false)
        exercises = listOf()
        post{drawExercises()}
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLines(canvas)
    }

    private fun drawExercises() {
        for (exerciseIndex in exercises.indices) {

            val exerciseView = MinimizedExerciseView(context)
            exerciseView.id = View.generateViewId()

            val params = LayoutParams(Constant.EXERCISE_VIEW_R, Constant.EXERCISE_VIEW_R)
            params.leftMargin = distanceX * exerciseIndex

            addView(exerciseView, params)
        }
    }

    private fun drawLines(canvas: Canvas) {

        /*val linesPaint = Paint()
        linesPaint.strokeWidth = 7.5f
        linesPaint.color = Color.WHITE
        linesPaint.style = Paint.Style.STROKE
        linesPaint.strokeCap = Paint.Cap.ROUND

        val fakePadding = 10
        for (exerciseIndex in 0 until exercises.size - 1) {

            // Draw bottom
            canvas.drawLine(
                fakePadding + (exerciseIndex % 2) * (width - 2f  * fakePadding),
                (fakePadding * 2) + exerciseIndex * distanceY.toFloat(),
                fakePadding + (exerciseIndex % 2) * (width - 2f  * fakePadding),
                (fakePadding * 2) + (exerciseIndex + 1) * distanceY.toFloat(),
                linesPaint
            )

            // If even draw bottom-right else draw bottom left
            canvas.drawLine(
                fakePadding + (exerciseIndex % 2) * (width - 2f  * fakePadding),
                (fakePadding * 2) + (exerciseIndex + 1) * distanceY.toFloat(),
                fakePadding + ((exerciseIndex + 1) % 2) * (width - 2f  * fakePadding),
                (fakePadding * 2) + (exerciseIndex + 1) * distanceY.toFloat(),
                linesPaint
            )
        }

        // Draw final line
        canvas.drawLine(
            fakePadding + ((exercises.size - 1) % 2) * (width - 2f  * fakePadding),
            (fakePadding * 2) + (exercises.size - 1) * distanceY.toFloat(),
            fakePadding + ((exercises.size - 1) % 2) * (width - 2f  * fakePadding),
            (fakePadding * 2) + ((exercises.size - 1) + 1) * distanceY.toFloat() - (distanceY / 2),
            linesPaint
        )*/
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}