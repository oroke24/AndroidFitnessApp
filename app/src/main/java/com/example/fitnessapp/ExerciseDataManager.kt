package com.example.fitnessapp

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExerciseDataManager(private val email: String) {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val thisUser = usersCollection.document(email)
    private val thisUsersExercises = thisUser.collection("exercises")

    suspend fun getAllExercises(): List<Exercise> {
        return try {
            val exerciseCollection = thisUsersExercises.get().await()
            val exercises = mutableListOf<Exercise>()
            for(document in exerciseCollection){
                val id = document.id
                val name = document.getString("name")?:""
                val muscleGroup = document.getString("muscleGroup")?:""
                val instructions = document.getString("instructions")?:""
                exercises.add(Exercise(id, name, muscleGroup, instructions))
            }
            exercises
        }catch(e: Exception) {
            Log.w(ContentValues.TAG, "Error loading exercises")
            emptyList()
        }
    }

    fun addExercise(exercise: Exercise){
        thisUsersExercises
            .add(exercise)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "Exercise added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{exception ->
                Log.w(ContentValues.TAG, "Error adding Exercise", exception)
            }
    }
    fun deleteExercise(exerciseToDeleteId: String){
        thisUsersExercises
            .document(exerciseToDeleteId)
            .delete()
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Recipe with ID: $exerciseToDeleteId deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error deleting exercise, id recieved: $exerciseToDeleteId", exception)
            }
    }
    fun fetchUserExerciseIds(callback: (List<Pair<String, String>>) -> Unit) {
        thisUsersExercises
            .get()
            .addOnSuccessListener { documents ->
                val exercises = mutableListOf<Pair<String, String>>()
                for (document in documents) {
                    val exerciseId = document.id
                    val exerciseName = document.getString("name") ?: ""
                    exercises.add(Pair(exerciseId, exerciseName))
                }
                callback(exercises)
            }
            .addOnFailureListener { exception ->
                Log.w("fetchUserExercises", "Failure fetching Exercises")
                // Handle error
            }
    }
    fun showExerciseSelectionDialog(context: Context, exercises: List<Pair<String, String>>, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select an Exercise")
        val exerciseNames = exercises.map { it.second }.toTypedArray()
        builder.setItems(exerciseNames) { dialog, which ->
            val selectedExerciseId = exercises[which].first
            callback(selectedExerciseId)
        }
        builder.show()
    }
    suspend fun getNameFromId(exerciseId: String): String {
        return try {
            val documentSnapshot = thisUsersExercises.document(exerciseId).get().await()
            documentSnapshot.getString("name") ?: ""
        } catch (e: Exception) {
            Log.e("getNameFromId", "Error getting exercise name: $e")
            ""
        }
    }

}