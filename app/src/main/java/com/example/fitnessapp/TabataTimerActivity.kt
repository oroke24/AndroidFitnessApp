package com.example.fitnessapp

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
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
    private lateinit var setCountTextView: TextView
    private var timerRunning: Boolean = false
    private var bufferTimerRunning: Boolean = false
    private lateinit var timer: CountDownTimer
    private lateinit var bufferTimer: CountDownTimer

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
        setCountTextView = findViewById(R.id.setCount)

        timerTextView.visibility = View.GONE
        elapsedIntervalTextView.visibility= View.GONE

        configureNumberPickers()

        checkBounds()
        minutesPicker.setOnValueChangedListener { _, _, _ ->
            checkBounds()
        }
        workIntervalInSecondsPicker.setOnValueChangedListener { _, _, _ ->
            checkBounds()
        }
        restIntervalInSecondsPicker.setOnValueChangedListener { _, _, _ ->
            checkBounds()
        }

        backButton.setOnClickListener{
            if(timerRunning){
                timer.cancel()
                timerRunning = false
            }
            if(bufferTimerRunning){
                bufferTimer.cancel()
                bufferTimerRunning = false
            }
            finish()
        }

        startStopButton.setOnClickListener {
            if (timerRunning) {
                resetTimer()
            } else {
                checkBounds()
                startBufferTimer()
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
    private fun checkBounds(){
        val minutes = minutesPicker.value * 60
        val workSeconds = workIntervalInSecondsPicker.value
        val restSeconds = restIntervalInSecondsPicker.value
        val modulus = minutes % (workSeconds + restSeconds)
        val pieces = minutes / (workSeconds + restSeconds)
        setCountTextView.text = "Sets: $pieces.$modulus"
        if(modulus == 0) startStopButton.visibility = View.VISIBLE
        else startStopButton.visibility = View.INVISIBLE
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
        minutesTextView.visibility = View.GONE
        workTextView.visibility = View.GONE
        restTextView.visibility = View.GONE
        startStopButton.visibility = View.GONE
        minutesPicker.visibility = View.GONE
        workIntervalInSecondsPicker.visibility = View.GONE
        restIntervalInSecondsPicker.visibility = View.GONE
        timerTextView.visibility = View.VISIBLE
        timerTextView.textSize = 200f
        val minutes = minutesPicker.value
        val workSeconds = workIntervalInSecondsPicker.value
        val restSeconds = restIntervalInSecondsPicker.value

        // Countdown from 10 seconds before starting the main timer
        bufferTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                timerTextView.text = secondsRemaining.toString()
            }
            override fun onFinish() {
                bufferTimer.cancel()
                bufferTimerRunning = false
                startTimer(minutes, workSeconds, restSeconds)
            }
        }
        bufferTimer.start()
        bufferTimerRunning = true

    }
    private fun startTimer(minutes: Int, workSeconds: Int, restSeconds: Int) {
        timerLayoutView.setBackgroundColor(Color.rgb(22,22,22))
        startStopButton.visibility = View.VISIBLE
        setCountTextView.visibility = View.VISIBLE
        elapsedIntervalTextView.visibility = View.VISIBLE
        val totalSeconds = minutes * 60L

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
                val setsRemaining = ((millisUntilFinished / 1000) / totalIntervalSeconds) + 1
                if (setsRemaining == 1L) setCountTextView.text = "Last One!! Finish Strong!"
                else setCountTextView.text = "$setsRemaining Sets Remaining"

                val isWorkInterval = elapsedIntervalSeconds < workSeconds

                if (isWorkInterval) {
                    timerLayoutView.background = getDrawable(R.drawable.background_tabata_work) // Work interval color
                    elapsedIntervalTextView.text = String.format("%02d", workSeconds - (elapsedIntervalSeconds % workSeconds))
                    typeTextView.text = getString(R.string.work)
                } else {
                    timerLayoutView.background = getDrawable(R.drawable.background_tabata_rest) // Rest interval color
                    elapsedIntervalTextView.text = String.format("%02d", restSeconds - (elapsedIntervalSeconds - workSeconds))
                    typeTextView.text = getString(R.string.rest)
                }
            }
            override fun onFinish() {
                resetTimer()
                timerLayoutView.background = getDrawable(R.drawable.background_dark_circle)
                //showDialog()
            }
        }

        timer.start()
        startStopButton.setImageResource(R.drawable.ic_stop_red_48_transparent)
        startStopButton.setBackgroundColor(Color.TRANSPARENT)
        timerRunning = true
    }

    private fun resetTimer() {
        // Resetting UI elements and visibility
        timerTextView.text = getString(R.string.default_tabata_timer)
        elapsedIntervalTextView.visibility = View.GONE
        timerLayoutView.background = getDrawable(R.drawable.background_dark_circle)
        //typeTextView.setTextColor(Color.rgb(60,60,70))
        typeTextView.text = getString(R.string.get_ready)

        minutesTextView.text= getString(R.string.minutes)
        workTextView.text= getString(R.string.work)
        restTextView.text= getString(R.string.rest)

        minutesTextView.visibility = View.VISIBLE
        workTextView.visibility = View.VISIBLE
        restTextView.visibility = View.VISIBLE

        minutesPicker.visibility = View.VISIBLE
        workIntervalInSecondsPicker.visibility = View.VISIBLE
        restIntervalInSecondsPicker.visibility = View.VISIBLE


        startStopButton.setImageResource(R.drawable.ic_arrow_forward_green) // Reset button icon
        timer.cancel()
        timerRunning = false
    }
    private fun showDialog() {
        val builder = AlertDialog.Builder(baseContext)
        builder.setTitle("Tabata Complete!")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

}