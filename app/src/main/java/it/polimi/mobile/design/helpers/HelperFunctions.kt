package it.polimi.mobile.design.helpers

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import it.polimi.mobile.design.R
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.enum.ExerciseType
import java.io.Serializable

class HelperFunctions {

    fun secondsToFormatString(seconds: Int) : String {
        return DateUtils.formatElapsedTime(seconds.toLong())
    }
    fun parseIntInput(text: String) : Int {
        if (text.isNotEmpty())
            return Integer.parseInt(text)
        return 0
    }

    fun parseFloatInput(text: String) : Float {
        if (text.isNotEmpty())
            return text.toFloat()
        return 0f
    }

    fun getWorkoutType(exercisesTypes: MutableList<Int>) : Int {
        val orderedIndices =
            exercisesTypes.indices.sortedBy { type -> exercisesTypes[type] }.reversed()
        return if (exercisesTypes[orderedIndices[0]] > exercisesTypes[orderedIndices[1]])
            orderedIndices[0]
        else ExerciseType.FULL_BODY.ordinal
    }

    inline fun <reified T : Serializable> getSerializableExtra(intent: Intent, extraName: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(extraName, T::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(extraName) as? T
        }
    }


    fun getExerciseBackground(exerciseType: ExerciseType, resources: Resources, context: Context) : Int {
        return when(exerciseType.ordinal) {
            0 -> ResourcesCompat.getColor(resources, R.color.red_arms, context.theme)
            1 -> ResourcesCompat.getColor(resources, R.color.blue_legs, context.theme)
            2 -> ResourcesCompat.getColor(resources, R.color.yellow_core, context.theme)
            3 -> ResourcesCompat.getColor(resources, R.color.green_spirit, context.theme)
            else -> ResourcesCompat.getColor(resources, R.color.exercise_default, context.theme)
        }
    }

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

}