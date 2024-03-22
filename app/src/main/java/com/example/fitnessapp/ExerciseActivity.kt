package com.example.fitnessapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        //Define a list to hold the recipes
        val exercises = mutableListOf<Exercise>()

        //Define the values to be retrieved from EditText
        val nameEditText = findViewById<EditText>(R.id.name)
        val muscleGroupEditText = findViewById<EditText>(R.id.muscleGroup)
        val instructionsEditText = findViewById<EditText>(R.id.instructions)

        //Define the list (recyclerView) to be displayed
        val exerciseRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)
        //Define the adapter from RecipeAdapter.kt
        val adapter = ExerciseAdapter(exercises)

        /* if HORIZONTAL orientation is desired use the below commented
        out assignment and replace LinearLayoutManager with layoutManager */
        //val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseRecyclerView.layoutManager = LinearLayoutManager(this)
        exerciseRecyclerView.adapter = adapter

        //Set a click listener on a button to add recipe
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener{
            val name = nameEditText.text.toString()
            val muscleGroup = muscleGroupEditText.text.toString()
            val instructions = instructionsEditText.text.toString()

            val exercise = Exercise(name, muscleGroup, instructions)
            exercises.add(exercise)

            //Notify the adapter that the data set has changed
            adapter.notifyItemInserted(exercises.size - 1)
            //clear editText fields
            nameEditText.text.clear()
            muscleGroupEditText.text.clear()
            instructionsEditText.text.clear()
        }

        //Back button
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

    }
}