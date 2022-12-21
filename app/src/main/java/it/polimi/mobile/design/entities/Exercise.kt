package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.ExerciseType


data class Exercise(val eid:String?=null, val name: String?=null, val caloriesPerRep: String?=null, val type: ExerciseType?=null, val experiencePerReps :String?=null){


}


