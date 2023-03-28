package it.polimi.mobile.design.custom_layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import it.polimi.mobile.design.R
import it.polimi.mobile.design.custom_views.MinimizedExerciseView
import it.polimi.mobile.design.databinding.FragmentExerciseInPlayBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.helpers.Constant


class WorkoutLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val distanceX = 75.toPx()
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

            // Exercise view
            val layoutInflater = LayoutInflater.from(context)
            val exerciseView = FragmentExerciseInPlayBinding.inflate(layoutInflater)
            exerciseView.exerciseNamePlay.text = exercises[exerciseIndex].exerciseName!!
                .lowercase().replaceFirstChar { it.uppercaseChar() }
            addView(exerciseView.root)
        }
    }

    private fun drawLines(canvas: Canvas) {

        val colorOnPrimary = TypedValue()
        context.theme.resolveAttribute (R.attr.colorOnPrimary, colorOnPrimary, true)

        val linesPaint = Paint()
        linesPaint.strokeWidth = 10f
        linesPaint.color = colorOnPrimary.data
        linesPaint.style = Paint.Style.STROKE
        linesPaint.strokeCap = Paint.Cap.ROUND
        linesPaint.pathEffect = DashPathEffect(floatArrayOf(10f, 30f), 0f)

        for (exerciseIndex in -1 until exercises.size) {
            canvas.drawLine(
                (exerciseIndex + 1).toFloat() * (distanceX + Constant.EXERCISE_VIEW_R),
                height / 2f + Constant.EXERCISE_VIEW_R / 1.75f,
                (exerciseIndex + 2).toFloat() * distanceX + (exerciseIndex + 1).toFloat() * Constant.EXERCISE_VIEW_R,
                height / 2f + Constant.EXERCISE_VIEW_R / 1.75f,
                linesPaint
            )
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}