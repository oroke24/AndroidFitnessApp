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
import kotlin.collections.hashMapOf

class ExerciseActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ExerciseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)



        val fx = InteractionEffects()
        val nameEditText = findViewById<EditText>(R.id.name)
        val muscleGroupEditText = findViewById<EditText>(R.id.muscleGroup)
        val instructionsEditText = findViewById<EditText>(R.id.instructions)
        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"

        val exerciseRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)

        db = FirebaseFirestore.getInstance()
        adapter = ExerciseAdapter(email)

        loadExercises(email)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseRecyclerView.layoutManager = layoutManager
        exerciseRecyclerView.adapter = adapter
        //Back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            fx.imageButtonClickEffect(backButton)
            finish() }
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener{
            fx.buttonClickEffect(addButton)
            val name = nameEditText.text.toString()
            val muscleGroup = muscleGroupEditText.text.toString()
            val instructions = instructionsEditText.text.toString()

            val exercise = Exercise("", name, muscleGroup, instructions)

            adapter.notifyItemInserted(adapter.itemCount)
            addExerciseToDatabase(exercise, email)

            //clear editText fields
            nameEditText.text.clear()
            muscleGroupEditText.text.clear()
            instructionsEditText.text.clear()

            loadExercises(email)
        }


    }
    private fun addExerciseToDatabase(exercise: Exercise, myEmail: String){
        val usersCollection = db.collection("users")
        val thisUser = usersCollection.document(myEmail)
        val thisUsersExercises = thisUser.collection("exercises")
        thisUsersExercises
            .add(exercise)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Exercise added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{exception ->
                Log.w(TAG, "Error adding exercise", exception)
            }
    }
    private fun loadExercises(myEmail: String){
        val usersCollection = db.collection("users")
        val thisUser = usersCollection.document(myEmail)
        val thisUsersRecipes = thisUser.collection("exercises")
        thisUsersRecipes
            .get()
            .addOnSuccessListener { result ->
                //Define a list to hold the recipes
                val exercises = mutableListOf<Exercise>()
                for(document in result){
                    val id = document.id
                    val name = document.getString("name")?:""
                    val muscleGroup = document.getString("muscleGroup")?:""
                    val instructions = document.getString("instructions")?:""
                    exercises.add(Exercise(id, name, muscleGroup, instructions))
                }
                val sortedExercises = exercises.sortedBy{it.name.lowercase()}
                adapter.setExercises(sortedExercises)
            }
            .addOnFailureListener{exception ->
                Toast.makeText(this, "Error Loading recipes", Toast.LENGTH_SHORT).show()
            }
    }
}