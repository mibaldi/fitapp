package com.mibaldi.fitapp.ui.calendarUtils

import com.mibaldi.domain.Training
import com.mibaldi.domain.generateStringDate
import com.mibaldi.fitapp.R
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.util.*
import kotlin.collections.HashMap

// calendar view manager is responsible for our displaying logic
fun myCalendarViewManager(itemHashMap: HashMap<String,List<Training>>) : CalendarViewManager{


    return object :
        CalendarViewManager {
        override fun bindDataToCalendarView(
            holder: SingleRowCalendarAdapter.CalendarViewHolder,
            date: Date,
            position: Int,
            isSelected: Boolean
        ) {
            // using this method we can bind data to calendar view
            // good practice is if all views in layout have same IDs in all item views
            holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
            holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

        }

        override fun setCalendarViewResourceId(
            position: Int,
            date: Date,
            isSelected: Boolean
        ): Int {
            // set date to calendar according to position where we are
            val cal = Calendar.getInstance()
            cal.time = date
            val generateStringDate = generateStringDate(date)
            val size = itemHashMap[generateStringDate]?.size ?: 0
            return if (isSelected)
                when (size) {
                    0 -> R.layout.selected_calendar_item
                    1 -> R.layout.first_special_selected_calendar_item
                    2 -> R.layout.second_special_selected_calendar_item
                    else -> R.layout.third_special_selected_calendar_item
                }
            else
            // here we return items which are not selected
                when (size) {
                    0 -> R.layout.calendar_item
                    1 -> R.layout.first_special_calendar_item
                    2 -> R.layout.second_special_calendar_item
                    else -> R.layout.third_special_calendar_item
                }

            // NOTE: if we don't want to do it this way, we can simply change color of background
            // in bindDataToCalendarView method
        }
    }
}


fun myCalendarChangesObserver(setupHeader: (Date) -> Unit,selectedDate: (Date)-> Unit): CalendarChangesObserver{
    val myCalendarChangesObserver = object :
        CalendarChangesObserver {
        // you can override more methods, in this example we need only this one
        override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
            setupHeader(date)
            selectedDate(date)

            super.whenSelectionChanged(isSelected, position, date)
        }
    }
    return myCalendarChangesObserver
}
// using calendar changes observer we can track changes in calendar

val mySelectionManager = object : CalendarSelectionManager {
    override fun canBeItemSelected(position: Int, date: Date): Boolean {
        // return true if item can be selected
        return true
    }
}

fun indexCurrentDay(singleRowCalendar: SingleRowCalendar): Int{
    val stringList = singleRowCalendar.getDates().map {
        generateStringDate(it)
    }
    val currentDayString = generateStringDate()
    return stringList.indexOf(currentDayString)
}


