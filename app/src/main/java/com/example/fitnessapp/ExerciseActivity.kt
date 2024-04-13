package com.example.fitnessapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.hashMapOf

class ExerciseActivity : ComponentActivity() {
    private lateinit var adapter: ExerciseAdapter
    private lateinit var homeButton: ImageButton
    private lateinit var recipeButton: ImageButton
    private lateinit var timersButton: ImageButton
    private lateinit var calendarButton: ImageButton
    val fx = InteractionEffects()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        val nameEditText = findViewById<EditText>(R.id.name)
        val muscleGroupEditText = findViewById<EditText>(R.id.muscleGroup)
        val instructionsEditText = findViewById<EditText>(R.id.instructions)
        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"
        val exerciseDataManager = ExerciseDataManager(email)
        val exerciseRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)
        homeButton = findViewById(R.id.menuHomeButton)
        recipeButton = findViewById(R.id.menuRecipeButton)
        timersButton = findViewById(R.id.menuTimersButton)
        calendarButton = findViewById(R.id.menuCalendarButton)

        adapter = ExerciseAdapter(email)
        loadExercises(exerciseDataManager)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseRecyclerView.layoutManager = layoutManager
        exerciseRecyclerView.adapter = adapter

        homeButton.setOnClickListener{
            fx.imageButtonClickEffect(homeButton)
            intentWithEmail(MainActivity(), email)
        }
        recipeButton.setOnClickListener{
            fx.imageButtonClickEffect(recipeButton)
            intentWithEmail(RecipeActivity(), email)
        }
        timersButton.setOnClickListener{
            fx.imageButtonClickEffect(timersButton)
            intentWithEmail(TimersActivity(), email)
        }
        calendarButton.setOnClickListener{
            fx.imageButtonClickEffect(calendarButton)
            intentWithEmail(CalendarActivity(), email)
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            fx.imageButtonClickEffect(backButton)
            finish()
        }

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener{
            fx.buttonClickEffect(addButton)
            if(nameEditText.text.isBlank()){
                Toast.makeText(this,"Recipe must have a name", Toast.LENGTH_LONG).show()
            }else {
                val name = nameEditText.text.toString()
                val muscleGroup = muscleGroupEditText.text.toString()
                val instructions = instructionsEditText.text.toString()
                val exercise = Exercise(name, name, muscleGroup, instructions)
                val context: Context = this
                var wasAdded: Boolean

                CoroutineScope(Dispatchers.Main).launch {
                    wasAdded = exerciseDataManager.addExerciseWithPermission(context, exercise)
                    if(wasAdded){
                        loadExercises(exerciseDataManager)
                        adapter.notifyDataSetChanged()
                        nameEditText.text.clear()
                        muscleGroupEditText.text.clear()
                        instructionsEditText.text.clear()
                    }
                }
            }
        }
    }


    private fun loadExercises(exerciseDataManager: ExerciseDataManager){
        CoroutineScope(Dispatchers.Main).launch {
            val exercises = exerciseDataManager.getAllExercises()
            val sortedExercises = exercises.sortedBy { it.name.lowercase() }
            adapter.setExercises(sortedExercises)
        }
    }
    private fun intentWithEmail(thisActivity: ComponentActivity, email: String) {
        val intent = Intent(this, thisActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        startActivity(intent)
    }
}