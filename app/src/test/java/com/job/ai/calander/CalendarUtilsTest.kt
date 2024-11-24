package com.job.ai.calander

import com.job.ai.calander.utils.calendarFromDate
import com.job.ai.calander.utils.firstDayOfMonth
import com.job.ai.calander.utils.isCurrentDate
import com.job.ai.calander.utils.isDateOfCurrentMonth
import com.job.ai.calander.utils.numberOfDaysInMonth
import com.job.ai.calander.utils.selectedDate
import com.job.ai.calander.utils.toDateFormat
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class CalendarUtilsTest {

    @Test
    fun testFirstDayOfMonth() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 23)
        }

        val firstDayOfMonth = calendar.firstDayOfMonth()

        assertEquals(Calendar.FRIDAY, firstDayOfMonth)
    }

    @Test
    fun testNumberOfDaysInMonth() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val numberOfDays = calendar.numberOfDaysInMonth()

        assertEquals(29, numberOfDays)
    }

    @Test
    fun testCalendarFromDate() {
        val date = Date(2024-1900, 10, 23)

        val calendar = date.calendarFromDate()

        assertEquals(2024, calendar.get(Calendar.YEAR))
        assertEquals(Calendar.NOVEMBER, calendar.get(Calendar.MONTH))
    }

    @Test
    fun testIsCurrentDate() {
        val currentCalendar = Calendar.getInstance()

        val testCalendar = Calendar.getInstance().apply {
            set(Calendar.DATE, currentCalendar.get(Calendar.DATE))
        }

        assertTrue(testCalendar.isCurrentDate(currentCalendar))

        testCalendar.add(Calendar.DATE, -1)
        assertFalse(testCalendar.isCurrentDate(currentCalendar))
    }

    @Test
    fun testIsDateOfCurrentMonth() {
        val currentCalendar = Calendar.getInstance()

        val testCalendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH))
            set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR))
        }

        assertTrue(testCalendar.isDateOfCurrentMonth(currentCalendar))

        testCalendar.add(Calendar.MONTH, -1)
        assertFalse(testCalendar.isDateOfCurrentMonth(currentCalendar))
    }

    @Test
    fun testSelectedDate() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DATE, 23)
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.YEAR, 2024)
        }

        calendar.selectedDate(20, calendar)

        assertEquals(20, calendar.get(Calendar.DATE))
    }

    @Test
    fun testToDateFormat() {
        val dateInMillis = 1704067200000L // 1st January 2024

        val formattedDate = dateInMillis.toDateFormat()

        assertEquals("01/01/2024", formattedDate)
    }
}
