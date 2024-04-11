package com.example.fitnessapp

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeDataManager(private val email: String) {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val thisUser = usersCollection.document(email)
    private val thisUsersRecipes = thisUser.collection("recipes")
    private val fx = InteractionEffects()

    suspend fun getAllRecipes(): List<Recipe> {
        return try {
            val recipesCollection = thisUsersRecipes.get().await()
            val recipes = mutableListOf<Recipe>()
            for(document in recipesCollection){
                val id = document.id
                val name = document.getString("name")?:""
                val ingredients = document.getString("ingredients")?:""
                val instructions = document.getString("instructions")?:""
                recipes.add(Recipe(id, name, ingredients, instructions))
            }
            recipes
        }catch(e: Exception) {
                Log.w(ContentValues.TAG, "Error loading recipes")
            emptyList()
            }
    }
    fun addRecipe(recipe: Recipe) {
        thisUsersRecipes.add(recipe)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, it.id)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error adding recipe: ${recipe.name}, $exception")
            }
    }
    suspend fun addRecipeWithPermission(context: Context, recipe: Recipe): Boolean {
        var isAdded = false
        val recipeName = recipe.name.lowercase()
        Log.d(ContentValues.TAG,"addRecipe: recipeID: ${recipe.name}")
        return try {
            val existingRecipeDoc = thisUsersRecipes.document(recipeName).get().await()
            if (existingRecipeDoc.exists()) {
                val overwriteApproved = fx.userApprovesOverwrite(context, recipeName)
                if(overwriteApproved){
                    existingRecipeDoc.reference.update("ingredients", recipe.ingredients, "instructions", recipe.instructions).await()
                    isAdded = true
                    Toast.makeText(context,  "$recipeName updated!", Toast.LENGTH_LONG).show()
                }else{Toast.makeText(context, "Recipe NOT overwritten",Toast.LENGTH_LONG).show()}
            }else{
                val newRecipeData = hashMapOf(
                    "name" to recipe.name,
                    "ingredients" to recipe.ingredients,
                    "instructions" to recipe.instructions
                )
                thisUsersRecipes.document(recipeName).set(newRecipeData).await()
                isAdded = true
                Toast.makeText(context, "$recipeName added!", Toast.LENGTH_LONG).show()
            }
            isAdded
        }catch(e: Exception){
            Log.e("addExerciseToDay: Error", "Error: ${e.message}")
            isAdded
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
    suspend fun getNameFromId(recipeId: String): String {
        return try {
            val documentSnapshot = thisUsersRecipes.document(recipeId).get().await()
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
}