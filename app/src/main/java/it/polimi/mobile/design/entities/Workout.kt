package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.WorkoutType


data class Workout(
    val workoutId: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val type: WorkoutType? = null,
    val spotifyPlaylistLink: String?=null
): java.io.Serializable
