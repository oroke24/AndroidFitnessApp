package com.example.fitnessapp


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity

class StopWatchActivity : ComponentActivity() {
    private var isRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private lateinit var stopwatchTextView: TextView
    private lateinit var millisecondTextView: TextView
    private lateinit var handler: Handler
    private lateinit var secondRunnable: Runnable
    private lateinit var startButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var resetButton: ImageButton
    val fx = InteractionEffects()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)

        stopwatchTextView = findViewById(R.id.stopwatchTextView)
        millisecondTextView = findViewById(R.id.millisecondTextView)
        handler = Handler(Looper.getMainLooper())

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackButtonClick(it)
        }

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            onStartButtonClick(it)
        }

        pauseButton = findViewById(R.id.pauseButton)
        pauseButton.setOnClickListener {
            onPauseButtonClick(it)
        }

        resetButton = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            onResetButtonClick(it)
        }
    }

    fun onBackButtonClick(view: View) {
        finish()
    }

    fun onStartButtonClick(view: View) {
        if (!isRunning) {
            isRunning = true
            startTime = System.currentTimeMillis() - elapsedTime
            startStopwatch()
        }
    }

    private fun startStopwatch() {
        startButton.visibility=View.GONE
        pauseButton.visibility=View.VISIBLE
        secondRunnable = object : Runnable {
            override fun run() {
                // Calculate elapsed time
                elapsedTime = System.currentTimeMillis() - startTime

                // Calculate hours, minutes, seconds, and remaining milliseconds
                val hours = elapsedTime / (1000 * 60 * 60)
                val minutes = (elapsedTime % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = (elapsedTime % (1000 * 60)) / 1000
                val millis = elapsedTime % 1000

                // Format time string
                val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                val milliseconds = String.format("%03d",millis)

                // Update TextView with formatted time
                stopwatchTextView.text = time
                millisecondTextView.text = milliseconds

                // Post delayed execution of this runnable after 10 milliseconds
                handler.postDelayed(this, 10)
            }
        }
        handler.post(secondRunnable)
    }

    fun onPauseButtonClick(view: View) {
        if (isRunning) {
            pauseButton.visibility=View.GONE
            startButton.visibility=View.VISIBLE
            isRunning = false
            handler.removeCallbacks(secondRunnable)
        }
    }

    fun onResetButtonClick(view: View) {
        fx.imageButtonClickEffectQuick(resetButton)
        pauseButton.visibility=View.GONE
        startButton.visibility=View.VISIBLE
        isRunning = false
        startTime = 0
        elapsedTime = 0
        stopwatchTextView.text = "00:00:00"
        millisecondTextView.text = "000"
        handler.removeCallbacks(secondRunnable)
    }
}