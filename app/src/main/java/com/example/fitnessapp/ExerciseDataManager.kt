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

    /* ToDo: finish converting from recipe to exercise
    fun addRecipe(recipe: Recipe){
        thisUsersRecipes
            .add(recipe)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "Recipe added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{exception ->
                Log.w(ContentValues.TAG, "Error adding recipe", exception)
            }
    }
    fun fetchUserRecipeIds(callback: (List<Pair<String, String>>) -> Unit) {
        thisUsersRecipes
            .get()
            .addOnSuccessListener { documents ->
                val recipes = mutableListOf<Pair<String, String>>()
                for (document in documents) {
                    val recipeId = document.id
                    val recipeName = document.getString("name") ?: ""
                    recipes.add(Pair(recipeId, recipeName))
                }
                callback(recipes)
            }
            .addOnFailureListener { exception ->
                Log.w("fetchUserRecipes", "Failure fetching recipes")
                // Handle error
            }
    }
    fun showRecipeSelectionDialog(context: Context, recipes: List<Pair<String, String>>, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select a Recipe")
        val recipeNames = recipes.map { it.second }.toTypedArray()
        builder.setItems(recipeNames) { dialog, which ->
            val selectedRecipeId = recipes[which].first
            callback(selectedRecipeId)
        }
        val dialog = builder.create()
        dialog.listView?.setBackground(Drawable.createFromPath("@drawable/cool_background"))
        dialog.show()
    }
    suspend fun getNameFromId(recipeId: String): String {
        return try {
            val documentSnapshot = db.collection("users").document(email)
                .collection("recipes").document(recipeId).get().await()
            documentSnapshot.getString("name") ?: ""
        } catch (e: Exception) {
            Log.e("getNameFromId", "Error getting recipe name: $e")
            ""
        }
    }
    fun deleteRecipe(recipeToDeleteId: String){
        thisUsersRecipes
            .document(recipeToDeleteId)
            .delete()
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Recipe with ID: $recipeToDeleteId deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error deleting recipe, id recieved: $recipeToDeleteId", exception)
            }
    }
     */

}