package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dateButton = findViewById<Button>(R.id.dateButton)
        val recipeButton = findViewById<Button>(R.id.recipeButton)
        val exerciseButton = findViewById<Button>(R.id.exerciseButton)
        val timersButton = findViewById<Button>(R.id.timersButton)
        val calendarButton = findViewById<Button>(R.id.calendarButton)

        dateButton.setOnClickListener{
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
            val currentDateFormatted = dateFormat.format(calendar.time)
            Toast.makeText(this, "Today's date: $currentDateFormatted", Toast.LENGTH_SHORT).show()
        }
        recipeButton.setOnClickListener{
            startActivity(Intent(this, RecipeActivity::class.java))
        }
        exerciseButton.setOnClickListener{
            startActivity(Intent(this, ExerciseActivity::class.java))
        }
        timersButton.setOnClickListener {
            startActivity(Intent(this, TimersActivity::class.java))
        }
        calendarButton.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
    }
}