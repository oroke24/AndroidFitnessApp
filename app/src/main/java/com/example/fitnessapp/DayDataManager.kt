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

    suspend fun addRecipeToDay(date: String, providedRecipeId: String, slot: Int) {
        Log.d("addRecipeToDay: date value", date)

        val recipeSlot: String = when(slot){
            1 -> "recipe1Id"
            2 -> "recipe2Id"
            3 -> "recipe3Id"
            4 -> "recipe4Id"
            else -> "recipe5Id"
        }
        try{
            val existingDayDoc = daysCollection.document(date).get().await()
            if(existingDayDoc.exists()){
                existingDayDoc.reference.update(recipeSlot, providedRecipeId).await()
            }else{
                val newDayData = hashMapOf(
                    recipeSlot to providedRecipeId,
                )
                daysCollection.document(date).set(newDayData).await()
            }

        }catch(e: Exception){
           Log.e("addRecipeToDay: Error", "Error: ${e.message}")
        }
    }
    suspend fun addExerciseToDay(date: String, providedExerciseId: String, slot: Int) {
        Log.d("addExerciseToDay: date value", date)
        val exerciseSlot: String = when(slot){
            1 -> "exercise1Id"
            2 -> "exercise2Id"
            3 -> "exercise3Id"
            4 -> "exercise4Id"
            else -> "exercise5Id"
        }
        try {
            val existingDayDoc = daysCollection.document(date).get().await()
            if (existingDayDoc.exists()) {
                existingDayDoc.reference.update(exerciseSlot, providedExerciseId).await()
            }else{
                val newDayData = hashMapOf(
                    exerciseSlot to providedExerciseId
                )
                daysCollection.document(date).set(newDayData).await()
            }
        }catch(e: Exception){
            Log.e("addExerciseToDay: Error", "Error: ${e.message}")
        }
    }
    suspend fun emptyRecipeIdFromDay(formattedDate: String){
        val thisDayDocRef = daysCollection.document(formattedDate).get().await()
        if(thisDayDocRef.exists()){
           thisDayDocRef.reference.update("recipeId","")
        }
    }
    suspend fun emptyExerciseIdFromDay(formattedDate: String){
        val thisDayDocRef = daysCollection.document(formattedDate).get().await()
        if(thisDayDocRef.exists()){
            thisDayDocRef.reference.update("exerciseId","")
        }
    }

    suspend fun getDayFromDate(formattedDate: String): Day {
        var thisDay = Day("", "", "", "", "","","","","", "", "","")
        val thisDayDocRef = daysCollection.document(formattedDate).get().await()
        if(thisDayDocRef.exists()){
            val date = thisDayDocRef.getString(thisDayDocRef.id)?:""
            val recipe1Name = thisDayDocRef.getString("recipe1Id") ?:""
            val recipe2Name = thisDayDocRef.getString("recipe2Id") ?:""
            val recipe3Name = thisDayDocRef.getString("recipe3Id") ?:""
            val recipe4Name = thisDayDocRef.getString("recipe4Id") ?:""
            val recipe5Name = thisDayDocRef.getString("recipe5Id") ?:""
            val exercise1Name = thisDayDocRef.getString("exercise1Id")?:""
            val exercise2Name = thisDayDocRef.getString("exercise2Id")?:""
            val exercise3Name = thisDayDocRef.getString("exercise3Id")?:""
            val exercise4Name = thisDayDocRef.getString("exercise4Id")?:""
            val exercise5Name = thisDayDocRef.getString("exercise5Id")?:""
            thisDay = Day("", date, recipe1Name, recipe2Name, recipe3Name, recipe4Name, recipe5Name, exercise1Name,  exercise2Name, exercise3Name, exercise4Name, exercise5Name)
        }
        return thisDay
    }
}