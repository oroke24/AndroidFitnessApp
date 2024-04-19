package com.example.fitnessapp

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserProfileDataManager(private val email: String) {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val thisUser = usersCollection.document(email)
    private val thisUsersProfile = thisUser.collection("profile")
    private val profileInfo = thisUsersProfile.document("information")

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
}