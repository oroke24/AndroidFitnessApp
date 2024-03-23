package com.example.fitnessapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class TimerActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        //binding buttons
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {finish()}
    }
}