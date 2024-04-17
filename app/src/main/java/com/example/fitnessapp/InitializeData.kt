package com.example.fitnessapp

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InitializeData(private val userProfile: UserProfile) {
    private val myEmail = userProfile.email
    private val exerciseDataManager = ExerciseDataManager(myEmail)
    private val recipeDataManager = RecipeDataManager(myEmail)
    private val dayDataManager = DayDataManager(myEmail)
    private val userProfileDataManager = UserProfileDataManager(myEmail)

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
                val firstRecipeId = addInitialRecipesForUser()
                val firstExerciseId = addInitialExercisesForUser()
                addInitialDayForUser(firstRecipeId, firstExerciseId)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding or updating email document", exception)
            }
        userProfileDataManager.addProfile(userProfile)
    }
    private fun addInitialRecipesForUser(): String{
        val initialRecipes = listOf(
            Recipe(
                "",
                "1) You can delete me!",
                "C'ya!",
                "I'll be back"
            ),
            Recipe(
                "",
                "2) You can edit me",
                "-Think of these cards as your personal deck of recipes\n"+
                        "-Your deck is always sorted a-z",
                "Following this card are two premade cards.  Enjoy!"
            ),
            Recipe(
                "",
                "Potato Bake",
                "-5 Potatoes\n-12 Eggs\n-3 Bell Peppers\n-1-2lbs Meat\n" +
                        "-2 Tbsp Oil\n-2 Tbsp seasoning\n-1 Tbsp salt\n-Tortillas (optional)",
                "Chop potatoes and veggies.\n" +
                        "-Add to 1 gallon bag.\n" +
                        "-Add salt, seasonings, and oil to bag.\n" +
                        "-Shake until well seasoned, drain bag if needed.\n" +
                        "-Empty mix into baking pan and bake @ 450 for 45min.\n" +
                        "-Scramble then and cook eggs and meat.\n" +
                        "-pour scrambled eggs and meat evenly over potatos and veggies.\n" +
                        "-Wrap in tortillas and freeze if desired."
            ),
            Recipe(
                "",
                 "Penne Pasta",
                 "-Penne Pasta noodles\n-Sauce(of choice)\n-1-2lbs Ground Beef",
                 "-Boil Pasta.\n-Cook Meat.\n-Once meat is cooked, add sauce\n" +
                        "-Mix meat sauce with pasta if desired"
            )
        )
        //now adding initialRecipes to db
        initialRecipes.forEach { recipe ->
            recipeDataManager.addRecipe(recipe)
        }
        return initialRecipes[0].name
    }
    private fun addInitialExercisesForUser(): String{
        val initialExercises = listOf(
            Exercise(
                "",
                "1) Me too!",
                "later alligator",
                "Back soon, I'll shall be"
            ),
            Exercise(
                "",
                "2) You can edit me",
                "-Think of these cards as your personal deck of exercises\n"+
                        "-Your deck is always sorted a-z",
                "Following this card are two premade cards.  Enjoy!"
            ),
            Exercise(
                 "",
                 "Hypertrophy",
                 "Chest, Arms, Back",
                 "-BB bench: 4 x 8-10 Slow eccentric.\n" +
                        "-Incline DB bench: 4 x 12-15 slow eccentric.\n" +
                        "-3 x superset of: Bar dips (6-8) and Incline seated DB Curls (8-10).\n" +
                        "-3 x superset of: Cable ext (8-10) and close grip (curl bar) curls (12-15).\n" +
                        "-Flat bar tricep pull downs 3 x 12-15.\n" +
                        "-(core): rope pull downs "
            ),
            Exercise(
                "",
                 "High Intensity (HIIT)",
                 "Whole Body",
                "-4 x 50 KB swings.\n" +
                        "-4 x 20 KB bob and weaves.\n" +
                        "-4 minute tabata burpees (20work/10rest).\n" +
                        "-3 x 8-10 pullups.\n" +
                        "-3 x 8-10 chinups.\n" +
                        "-(core) 4 x 12-15 alternating: V-ups and heel touches."
            )
        )
        //now adding initialExercises to db
        initialExercises.forEach { exercise ->
            exerciseDataManager.addExercise(exercise)
        }
        return initialExercises[0].name
    }
    private fun addInitialDayForUser(recipeId: String, exerciseId:String){
        val dateOfRegistration = Date().time
        val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(dateOfRegistration))
        CoroutineScope(Dispatchers.Main).launch {
            dayDataManager.addRecipeToDay(formattedDate, recipeId, 2)
            dayDataManager.addExerciseToDay(formattedDate, exerciseId, 1)
        }
    }

}