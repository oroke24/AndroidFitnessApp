package com.example.fitnessapp.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AiDataManager(email: String) {
    private val db = FirebaseFirestore.getInstance()
    private val keys = db.collection("keys")
    suspend fun accessKey(): String {
        return try {
            val key = keys.document("openAiText").get().await()
            key.getString("key")?:""
        }catch(e: Exception){
            Log.e("AiDataManager.accessKey", "Error getting access key: $e")
            ""
        }
    }
}
