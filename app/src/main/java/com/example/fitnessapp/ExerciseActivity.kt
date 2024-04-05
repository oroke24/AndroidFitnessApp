package com.example.fitnessapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.hashMapOf

class ExerciseActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ExerciseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        db = FirebaseFirestore.getInstance()
        adapter = ExerciseAdapter()

        //Back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        val nameEditText = findViewById<EditText>(R.id.name)
        val muscleGroupEditText = findViewById<EditText>(R.id.muscleGroup)
        val instructionsEditText = findViewById<EditText>(R.id.instructions)

        val exerciseRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)

        loadExercises()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseRecyclerView.layoutManager = layoutManager
        exerciseRecyclerView.adapter = adapter


        //Set a click listener on a button to add recipe
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener{
            val name = nameEditText.text.toString()
            val muscleGroup = muscleGroupEditText.text.toString()
            val instructions = instructionsEditText.text.toString()

            val exercise = Exercise("", name, muscleGroup, instructions)

            adapter.notifyItemInserted(adapter.itemCount)
            addExerciseToDatabase(exercise)

            //clear editText fields
            nameEditText.text.clear()
            muscleGroupEditText.text.clear()
            instructionsEditText.text.clear()

            // Reload exercise after adding a new one
            loadExercises()
        }


    }
    private fun addExerciseToDatabase(exercise: Exercise){
        db.collection("exercises")
            .add(exercise)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Exercise added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{exception ->
                Log.w(TAG, "Error adding exercise", exception)
            }
    }
    private fun loadExercises(){
        db.collection("exercises")
            .get()
            .addOnSuccessListener { result ->
                //Define a list to hold the exercises
                val exercises = mutableListOf<Exercise>()
                for(document in result){
                    val id = document.id
                    val name = document.getString("name")?:""
                    val muscleGroup = document.getString("muscleGroup")?:""
                    val instructions = document.getString("instructions")?:""
                    exercises.add(Exercise(id,name,muscleGroup,instructions))
                }
                adapter.setExercises(exercises)
            }
            .addOnFailureListener{exception ->
                Toast.makeText(this, "Error Loading exercises",Toast.LENGTH_SHORT).show()
            }
    }
}