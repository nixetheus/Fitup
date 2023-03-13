package it.polimi.mobile.design.enum

import android.content.res.Resources
import it.polimi.mobile.design.R

enum class GraphType {

    BODY_COMP,
    ONE_REP_MAX;

    override fun toString(): String {
        return when(this) {
            BODY_COMP -> "Body Composition"  // TODO
            ONE_REP_MAX -> "Max Repetitions"
            else -> "NONE"
        }
    }
}