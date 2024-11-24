package com.job.ai.calander.utils

import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Calendar.firstDayOfMonth(): Int {
    val currentDay = this.get(Calendar.DAY_OF_MONTH)
    this.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = this.get(Calendar.DAY_OF_WEEK)
    this.set(Calendar.DAY_OF_MONTH, currentDay)
    return firstDayOfWeek
}

fun Calendar.numberOfDaysInMonth(): Int {
    return getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun Date.calendarFromDate() : Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun Calendar.isCurrentDate(currentCalendar: Calendar): Boolean {
    return get(Calendar.DATE) == currentCalendar.get(Calendar.DATE)
            && isDateOfCurrentMonth(currentCalendar)
}

fun Calendar.isDateOfCurrentMonth(currentCalendar: Calendar): Boolean {
    return get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
            && get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
}

fun Calendar.selectedDate(date: Int, currentCalendar: Calendar) {
    set(Calendar.DATE, date)
    set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH))
    set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR))
}

fun showSnackBar(layout: CoordinatorLayout, message: String) {
    val snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG)
    val snackbarView = snackbar.view
    val params = snackbarView.layoutParams as CoordinatorLayout.LayoutParams
    params.gravity = android.view.Gravity.TOP
    snackbarView.layoutParams = params

    snackbar.show()
}

fun Long.toDateFormat(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.timeZone = TimeZone.getDefault()
    return format.format(date)
}