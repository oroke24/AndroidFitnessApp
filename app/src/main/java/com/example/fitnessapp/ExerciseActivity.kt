package com.example.fitnessapp

import android.content.ContentValues.TAG
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        val fx = InteractionEffects()
        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"
        val exerciseDataManager = ExerciseDataManager(email)
        val exerciseRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)

        adapter = ExerciseAdapter(email)
        loadExercises(exerciseDataManager)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseRecyclerView.layoutManager = layoutManager
        exerciseRecyclerView.adapter = adapter

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            fx.imageButtonClickEffect(backButton)
            finish()
        }

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener{
            fx.buttonClickEffect(addButton)
            addExercise(exerciseDataManager)
            loadExercises(exerciseDataManager)
        }
    }
    private fun addExercise(exerciseDataManager: ExerciseDataManager){
        val nameEditText = findViewById<EditText>(R.id.name)
        val muscleGroupEditText = findViewById<EditText>(R.id.muscleGroup)
        val instructionsEditText = findViewById<EditText>(R.id.instructions)

        val name = nameEditText.text.toString()
        val muscleGroup = muscleGroupEditText.text.toString()
        val instructions = instructionsEditText.text.toString()

        val exercise = Exercise("", name, muscleGroup, instructions)

        adapter.notifyItemInserted(adapter.itemCount)
        exerciseDataManager.addExercise(exercise)

        //clear editText fields
        nameEditText.text.clear()
        muscleGroupEditText.text.clear()
        instructionsEditText.text.clear()

    }

    private fun loadExercises(exerciseDataManager: ExerciseDataManager){
        CoroutineScope(Dispatchers.Main).launch {
            val exercises = exerciseDataManager.getAllExercises()
            val sortedExercises = exercises.sortedBy { it.name.lowercase() }
            adapter.setExercises(sortedExercises)
        }
    }
}