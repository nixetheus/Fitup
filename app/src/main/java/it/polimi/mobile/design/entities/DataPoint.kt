package it.polimi.mobile.design.entities

import java.time.LocalDateTime

data class DataPoint (
    val userId: String? = null,
    val pointId: String? = null,
    val graphId: String? = null,
    val xCoordinate: LocalDateTime = LocalDateTime.MIN,
    val yCoordinate: Float = 0.0f
    )