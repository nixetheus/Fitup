package it.polimi.mobile.design.helpers

import android.content.res.Resources

object Constant {
    const val DATA_BUTTON_SIZE = 100
    const val MAX_SIZE_GRAPH = 20
    val EXERCISE_VIEW_R = 75.toPx()

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}