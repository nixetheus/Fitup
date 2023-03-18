package it.polimi.mobile.design.entities

data class DataPoint (
    val userId: String? = null,
    val pointId: String? = null,
    val graphId: String? = null,
    val xcoordinate: Long? = 0,  // LocalDateTime in milliseconds since Epoch
    val ycoordinate: Float? = 0.0f
    )