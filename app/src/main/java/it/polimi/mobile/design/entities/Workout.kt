package it.polimi.mobile.design.entities

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import it.polimi.mobile.design.enum.ExerciseType


@kotlinx.serialization.Serializable
data class Workout(
    val workoutId: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val spotifyPlaylistLink: String?=null,
    var ranking:Int?=null,
    private var timestamp: Long? =null ,
    var exercisesType: MutableList<Int>? = MutableList(ExerciseType.values().size) { 0 }
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

    fun getTimestamp(): Map<String?, String?>? {
        return ServerValue.TIMESTAMP
    }

    @Exclude
    fun getTimestampLong(): Long? {
        return  timestamp
    }


    /*@Exclude
    fun getTimestampLong(): Long {
        return timestamp as Long
    }*/




}

