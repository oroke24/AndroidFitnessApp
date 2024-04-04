package com.example.fitnessapp
import android.app.AlertDialog
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

class TimerActivity : ComponentActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var hoursPicker: NumberPicker
    private lateinit var minutesPicker: NumberPicker
    private lateinit var secondsPicker: NumberPicker
    private lateinit var startButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var continueButton: ImageButton
    private lateinit var timerTextView: TextView
    private var countDownTimer: CountDownTimer? = null
    private var timeRemaining: Long = 0
    private var timerRunning = false
    private var alarmRingtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // Initialize views
        hoursPicker = findViewById(R.id.hoursPicker)
        minutesPicker = findViewById(R.id.minutesPicker)
        secondsPicker = findViewById(R.id.secondsPicker)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        continueButton = findViewById(R.id.continueButton)
        timerTextView = findViewById(R.id.timerTextView)
        backButton = findViewById(R.id.backButton)

        // Set initial values for NumberPickers
        hoursPicker.minValue = 0
        hoursPicker.maxValue = 23
        hoursPicker.value = 0

        minutesPicker.minValue = 0
        minutesPicker.maxValue = 59
        minutesPicker.value = 0

        secondsPicker.minValue = 0
        secondsPicker.maxValue = 59
        secondsPicker.value = 0

        // Set long press interval for quick display
        hoursPicker.setOnLongPressUpdateInterval(100)
        minutesPicker.setOnLongPressUpdateInterval(100)
        secondsPicker.setOnLongPressUpdateInterval(100)

        backButton.setOnClickListener{finish()}
        // Set up start button click listener
        startButton.setOnClickListener {
            if (!timerRunning) {
                val hours = hoursPicker.value
                val minutes = minutesPicker.value
                val seconds = secondsPicker.value

                val totalMilliseconds = ((hours * 3600) + (minutes * 60) + seconds) * 1000L
                timeRemaining = totalMilliseconds

                startTimer(totalMilliseconds)
            }
        }

        // Set up pause button click listener
        pauseButton.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
            }
        }

        // Set up continue button click listener
        continueButton.setOnClickListener {
            if (!timerRunning) {
                resumeTimer()
            }
        }
    }

    private fun startTimer(totalMilliseconds: Long) {
        countDownTimer?.cancel() // Cancel previous timer if running

        countDownTimer = object : CountDownTimer(totalMilliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                timeRemaining = 0
                timerRunning = false
                updateTimerDisplay()
                showDialog()
                playDefaultAlarmRingtone()
                startVibration()
            }
        }.start()

        timerRunning = true
    }

    private fun resetTimer(){
        countDownTimer?.cancel()
        timerRunning = false

    }
    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
    }

    private fun resumeTimer() {
        startTimer(timeRemaining)
    }

    private fun updateTimerDisplay() {
        val hours = timeRemaining / 1000 / 3600
        val minutes = (timeRemaining / 1000 % 3600) / 60
        val seconds = (timeRemaining / 1000) % 60

        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        timerTextView.text = timeString
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

    private fun playDefaultAlarmRingtone() {
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
}