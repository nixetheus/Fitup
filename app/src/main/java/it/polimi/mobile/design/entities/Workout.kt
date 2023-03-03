package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.enum.WorkoutType


data class Workout(
    val workoutId: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val type: WorkoutType? = null,
    val spotifyPlaylistLink: String?=null,
    var exercisesType: MutableList<Int>? = MutableList(ExerciseType.values().size) { _ -> 0 }
): java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Workout

        if (workoutId != other.workoutId) return false

        return true
    }

    override fun hashCode(): Int {
        return workoutId?.hashCode() ?: 0
    }
}
