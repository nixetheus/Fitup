package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.ExerciseType

data class Exercise(val eId:String?=null, val name: String?=null, val type: ExerciseType?=null)
