package it.polimi.mobile.design.entities

data class WorkoutExercise(
    val id: String? = null,
    val workoutId: String? = null,
    val exerciseId: String? = null,
    val exerciseName: String? = null,
    val sets: Int? = null,
    val reps: Int? = null,
    val rest :Int? = null,
    val weight: Float? = null,
    val buffer: Int? = null
)
