package it.polimi.mobile.design.entities


data class Graph (
    val graphId: String? = null,
    val graphName: String? = null,
    val graphMeasure: String? = null,
    val graphType: Int? = 0
) {
    override fun toString(): String {
        return graphName ?: "ERROR"
    }
}