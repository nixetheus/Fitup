package it.polimi.mobile.design.helpers

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.format.DateUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import it.polimi.mobile.design.R
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.enum.WorkoutTypes
import java.io.Serializable

class HelperFunctions {

    fun secondsToFormatString(seconds: Int) : String {
        return DateUtils.formatElapsedTime(seconds.toLong())
    }
    fun parseIntInput(text: String) : Int {
        if (text.isNotEmpty() && text.isDigitsOnly())
            return Integer.parseInt(text)
        return 0
    }

    fun parseFloatInput(text: String) : Float {
        if (text.isNotEmpty() && text.replace(".", "").isDigitsOnly())
            return text.toFloat()
        return 0f
    }

    fun getWorkoutType(exercisesTypes: MutableList<Int>): Int {
        val orderedIndices = exercisesTypes.indices.sortedBy { type -> exercisesTypes[type] }.reversed()
        val highestIndex = if (orderedIndices.isNotEmpty()) orderedIndices[0] else null
        return if (highestIndex != null && exercisesTypes.count { it == exercisesTypes[highestIndex] } == 1) {
            highestIndex
        } else {
            ExerciseType.FULL_BODY.ordinal
        }
    }



    inline fun <reified T : Serializable> getSerializableExtra(intent: Intent, extraName: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(extraName, T::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(extraName) as? T
        }
    }

    inline fun <reified T : Serializable> getExtra(intent: Intent, extraName: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (T::class.java) {
                Long::class.java -> intent.getLongExtra(extraName, 0) as? T
                Int::class.java -> intent.getIntExtra(extraName, 0) as? T
                Float::class.java -> intent.getFloatExtra(extraName, 0f) as? T
                String::class.java -> intent.getStringExtra(extraName) as? T
                else -> throw NoSuchMethodException()
            }
        } else {
            @Suppress("DEPRECATION")
            intent.extras?.get(extraName) as? T
        }
    }


    fun getExerciseBackground(exerciseType: ExerciseType, resources: Resources, context: Context) : Int {
        return when(exerciseType.ordinal) {
            in ExerciseType.CHEST.ordinal..ExerciseType.TRICEPS.ordinal -> ResourcesCompat.getColor(resources, R.color.red_arms, context.theme)
            in ExerciseType.ABDOMINALS.ordinal..ExerciseType.OBLIQUES.ordinal -> ResourcesCompat.getColor(resources, R.color.yellow_core, context.theme)
            in ExerciseType.QUADRICEPS.ordinal..ExerciseType.CALVES.ordinal -> ResourcesCompat.getColor(resources, R.color.blue_legs, context.theme)
            in ExerciseType.YOGA.ordinal..ExerciseType.YOGA.ordinal -> ResourcesCompat.getColor(resources, R.color.green_spirit, context.theme)
            else -> ResourcesCompat.getColor(resources, R.color.cardio, context.theme)
        }
    }

    fun getWorkoutColor(workoutType: Int, resources: Resources, context: Context) : Drawable {
        return when(workoutType) {
            WorkoutTypes.UPPER_BODY.ordinal -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_arms, context.theme)!!
            WorkoutTypes.ABDOMINALS.ordinal -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_core, context.theme)!!
            WorkoutTypes.LOWER_BODY.ordinal -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_legs, context.theme)!!
            WorkoutTypes.YOGA.ordinal       -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_yoga, context.theme)!!
            WorkoutTypes.CARDIO.ordinal     -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_cardio, context.theme)!!  // TODO: cardio
            else -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_default, context.theme)!!
        }
    }

}