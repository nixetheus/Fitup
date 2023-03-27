package it.polimi.mobile.design.custom_layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import it.polimi.mobile.design.R
import it.polimi.mobile.design.custom_views.MinimizedExerciseView
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.helpers.Constant


class WorkoutLayout(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private val distanceX = 175.toPx()
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

        val colorOnPrimary = TypedValue()
        context.theme.resolveAttribute (R.attr.colorOnPrimary, colorOnPrimary, true)

        val linesPaint = Paint()
        linesPaint.strokeWidth = 7.5f
        linesPaint.color = colorOnPrimary.data
        linesPaint.style = Paint.Style.STROKE
        linesPaint.strokeCap = Paint.Cap.ROUND

        for (exerciseIndex in 0 until exercises.size - 1) {
            canvas.drawLine(
                paddingStart + (exerciseIndex + 1).toFloat() * Constant.EXERCISE_VIEW_R,
                height / 2f + Constant.EXERCISE_VIEW_R / 2f,
                paddingStart + (exerciseIndex + 1).toFloat() * distanceX,
                height / 2f + Constant.EXERCISE_VIEW_R / 2f,
                linesPaint
            )
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}