package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fx = InteractionEffects()
        val email = intent.getStringExtra("USER_EMAIL") ?: ""
        val recipeButton = findViewById<Button>(R.id.recipeButton)
        val exerciseButton = findViewById<Button>(R.id.exerciseButton)
        val timersButton = findViewById<Button>(R.id.timersButton)
        val calendarButton = findViewById<Button>(R.id.calendarButton)
        val logoutButton = findViewById<ImageButton>(R.id.logoutButton)
        val firebaseAuth = FirebaseAuth.getInstance()
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        val dateTextView = findViewById<TextView>(R.id.dateTextView)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        val currentDateFormatted = dateFormat.format(calendar.time)

        usernameTextView.text = email
        dateTextView.text = currentDateFormatted

        logoutButton.setOnClickListener {
            fx.imageButtonClickEffect(logoutButton)
            firebaseAuth.signOut()
            finish()
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
            startActivity(Intent(this, TimersActivity::class.java))
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
}