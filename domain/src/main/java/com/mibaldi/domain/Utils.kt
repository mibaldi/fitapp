package com.mibaldi.domain

import java.util.*
import java.util.concurrent.TimeUnit

fun generateStringDate(calendarDate: Date? = null): String{
    val cal = Calendar.getInstance()
    if (calendarDate != null) cal.time = calendarDate
    return "${cal.get(Calendar.DAY_OF_MONTH)}-${cal.get(Calendar.MONTH)+1}-${cal.get(Calendar.YEAR)}"
}

fun Long.toWorkoutString(hours:Boolean = false):String{
    var pattern = "%02d:%02d"
    if (hours){
        val mHours= TimeUnit.MILLISECONDS.toHours(this) % 60
        pattern = "%02d:%02d:%02d"
        return java.lang.String.format(
            Locale.getDefault(), pattern,mHours ,
            TimeUnit.MILLISECONDS.toMinutes(this) % 60,
            TimeUnit.MILLISECONDS.toSeconds(this) % 60
        )
    }
    return java.lang.String.format(
        Locale.getDefault(), pattern,
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )
}