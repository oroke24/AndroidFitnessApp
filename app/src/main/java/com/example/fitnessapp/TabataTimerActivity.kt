package com.example.fitnessapp

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.media.Image
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class TabataTimerActivity : ComponentActivity() {

    private lateinit var minutesPicker: NumberPicker
    private lateinit var workIntervalInSecondsPicker: NumberPicker
    private lateinit var restIntervalInSecondsPicker: NumberPicker
    private lateinit var timerLayoutView: LinearLayout
    private lateinit var typeTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var elapsedIntervalTextView: TextView
    private lateinit var minutesTextView: TextView
    private lateinit var workTextView: TextView
    private lateinit var restTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var startStopButton: ImageButton
    private var timerRunning: Boolean = false
    private lateinit var timer: CountDownTimer
    private var alarmRingtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabata_timer)

        timerLayoutView = findViewById(R.id.timerLayout)
        minutesPicker = findViewById(R.id.minutesPicker)
        workIntervalInSecondsPicker = findViewById(R.id.workPicker)
        restIntervalInSecondsPicker = findViewById(R.id.restPicker)
        typeTextView = findViewById(R.id.typeTextView)
        timerTextView = findViewById(R.id.timerTextView)
        elapsedIntervalTextView = findViewById(R.id.elapsedIntervalTextView)
        minutesTextView = findViewById(R.id.minutesTextView)
        workTextView = findViewById(R.id.workTextView)
        restTextView = findViewById(R.id.restTextView)
        backButton = findViewById(R.id.backButton)
        startStopButton = findViewById(R.id.startStopButton)

        timerTextView.visibility = View.GONE
        elapsedIntervalTextView.visibility= View.GONE

        configureNumberPickers()
        backButton.setOnClickListener{finish()}

        startStopButton.setOnClickListener {
            if (timerRunning) {
                stopTimer()
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                startBufferTimer()
            }
        }
    }

    private fun configureNumberPickers() {
        // Configure minutes picker
        minutesPicker.textColor = getColor(R.color.white)
        minutesPicker.textSize = 60f
        minutesPicker.minValue = 1
        minutesPicker.maxValue = 60
        minutesPicker.value = 4

        // Configure work picker
        workIntervalInSecondsPicker.textColor = getColor(R.color.white)
        workIntervalInSecondsPicker.textSize = 60f
        workIntervalInSecondsPicker.minValue = 1
        workIntervalInSecondsPicker.maxValue = 60
        workIntervalInSecondsPicker.value = 20

        // Configure rest picker
        restIntervalInSecondsPicker.textColor = getColor(R.color.white)
        restIntervalInSecondsPicker.textSize = 60f
        restIntervalInSecondsPicker.minValue = 1
        restIntervalInSecondsPicker.maxValue = 60
        restIntervalInSecondsPicker.value = 10
    }
    private fun startBufferTimer() {
        startStopButton.visibility = View.GONE
        minutesPicker.visibility = View.GONE
        workIntervalInSecondsPicker.visibility = View.GONE
        restIntervalInSecondsPicker.visibility = View.GONE
        timerTextView.visibility = View.VISIBLE
        timerTextView.textSize = 80f
        val minutes = minutesPicker.value
        val workSeconds = workIntervalInSecondsPicker.value
        val restSeconds = restIntervalInSecondsPicker.value

        minutesTextView.text= getString(R.string.minutes_template, minutes)
        workTextView.text= getString(R.string.work_template, workSeconds)
        restTextView.text= getString(R.string.rest_template, restSeconds)
        // Countdown from 10 seconds before starting the main timer
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update timerTextView to display the countdown from 10 seconds
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                timerTextView.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                // Start the main timer after the countdown
                startTimer(minutes, workSeconds, restSeconds)
            }
        }.start()
    }

    private fun startTimer(minutes: Int, workSeconds: Int, restSeconds: Int) {
        timerLayoutView.setBackgroundColor(Color.rgb(22,22,22))
        startStopButton.visibility = View.VISIBLE
        elapsedIntervalTextView.visibility = View.VISIBLE
        val totalSeconds = minutes * 60L

        minutesTextView.text= getString(R.string.minutes_template, minutes)
        workTextView.text= getString(R.string.work_template, workSeconds)
        restTextView.text= getString(R.string.rest_template, restSeconds)

        // Log statement to check values
        println("Minutes: ${minutesPicker.value}, Work Interval: $workSeconds seconds, Rest Interval: $restSeconds seconds")

        timer = object : CountDownTimer(totalSeconds * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val minutesDisplay = (millisUntilFinished / 1000) / 60
                val secondsDisplay = (millisUntilFinished / 1000) % 60
                timerTextView.textSize = 80f
                timerTextView.text = String.format("%02d:%02d", minutesDisplay, secondsDisplay)


                val elapsedSeconds = (totalSeconds - millisUntilFinished / 1000).toInt()
                val totalIntervalSeconds = workSeconds + restSeconds
                val elapsedIntervalSeconds = elapsedSeconds % totalIntervalSeconds
                //elapsedIntervalTextView.text = String.format("%02d", elapsedIntervalSeconds)

                val isWorkInterval = elapsedIntervalSeconds < workSeconds

                if (isWorkInterval) {
                    timerLayoutView.setBackgroundColor(Color.rgb(255, 150, 69)) // Work interval color
                    elapsedIntervalTextView.text = String.format("%02d", workSeconds - (elapsedIntervalSeconds % workSeconds))
                    typeTextView.text = getString(R.string.work)
                } else {
                    timerLayoutView.setBackgroundColor(Color.rgb(22, 150, 255)) // Rest interval color
                    elapsedIntervalTextView.text = String.format("%02d", restSeconds - (elapsedIntervalSeconds - workSeconds))
                    typeTextView.text = getString(R.string.rest)
                }
            }

            override fun onFinish() {
                stopTimer()
                timerLayoutView.background = getDrawable(R.drawable.background_dark_circle)
                startVibration()
                startRingtone()
                showDialog()
            }
        }

        timer.start()
        startStopButton.setImageResource(R.drawable.ic_stop_red_48_transparent)
        startStopButton.setBackgroundColor(Color.TRANSPARENT)
        timerRunning = true
    }

    private fun stopTimer() {
        timer.cancel()
        resetTimer()
    }

    private fun resetTimer() {
        // Resetting UI elements and visibility
        timerTextView.text = getString(R.string.default_tabata_timer)
        elapsedIntervalTextView.visibility = View.GONE
        timerLayoutView.background = getDrawable(R.drawable.background_dark_circle)
        //typeTextView.setTextColor(Color.rgb(60,60,70))
        typeTextView.text = getString(R.string.get_ready)

        minutesPicker.visibility = View.VISIBLE
        workIntervalInSecondsPicker.visibility = View.VISIBLE
        restIntervalInSecondsPicker.visibility = View.VISIBLE

        minutesTextView.text= getString(R.string.minutes)
        workTextView.text= getString(R.string.work)
        restTextView.text= getString(R.string.rest)

        startStopButton.setImageResource(R.drawable.ic_arrow_forward_green) // Reset button icon
        timerRunning = false
    }
    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Complete!")
        builder.setPositiveButton("OK") { dialog, _ ->

            dialog.dismiss()
            stopRingtone()
            stopVibration()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun startRingtone() {
        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        alarmRingtone = RingtoneManager.getRingtone(this, defaultRingtoneUri)
        alarmRingtone?.play()
    }

    private fun startVibration() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.let { vibratorService ->
            if (vibratorService.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibratorService.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 1000), 0))
                } else {
                    @Suppress("DEPRECATION")
                    vibratorService.vibrate(longArrayOf(0, 1000), 0)
                }
            }
        }
    }

    private fun stopRingtone() {
        alarmRingtone?.stop()
    }

    private fun stopVibration() {
        vibrator?.cancel()
    }
    override fun onPause() {
        super.onPause()
        if (timerRunning) {
            stopTimer()
        }
    }
}