package it.polimi.mobile.design.entities

import it.polimi.mobile.design.enum.Gender

data class User(
    val uId: String? = null,
    val username: String? = null,
    val weight: Float? = null,
    val age: Int? = null,
    val gender: Gender? = null,
    val exp: Float? = null,
    val workoutsCounters: Map<String, Int>? = mapOf(),
    val exercisesCounters: Map<String, Int>? = mapOf()
)