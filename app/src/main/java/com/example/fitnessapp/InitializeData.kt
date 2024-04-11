package com.example.fitnessapp

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class InitializeData(private val myEmail: String) {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    val thisUser = usersCollection.document(myEmail)

    fun begin(){
        addEmailToUsersIfNotExists()
    }
    private fun addEmailToUsersIfNotExists(){
        val email = hashMapOf(
            "email" to myEmail
        )
        thisUser.set(email, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Email document added or updated successfully, now loading initial data")
                addInitialRecipesForUser()
                addInitialExercisesForUser()
                addInitialDaysForUser()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding or updating email document", exception)
            }
    }
    private fun addInitialRecipesForUser(){
        val initialRecipes = listOf(
            hashMapOf(
                "id" to "",
                "name" to "Potato Bake",
                "ingredients" to "-5 Potatoes\n-12 Eggs\n-3 Bell Peppers\n-1-2lbs Meat\n" +
                        "-2 Tbsp Oil\n-2 Tbsp seasoning\n-1 Tbsp salt\n-Tortillas (optional)",
                "instructions" to "Chop potatoes and veggies.\n" +
                        "-Add to 1 gallon bag.\n" +
                        "-Add salt, seasonings, and oil to bag.\n" +
                        "-Shake until well seasoned, drain bag if needed.\n" +
                        "-Empty mix into baking pan and bake @ 450 for 45min.\n" +
                        "-Scramble then and cook eggs and meat.\n" +
                        "-pour scrambled eggs and meat evenly over potatos and veggies.\n" +
                        "-Wrap in tortillas and freeze if desired."
            ),
            hashMapOf(
                "id" to "",
                "name" to "Penne Pasta",
                "ingredients" to "-Penne Pasta noodles\n-Sauce(of choice)\n-1-2lbs Ground Beef",
                "instructions" to "-Boil Pasta.\n-Cook Meat.\n-Once meat is cooked, add sauce\n" +
                        "-Mix meat sauce with pasta if desired"
            )
        )

        //now adding initialRecipes to db
        val thisUsersRecipes = thisUser.collection("recipes")
        initialRecipes.forEach { recipe ->
            thisUsersRecipes.add(recipe)
                .addOnSuccessListener { documentReference ->
                    documentReference.update("id", documentReference.id)
                    println("Recipe added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { exception ->
                    println("Error adding recipe: $exception")
                }
        }
    }
    private fun addInitialExercisesForUser(){
        val initialRecipes = listOf(
            hashMapOf(
                "id" to "",
                "name" to "Hypertrophy",
                "muscleGroup" to "Chest, Arms, Back",
                "instructions" to "-BB bench: 4 x 8-10 Slow eccentric.\n" +
                        "-Incline DB bench: 4 x 12-15 slow eccentric.\n" +
                        "-3 x superset of: Bar dips (6-8) and Incline seated DB Curls (8-10).\n" +
                        "-3 x superset of: Cable ext (8-10) and close grip (curl bar) curls (12-15).\n" +
                        "-Flat bar tricep pull downs 3 x 12-15.\n" +
                        "-(core): rope pull downs "
            ),
            hashMapOf(
                "id" to "",
                "name" to "High Intensity (HIIT)",
                "muscleGroup" to "Whole Body",
                "instructions" to "-4 x 50 KB swings.\n" +
                        "-4 x 20 KB bob and weaves.\n" +
                        "-4 minute tabata burpees (20work/10rest).\n" +
                        "-3 x 8-10 pullups.\n" +
                        "-3 x 8-10 chinups.\n" +
                        "-(core) 4 x 12-15 alternating: V-ups and heel touches."
            )
        )
        //now adding initialExercises to db
        val thisUsersExercises = thisUser.collection("exercises")
        initialRecipes.forEach { exercise ->
            thisUsersExercises.add(exercise)
                .addOnSuccessListener { documentReference ->
                    documentReference.update("id", documentReference.id)
                    Log.d("Init Recipe Added","Exercise added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { exception ->
                    Log.w("Init Recipe Error","Error adding Exercise: $exception")
                }
        }
    }
    private fun addInitialDaysForUser(){
        val initialDays = listOf( hashMapOf(
                "id" to "",
                "date" to "yyyyMMdd",
                "recipeId" to "(exampleId1) eSKareilseifH..."
            ))
        //now adding initialDays to db
        val thisUsersDays = thisUser.collection("days")
        initialDays.forEach{ day ->
            thisUsersDays.add(day)
                .addOnSuccessListener { documentReference ->
                    documentReference.update("id", documentReference.id)
                    Log.d("Init Day Added", "Day added with ID: ${documentReference.id}")
                }
                .addOnFailureListener{exception ->
                    Log.w("Init Day Error", "Error adding Day: $exception")
                }
        }
    }
}