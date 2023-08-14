package it.polimi.mobile.design.helpers

import android.content.res.Resources

object Constant {
    const val DATA_BUTTON_SIZE = 100
    const val MAX_SIZE_GRAPH = 20
    val EXERCISE_VIEW_R = 75.toPx()
    const val CLIENT_ID = "8db4d298653041b4b1850c09464182a3"
    const val REDIRECT_URI = "mobile-app-login://callback"
    const val ADMIN_ID = "p0YFtxASaFXbjqkxBJVXsAbeZYG3"

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}