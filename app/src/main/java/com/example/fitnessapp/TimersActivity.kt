package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class TimersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        //Binding vals to buttons
        val backButton = findViewById<Button>(R.id.backButton)
        val stopWatch = findViewById<Button>(R.id.stopWatchButton)
        val timer = findViewById<Button>(R.id.timerButton)
        val tabataTimer = findViewById<Button>(R.id.tabataTimerButton)

        backButton.setOnClickListener {finish()}

        stopWatch.setOnClickListener {
            startActivity(Intent(this, StopWatchActivity::class.java))
        }
        timer.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }
        tabataTimer.setOnClickListener {
            startActivity(Intent(this, TabataTimerActivity::class.java))
        }


    }
}