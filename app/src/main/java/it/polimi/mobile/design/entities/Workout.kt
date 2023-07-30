package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.ExerciseType
import java.io.Serializable


@kotlinx.serialization.Serializable
data class Workout(
    val workoutId: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val spotifyPlaylistLink: String?=null,
    var ranking: Int? = null,
    var timestamp: Long? = null,
    var exercisesType: MutableList<Int>? = MutableList(ExerciseType.values().size) { 0 }
): Serializable {

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

