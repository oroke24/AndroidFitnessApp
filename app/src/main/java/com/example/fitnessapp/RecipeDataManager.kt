package com.example.fitnessapp

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipeDataManager(private val email: String) {
    private val db = FirebaseFirestore.getInstance()
    fun fetchUserRecipeIds(callback: (List<Pair<String, String>>) -> Unit) {
        db.collection("users").document(email).collection("recipes")
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
                Log.w("fetchUserRecipes", "Failure fethcing recipes")
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
        builder.show()
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
}