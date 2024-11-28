package com.example.fitnessapp.firestore

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.storage.FirebaseStorage

class ImageDataManager(val email: String) {
    private val userPhotos = FirebaseStorage.getInstance().reference.child("users/$email")
    private val recipePhotos = userPhotos.child("recipes")


    fun uploadRecipePhoto(uri: Uri, recipeName: String){
        recipePhotos.child(recipeName).putFile(uri)
    }
    fun downloadRecipePhoto(recipeName: String): Task<Uri>{
        val taskCompletionSource = TaskCompletionSource<Uri>()

        recipePhotos.child(recipeName).downloadUrl
            .addOnSuccessListener { uri->
                taskCompletionSource.setResult(uri)
            }
            .addOnFailureListener{ exception->
                taskCompletionSource.setException(exception)
            }
        return taskCompletionSource.task
    }
    fun downloadAllRecipePhotos(): Task<List<Uri>>{
        val taskCompletionSource = TaskCompletionSource<List<Uri>>()
        recipePhotos.listAll()
            .addOnSuccessListener { photos->
                val uris = mutableListOf<Uri>()
                for (item in photos.items){
                    item.downloadUrl
                        .addOnSuccessListener { uri ->
                            uris.add(uri)
                            if(uris.size == photos.items.size){
                                taskCompletionSource.setResult(uris)
                        }
                    }.addOnFailureListener{ exception->
                        taskCompletionSource.setException(exception)
                        }
                }
            }.addOnFailureListener{ exception->
               taskCompletionSource.setException(exception)
            }
        return taskCompletionSource.task
    }
    /*
    fun downloadRecipePhotos(): List<Task<Uri>>{
    }
     */
}