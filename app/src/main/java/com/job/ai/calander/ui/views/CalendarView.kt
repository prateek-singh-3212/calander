package com.job.ai.calander.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setMargins
import com.job.ai.calander.R
import com.job.ai.calander.databinding.CalendarCardBinding
import com.job.ai.calander.utils.calendarFromDate
import com.job.ai.calander.utils.firstDayOfMonth
import com.job.ai.calander.utils.isCurrentDate
import com.job.ai.calander.utils.isDateOfCurrentMonth
import com.job.ai.calander.utils.numberOfDaysInMonth
import com.job.ai.calander.utils.selectedDate
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.abs


class CustomCalendarView : LinearLayout {
    private lateinit var calendarCardBinding: CalendarCardBinding

    private val currentCalendar: Calendar by lazy {
        Calendar.getInstance()
    }
    private val dayViews: MutableList<TextView> by lazy { mutableListOf() }
    private var onDateSelectedListener: OnDateSelectedListener? = null
    var dateSelected: Calendar? = null
    private val todayDate by lazy {
        currentCalendar.time.calendarFromDate()
    }

    interface OnDateSelectedListener {
        fun onDateSelected(date: Long)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(context: Context) {
        calendarCardBinding = CalendarCardBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        calendarCardBinding.prevButton.setOnClickListener { // Move to the previous month
            updateMonth(-1)
        }

        calendarCardBinding.nextButton.setOnClickListener { // Move to the next month
            updateMonth(1)
        }

        updateCalendar()
    }

    private fun updateMonth(num: Int) {
        currentCalendar.add(Calendar.MONTH, -1)
        updateCalendar()
    }

    private fun updateCalendar() {
        updateMonthHeader()

        val firstDayOfMonth = currentCalendar.firstDayOfMonth()
        val daysInMonth = currentCalendar.numberOfDaysInMonth()

        // Clear any existing views in the grid layout
        calendarCardBinding.gridLayout.removeAllViews()
        dayViews.clear()

        val dayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (dayName in dayNames) {
            val dayNameView = createDayDateTextView(dayName, isDay = true, isEmpty = false)
            calendarCardBinding.gridLayout.addView(dayNameView)
        }

        // Fill the grid with empty spaces until the first day of the month
        for (i in 1 until firstDayOfMonth) {
            val emptyView = createDayDateTextView("", isDay = true, isEmpty = true)
            calendarCardBinding.gridLayout.addView(emptyView)
        }

        // Add the days of the month
        for (day in 1..daysInMonth) {
            val dayView = createDayDateTextView(day.toString(), isDay = false, isEmpty = false)

            dayView.setOnClickListener { view ->
                val textView = view as TextView
                // Remove previous selected value
                dateSelected?.let { getViewFromDate(it) }?.isSelected = false
                dateSelected = (currentCalendar.clone() as Calendar).apply {
                    val date = textView.text.toString().toInt()
                    set(Calendar.DATE, date)
                }
                textView.isSelected = true
                dateSelected?.selectedDate(textView.text.toString().toInt(), currentCalendar)
                if (onDateSelectedListener != null) {
                    dateSelected?.let {
                        onDateSelectedListener?.onDateSelected(it.time.time)
                    }
                }
            }

            calendarCardBinding.gridLayout.addView(dayView)
            dayViews.add(dayView)
        }

        // Highlight selected date
        if (todayDate.isCurrentDate(currentCalendar)) {
            getViewFromDate(todayDate).setBackgroundResource(R.drawable.date_current)
        }
        if (dateSelected?.isDateOfCurrentMonth(currentCalendar) == true) {
            dateSelected?.let { getViewFromDate(it).isSelected = true }
        }
    }

    private fun updateMonthHeader() {
        val monthYearFormat = SimpleDateFormat("MMMM, yyyy")
        calendarCardBinding.monthYearText.text = monthYearFormat.format(currentCalendar.time)
    }

    private fun getViewFromDate(calendar: Calendar): TextView {
        return dayViews[calendar.get(Calendar.DATE) - 1]
    }

    private fun createDayDateTextView(dayDate: String, isDay: Boolean, isEmpty: Boolean): TextView {
        val layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED, 1f),
            GridLayout.spec(GridLayout.UNDEFINED, 1f)
        ).apply {
            width = 0
            setMargins(2)
        }
        return TextView(context).apply {
            text = dayDate
            setLayoutParams(layoutParams)
            viewTreeObserver.addOnPreDrawListener {
                layoutParams.height = width
                requestLayout()
                true
            }
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            textAlignment = TEXT_ALIGNMENT_CENTER
            setPadding(8, 8, 8, 8)
            if (!isDay) setBackgroundResource(R.drawable.date_selector)
        }
    }

    fun setOnDateSelectedListener(listener: OnDateSelectedListener?) {
        this.onDateSelectedListener = listener
    }

    fun setSelectedDate(newTime: Calendar) {
        dateSelected = newTime.clone() as Calendar
        if (onDateSelectedListener != null) {
            dateSelected?.let {
                onDateSelectedListener?.onDateSelected(it.time.time)
            }
        }
        updateCalendar()
    }

    fun setSelectedYear(month: Int, year: Int) {
        currentCalendar.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }
        updateCalendar()
    }
}