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

    suspend fun addRecipetoDay(providedDate: Date, providedRecipeId: String) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(providedDate)

        Log.w("addRecipetoDay date value", "$date")
        val existingDayQuery = daysCollection.whereEqualTo("date", date).get().await()

        if (!existingDayQuery.isEmpty) {
            val existingDayDoc = existingDayQuery.documents.first()
            existingDayDoc.reference.update("recipeId", providedRecipeId).await()
        } else {
            val newDayData = hashMapOf(
                "date" to date,
                "recipeId" to providedRecipeId
            )
            daysCollection.add(newDayData).await()
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