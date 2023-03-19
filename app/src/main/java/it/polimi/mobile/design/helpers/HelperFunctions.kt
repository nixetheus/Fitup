package it.polimi.mobile.design.helpers

import android.text.format.DateUtils
import android.util.Log
import it.polimi.mobile.design.enum.ExerciseType

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

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

}