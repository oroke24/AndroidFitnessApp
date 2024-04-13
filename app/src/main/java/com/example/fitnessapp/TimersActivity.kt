package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity

class TimersActivity : ComponentActivity() {
    private lateinit var homeButton: ImageButton
    private lateinit var recipeButton: ImageButton
    private lateinit var exerciseButton: ImageButton
    private lateinit var calendarButton: ImageButton
    val fx = InteractionEffects()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        //Binding vals to buttons
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val stopWatch = findViewById<Button>(R.id.stopWatchButton)
        val timer = findViewById<Button>(R.id.timerButton)
        val tabataTimer = findViewById<Button>(R.id.tabataTimerButton)
        homeButton = findViewById(R.id.menuHomeButton)
        recipeButton = findViewById(R.id.menuRecipeButton)
        exerciseButton = findViewById(R.id.menuExerciseButton)
        calendarButton = findViewById(R.id.menuCalendarButton)
        val email = intent.getStringExtra("USER_EMAIL")?:""


        backButton.setOnClickListener {
            fx.imageButtonClickEffectQuick(backButton)
                finish()}

        homeButton.setOnClickListener{
            fx.imageButtonClickEffect(homeButton)
            intentWithEmail(MainActivity(), email)
        }
        recipeButton.setOnClickListener{
            fx.imageButtonClickEffect(recipeButton)
            intentWithEmail(RecipeActivity(), email)
        }
        exerciseButton.setOnClickListener{
            fx.imageButtonClickEffect(exerciseButton)
            intentWithEmail(ExerciseActivity(), email)
        }
        calendarButton.setOnClickListener{
            fx.imageButtonClickEffect(calendarButton)
            intentWithEmail(CalendarActivity(), email)
        }
        stopWatch.setOnClickListener {
            fx.buttonClickEffect(stopWatch)
            startActivity(Intent(this, StopWatchActivity::class.java))
        }
        timer.setOnClickListener {
            fx.buttonClickEffect(timer)
            startActivity(Intent(this, TimerActivity::class.java))
        }
        tabataTimer.setOnClickListener {
            fx.buttonClickEffect(tabataTimer)
            startActivity(Intent(this, TabataTimerActivity::class.java))
        }
    }
    private fun intentWithEmail(thisActivity: ComponentActivity, email: String) {
        val intent = Intent(this, thisActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        startActivity(intent)
    }
}