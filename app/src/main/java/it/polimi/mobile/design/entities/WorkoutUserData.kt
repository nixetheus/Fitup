package it.polimi.mobile.design.entities

data class WorkoutUserData (
    val id: String? = null,
    val uid: String? = null,
    val workoutId: String? = null,
    var lastDate: Long? = null,
    var numberOfTimesPlayed: Int? = 0
)
