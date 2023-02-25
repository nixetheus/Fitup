package it.polimi.mobile.design.helpers

import android.text.format.DateUtils

class HelperFunctions {

    fun secondsToFormatString(seconds: Int) : String {
        return DateUtils.formatElapsedTime(seconds.toLong())
    }
}