package com.example.fitnessapp

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"

        //binding
        backButton = findViewById(R.id.backButton)
        monthlyCalendar = findViewById(R.id.monthlyCalendarView)
        weeklyCalendar = findViewById(R.id.weeklyCalendarView)
        weeklyAdapter = WeeklyCalendarAdapter(email)

       //setting up weekly recycler view
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weeklyCalendar.layoutManager = layoutManager
        weeklyCalendar.adapter = weeklyAdapter

        updateWeeklyView(Calendar.getInstance())
        backButton.setOnClickListener {finish()}

        monthlyCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            updateWeeklyView(selectedDate)
        }

    }

    private fun updateWeeklyView(selectedDate: Calendar) {
        val daysOfWeek = arrayListOf<Date>()
        val cal = Calendar.getInstance()

        cal.time = selectedDate.time
        for(i in 0 until 7){
            daysOfWeek.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        weeklyAdapter.updateData(daysOfWeek)
    }

}