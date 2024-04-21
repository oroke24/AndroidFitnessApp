package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Date

class CalendarActivity : ComponentActivity(){
    private lateinit var backButton: ImageButton
    private lateinit var monthlyCalendar: CalendarView
    private lateinit var weeklyCalendar: RecyclerView
    private lateinit var weeklyAdapter: WeeklyCalendarAdapter
    private lateinit var homeButton: ImageButton
    private lateinit var foodButton: ImageButton
    private lateinit var exerciseButton: ImageButton
    private lateinit var timersButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"
        val fx = InteractionEffects()

        //binding
        backButton = findViewById(R.id.backButton)
        monthlyCalendar = findViewById(R.id.monthlyCalendarView)
        weeklyCalendar = findViewById(R.id.weeklyCalendarView)
        weeklyAdapter = WeeklyCalendarAdapter(email, weeklyCalendar)
        homeButton = findViewById(R.id.menuHomeButton)
        foodButton = findViewById(R.id.menuRecipeButton)
        exerciseButton = findViewById(R.id.menuExerciseButton)
        timersButton = findViewById(R.id.menuTimersButton)

       //setting up weekly recycler view
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weeklyCalendar.layoutManager = layoutManager
        weeklyCalendar.adapter = weeklyAdapter

        backButton.setOnClickListener {
            fx.imageButtonClickEffectQuick(backButton)
            finish()
        }

        homeButton.setOnClickListener{
            fx.imageButtonClickEffectQuick(homeButton)
            intentWithEmail(MainActivity(), email)
        }
        foodButton.setOnClickListener{
            fx.imageButtonClickEffectQuick(foodButton)
            intentWithEmail(RecipeActivity(), email)
        }
        exerciseButton.setOnClickListener{
            fx.imageButtonClickEffectQuick(exerciseButton)
            intentWithEmail(ExerciseActivity(), email)
        }
        timersButton.setOnClickListener{
            fx.imageButtonClickEffectQuick(timersButton)
            intentWithEmail(TimersActivity(), email)
        }
        updateWeeklyView(Calendar.getInstance())

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
    private fun intentWithEmail(nextActivity: ComponentActivity, email: String) {
        val intent = Intent(this, nextActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        startActivity(intent)
    }
}