package it.polimi.mobile.design.entities

data class Achievement (
    val achievementId: String? = null,
    val description: String? = null,
    val numberOfTiers: Int? = 0,
    val observedValueId: String? = null,
    val tiers: ArrayList<Tier> = arrayListOf()
)
