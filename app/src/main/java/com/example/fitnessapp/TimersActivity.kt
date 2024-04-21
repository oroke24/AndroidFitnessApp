package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity

class TimersActivity : ComponentActivity() {
    private lateinit var homeButton: ImageButton
    private lateinit var recipeButton: ImageButton
    private lateinit var exerciseButton: ImageButton
    private lateinit var calendarButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var stopWatch: Button
    private lateinit var tabataTimer: Button
    val fx = InteractionEffects()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        backButton = findViewById(R.id.backButton)
        stopWatch = findViewById(R.id.stopWatchButton)
        tabataTimer = findViewById(R.id.tabataTimerButton)
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