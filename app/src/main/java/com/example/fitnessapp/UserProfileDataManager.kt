package com.example.fitnessapp

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext

class UserProfileDataManager(private val email: String) {
    private val db = FirebaseFirestore.getInstance()
    private val adminComments = db.collection("admin").document("comments")
    private val usersCollection = db.collection("users")
    private val thisUser = usersCollection.document(email)
    private val thisUsersProfile = thisUser.collection("profile")
    private val profileInfo = thisUsersProfile.document("information")
    private lateinit var recipeDataManager: RecipeDataManager
    private lateinit var exerciseDataManager: ExerciseDataManager
    private lateinit var dayDataManager: DayDataManager

    fun addProfile(userProfile: UserProfile){
       profileInfo.set(userProfile)
    }
    fun updateUsername(newName: String){
        profileInfo.update("username",newName)
    }
    suspend fun getUsername(): String{
        val myProfile = profileInfo.get().await()
        return myProfile.getString("username")?:""
    }
    fun addComment(newComment: String){
        val comment = hashMapOf(
            "comment" to newComment
        )
        adminComments.set(comment)
    }
    fun deleteAccount(){
        recipeDataManager = RecipeDataManager(email)
        exerciseDataManager = ExerciseDataManager(email)
        dayDataManager = DayDataManager(email)
        CoroutineScope(Dispatchers.Main).launch {
            recipeDataManager.deleteAllRecipes()
            exerciseDataManager.deleteAllExercises()
            dayDataManager.deleteAllDays()
            deleteProfile()
        }
    }
    private fun deleteProfile(){
        thisUsersProfile.document("information").delete()
        usersCollection.document(email).delete()
    }
}