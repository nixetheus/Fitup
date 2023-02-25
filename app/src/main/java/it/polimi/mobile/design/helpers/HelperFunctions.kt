package it.polimi.mobile.design.helpers

import android.text.format.DateUtils

class HelperFunctions {

    fun secondsToFormatString(seconds: Int) : String {
        return DateUtils.formatElapsedTime(seconds.toLong())
    }
    fun parseIntInput(text: String) : Int {
        if (text.isNotEmpty())
            return Integer.parseInt(text)
        return 0
    }

    fun parseFloatInput(text: String) : Float {
        if (text.isNotEmpty())
            return text.toFloat()
        return 0f
    }
}