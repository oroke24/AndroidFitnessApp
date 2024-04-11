package com.example.fitnessapp

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DayDataManager(private val email: String) {
    private val db = FirebaseFirestore.getInstance()
    private val user = db.collection("users").document(email)

    private val daysCollection = user.collection("days")
    private val recipesCollection = user.collection("recipes")
    private val exercisesCollection = user.collection("exercises")

    suspend fun addRecipeToDay(providedDate: Date, providedRecipeId: String) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(providedDate)
        Log.d("addRecipeToDay: date value", date)
        try{
            val existingDayDoc = daysCollection.document(date).get().await()
            if(existingDayDoc.exists()){
                existingDayDoc.reference.update("recipeId", providedRecipeId).await()
            }else{
                val newDayData = hashMapOf(
                    "recipeId" to providedRecipeId,
                    "exerciseId" to ""
                )
                daysCollection.document(date).set(newDayData).await()
            }

        }catch(e: Exception){
           Log.e("addRecipeToDay: Error", "Error: ${e.message}")
        }
    }
    suspend fun addExerciseToDay(providedDate: Date, providedExerciseId: String) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(providedDate)
        Log.d("addExerciseToDay: date value", date)
        try {
            val existingDayDoc = daysCollection.document(date).get().await()
            if (existingDayDoc.exists()) {
                existingDayDoc.reference.update("exerciseId", providedExerciseId).await()
            }else{
                val newDayData = hashMapOf(
                    "recipeId" to "",
                    "exerciseId" to providedExerciseId
                )
                daysCollection.document(date).set(newDayData).await()
            }
        }catch(e: Exception){
            Log.e("addExerciseToDay: Error", "Error: ${e.message}")
        }
    }
    suspend fun fetchRecipe(recipeId: String): Recipe{
        var recipe = Recipe("not found in fetchRecipe","","","")
        recipesCollection
            .get()
            .addOnSuccessListener { result ->
                for(document in result){
                    val idString = document.id
                    if(idString == recipeId){
                        val name = document.getString("name")?:""
                        val ingredients = document.getString("ingredients")?:""
                        val instructions = document.getString("instructions")?:""
                        recipe = Recipe(idString,name,ingredients,instructions)
                    }
                }
            }
            .addOnFailureListener{exception ->
                Log.w("DayDataManager::fetchRecipe","Error Loading recipes")
            }
       return recipe
    }
    suspend fun getRecipeIDForDay(date: String): String? {
        return try {
            val thisDaySnapshot = daysCollection.document(date).get().await()
            thisDaySnapshot.getString("recipeId")

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun setRecipeIDForDay(dayId: String, recipeId: String){
        try{
            daysCollection.document(dayId).update("recipeId", recipeId)
    }catch(e: Exception){
        e.printStackTrace()
        }
    }
}