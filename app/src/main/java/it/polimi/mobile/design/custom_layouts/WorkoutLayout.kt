package it.polimi.mobile.design.custom_layouts

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.R
import it.polimi.mobile.design.databinding.FragmentExerciseInPlayBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.helpers.Constant
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions


class WorkoutLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var toDraw = false
    private val distanceX = 90.toPx()
    var exercises: List<WorkoutExercise> = listOf()
    private val helperDB = DatabaseHelper().getInstance()
    private val exerciseFragments = mutableListOf<FragmentExerciseInPlayBinding>()

    init {
        setWillNotDraw(false)
        exercises = listOf()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLines(canvas)
        drawExercises()
    }

    private fun drawExercises() {
        if (toDraw) {
            toDraw = false
            for (exerciseIndex in exercises.indices) {
                // Exercise view
                val layoutInflater = LayoutInflater.from(context)
                val exerciseView = FragmentExerciseInPlayBinding.inflate(layoutInflater)
                exerciseView.exerciseNamePlay.text = exercises[exerciseIndex].exerciseName!!
                    .lowercase().replaceFirstChar { it.uppercaseChar() }
                addView(exerciseView.root)
                exerciseFragments.add(exerciseView)
                setExerciseTypeImage(exercises[exerciseIndex], exerciseIndex)
            }
        }
    }

    fun populateExercises(workoutExercises: List<WorkoutExercise>) {
        exercises = workoutExercises
        toDraw = true
        invalidate()
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

    fun completeExercise(index: Int) {
        try {
            exerciseFragments[index].exerciseLevel.drawInsideColor()
        }
        catch (_: ArrayIndexOutOfBoundsException) {}
        catch (_: java.lang.IndexOutOfBoundsException) {}
    }

    private fun renderImageVisible(index: Int) {
        try {
            exerciseFragments[index].exTypeImage.visibility = VISIBLE
        }
        catch (_: ArrayIndexOutOfBoundsException) {}
    }

    private fun setExerciseTypeImage(workoutExercise: WorkoutExercise, index: Int) {
        helperDB.exercisesSchema.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val exercises = helperDB.getExercisesFromSnapshot(dataSnapshot)
                    val type = exercises.find { it.eid == workoutExercise.exerciseId }!!.type?: ExerciseType.CHEST
                    exerciseFragments[index].exTypeImage.setImageDrawable(getExerciseTypeImage(type))
                    renderImageVisible(index)
                    exerciseFragments[index].exerciseLevel.setFinishedColor(
                        HelperFunctions().getExerciseBackground(type, resources, context)
                    )
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getExerciseTypeImage(type: ExerciseType) : Drawable {
        return when(type.ordinal) {
            in ExerciseType.CHEST.ordinal..ExerciseType.TRICEPS.ordinal    -> resources.getDrawable(R.drawable.muscle, context.theme)!!
            in ExerciseType.ABDOMINALS.ordinal..ExerciseType.OBLIQUES.ordinal    -> resources.getDrawable(R.drawable.body, context.theme)!!
            in ExerciseType.QUADRICEPS.ordinal..ExerciseType.CALVES.ordinal    -> resources.getDrawable(R.drawable.leg, context.theme)!!
            in ExerciseType.YOGA.ordinal..ExerciseType.YOGA.ordinal    -> resources.getDrawable(R.drawable.yoga, context.theme)!!
            else -> resources.getDrawable(R.drawable.bpm, context.theme)!!
        }
    }
}