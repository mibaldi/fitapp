package com.mibaldi.domain

import java.util.*

fun generateStringDate(calendarDate: Date? = null): String{
    val cal = Calendar.getInstance()
    if (calendarDate != null) cal.time = calendarDate
    return "${cal.get(Calendar.DAY_OF_MONTH)}-${cal.get(Calendar.MONTH)+1}-${cal.get(Calendar.YEAR)}"
}

