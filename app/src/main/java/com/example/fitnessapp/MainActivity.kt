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

        recipeButton.setOnClickListener{
            val intent = Intent(this, RecipeActivity::class.java)
            startActivity(intent)
        }
        exerciseButton.setOnClickListener{
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }
        dateButton.setOnClickListener{
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
            val currentDateFormatted = dateFormat.format(calendar.time)
            Toast.makeText(this, "Today's date: $currentDateFormatted", Toast.LENGTH_SHORT).show()
        }
    }
}