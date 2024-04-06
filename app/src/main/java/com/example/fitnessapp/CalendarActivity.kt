package com.example.fitnessapp

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import java.util.Calendar
import java.util.Date

class CalendarActivity : ComponentActivity(){
    private lateinit var backButton: ImageButton
    private lateinit var monthlyCalendar: CalendarView
    private lateinit var weeklyCalendar: RecyclerView
    private lateinit var weeklyAdapter: WeeklyCalendarAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        //binding
        backButton = findViewById<ImageButton>(R.id.backButton)
        monthlyCalendar = findViewById<CalendarView>(R.id.monthlyCalendarView)
        weeklyCalendar = findViewById<RecyclerView>(R.id.weeklyCalendarView)
        weeklyAdapter = WeeklyCalendarAdapter()

       //setting up weekly recycler view
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weeklyCalendar.layoutManager = layoutManager
        weeklyCalendar.adapter = weeklyAdapter

        updateWeeklyView(Calendar.getInstance())
        backButton.setOnClickListener {finish()}

        monthlyCalendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            updateWeeklyView(calendar)
        }

    }

    private fun updateWeeklyView(calendar: Calendar) {
        val daysOfWeek = arrayListOf<Date>()

        //setting first day of weekly view to selected day
        val cal = Calendar.getInstance()
        cal.time = calendar.time

        //filling in the rest of the days
        for(i in 0 until 7){
            daysOfWeek.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        //updating adapter
        weeklyAdapter.updateData(daysOfWeek)
    }

}