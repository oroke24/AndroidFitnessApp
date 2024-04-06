package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recipeButton = findViewById<Button>(R.id.recipeButton)
        val exerciseButton = findViewById<Button>(R.id.exerciseButton)
        val timersButton = findViewById<Button>(R.id.timersButton)
        val calendarButton = findViewById<Button>(R.id.calendarButton)
        val logoutButton = findViewById<ImageButton>(R.id.logoutButton)
        val firebaseAuth = FirebaseAuth.getInstance()
        val email = intent.getStringExtra("USER_EMAIL")
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        val dateTextView = findViewById<TextView>(R.id.dateTextView)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val currentDateFormatted = dateFormat.format(calendar.time)

        usernameTextView.text = email
        dateTextView.text = currentDateFormatted

        logoutButton.setOnClickListener{
            firebaseAuth.signOut()
            finish()
        }
        recipeButton.setOnClickListener{
            val intent = Intent(this, RecipeActivity::class.java)
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
        }
        exerciseButton.setOnClickListener{
            val intent = Intent(this, ExerciseActivity::class.java)
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
        }
        timersButton.setOnClickListener {
            startActivity(Intent(this, TimersActivity::class.java))
        }
        calendarButton.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
    }
}