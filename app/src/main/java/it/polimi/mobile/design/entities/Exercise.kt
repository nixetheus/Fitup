package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.ExerciseType


data class Exercise(
    var eid: String? = null,
    var name: String? = null,
    val caloriesPerRep: Float? = null,
    val type: ExerciseType? = null,
    val experiencePerReps: Float? = null
)
{
    override fun toString(): String {
        return name.toString()
    }
}


