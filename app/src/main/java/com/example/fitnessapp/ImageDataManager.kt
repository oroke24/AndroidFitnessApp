package com.example.fitnessapp

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage

class ImageDataManager(val email: String) {
    private val userPhotos = FirebaseStorage.getInstance().reference.child("users/$email")
    private val recipePhotos = userPhotos.child("recipes")

    fun uploadRecipePhoto(uri: Uri, recipeName: String){
        recipePhotos.child(recipeName).putFile(uri)
    }
    fun downloadRecipePhoto(recipeName: String): Task<Uri>?{
        return try{
            recipePhotos.child(recipeName).downloadUrl
        }catch(e: Exception){
            null
        }
    }

}