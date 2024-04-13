package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var email: String
    lateinit var todaysRecipeTextView : TextView
    lateinit var todaysExerciseTextView : TextView
    lateinit var dateTextView : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = intent.getStringExtra("USER_EMAIL") ?: ""
        val fx = InteractionEffects()
        val todaySnapShotLayout = findViewById<LinearLayout>(R.id.todaySnapShotLayout)
        val recipeButton = findViewById<Button>(R.id.recipeButton)
        val exerciseButton = findViewById<Button>(R.id.exerciseButton)
        val timersButton = findViewById<Button>(R.id.timersButton)
        val calendarButton = findViewById<Button>(R.id.calendarButton)
        val logoutButton = findViewById<ImageButton>(R.id.logoutButton)
        val firebaseAuth = FirebaseAuth.getInstance()
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        dateTextView = findViewById(R.id.dateTextView)
        todaysRecipeTextView = findViewById(R.id.recipeName)
        todaysExerciseTextView = findViewById(R.id.exerciseName)




        setToday(DayDataManager(email))
        usernameTextView.text = email

        logoutButton.setOnClickListener {
            fx.imageButtonClickEffect(logoutButton)
            firebaseAuth.signOut()
            intentWithEmail(LoginActivity(), email)
            finish()
        }
        todaySnapShotLayout.setOnClickListener{
            fx.itemViewClickEffect(todaySnapShotLayout)
            intentWithEmail(CalendarActivity(), email)
        }
        recipeButton.setOnClickListener {
            fx.buttonClickEffect(recipeButton)
            intentWithEmail(RecipeActivity(), email)
        }
        exerciseButton.setOnClickListener {
            fx.buttonClickEffect(exerciseButton)
            intentWithEmail(ExerciseActivity(), email)
        }
        timersButton.setOnClickListener {
            fx.buttonClickEffect(timersButton)
            intentWithEmail(TimersActivity(), email)
        }
        calendarButton.setOnClickListener {
            fx.buttonClickEffect(calendarButton)
            intentWithEmail(CalendarActivity(), email)
        }
    }

    private fun intentWithEmail(thisActivity: ComponentActivity, email: String) {
        val intent = Intent(this, thisActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        setToday(DayDataManager(email))
    }
    private fun setToday(dayDataManager: DayDataManager) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        val currentDateFormatted = dateFormat.format(calendar.time)
        val formattedDateForDB = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.time)
        dateTextView.text = currentDateFormatted
        CoroutineScope(Dispatchers.Main).launch {
            val thisDay = dayDataManager.getDayFromDate(formattedDateForDB)
            todaysRecipeTextView.text = thisDay.recipeId
            todaysExerciseTextView.text = thisDay.exerciseId
            if (todaysRecipeTextView.text.isBlank()) {
                todaysRecipeTextView.text = getString(R.string.none)
            }
            if (todaysExerciseTextView.text.isBlank()) {
                todaysExerciseTextView.text = getString(R.string.none)
            }
            todaysRecipeTextView.visibility = View.VISIBLE
            todaysExerciseTextView.visibility = View.VISIBLE
        }
    }
}