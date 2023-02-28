package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.ExerciseType


data class Exercise(
    val eid: String? = null,
    val name: String? = null,
    val caloriesPerRep: Float? = null,
    val type: ExerciseType? = null,
    val experiencePerReps: Int? = null


)
{
    override fun toString(): String {
        return name.toString()
    }
}


