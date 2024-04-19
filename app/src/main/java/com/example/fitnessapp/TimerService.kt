package com.example.fitnessapp

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

/*
class TimerService: Service(){
    private var isTimerRunning = false
    private var secondsElapsed = 0
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    companion object {
        const val NOTIFICATION_ID = 1
    }
    override fun onCreate(){
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
        startForeground(NOTIFICATION_ID, createNotification())
        startTimer()
    }
    private fun createNotification(): NotificationCompat.Builder {
        val notificationTitle = "Timer Running"
        val notificationBody = "Timer is running in the background."

        return NotificationUtils.createNotification(this, notificationTitle, notificationBody)
    }

    private fun startTimer(){
        isTimerRunning = true
        runnable = Runnable{
            if(isTimerRunning){
                secondsElapsed++
                handler.postDelayed(runnable, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
        //TODO("Not yet implemented")
    }

    override fun onDestroy(){
        super.onDestroy()
        isTimerRunning = false
        handler.removeCallbacks(runnable)
    }
}

 */