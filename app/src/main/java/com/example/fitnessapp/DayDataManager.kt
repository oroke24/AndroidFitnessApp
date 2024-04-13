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

    suspend fun addRecipeToDay(date: String, providedRecipeId: String) {
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
    suspend fun addExerciseToDay(date: String, providedExerciseId: String) {
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
        var thisDay = Day("", "", "", "")
        val thisDayDocRef = daysCollection.document(formattedDate).get().await()
        if(thisDayDocRef.exists()){
            val date = thisDayDocRef.getString(thisDayDocRef.id)?:""
            val recipeName = thisDayDocRef.getString("recipeId") ?: ""
            val exerciseName = thisDayDocRef.getString("exerciseId")?:""
            thisDay = Day("", date, recipeName, exerciseName)
        }
        return thisDay
    }
}