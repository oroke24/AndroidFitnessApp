package com.example.fitnessapp.startup

import android.content.ContentValues.TAG
import android.util.Log
import com.example.fitnessapp.firestore.DayDataManager
import com.example.fitnessapp.userProfile.UserProfile
import com.example.fitnessapp.firestore.UserProfileDataManager
import com.example.fitnessapp.exercise.Exercise
import com.example.fitnessapp.firestore.ExerciseDataManager
import com.example.fitnessapp.recipes.Recipe
import com.example.fitnessapp.firestore.RecipeDataManager
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
    private val thisUser = usersCollection.document(myEmail)

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
                CoroutineScope(Dispatchers.Main).launch {
                    val firstRecipes: List<Recipe> = addInitialRecipesForUser()
                    val firstExercises: List<Exercise> = addInitialExercisesForUser()
                    for(i in 2..6){
                        addInitialDayForUser(firstRecipes[i].name, firstExercises[i].name, i-1)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding or updating email document", exception)
            }
        userProfileDataManager.addProfile(userProfile)
    }
    private fun addInitialRecipesForUser(): List<Recipe>{
        val initialRecipes = listOf(
            Recipe(
                "",
                "1) You can delete me!",
                "Cya!",
                "I'll be back"
            ),
            Recipe(
                "",
                "2) You can edit me",
                "-Think of these cards as your personal deck of recipes\n"+
                        "-Your deck is always sorted a-z\n"+
                        "-You can edit and duplicate any of your cards as you please!",
                "Following this card are some pre-made cards.  Enjoy!"
            ),
            Recipe(
                "",
                "Breakfast Toast",
                "-Banana\n"+
                        "-Peanut Butter\n"+
                        "-slice of Whole Wheat",
                "-Toast bread\n"+
                        "-Spread Peanut Butter\n"+
                        "-Slice banana and put on toast or eat separately"
            ),
            Recipe("",
                "Turkey sandwich",
                "-Wheat Bread\n-Sliced Turkey Breast\n-Mustard\n-Lettuce\n-Tomato",
                "-Slice/chop veggies, add between bread and enjoy!"
            ),
            Recipe(
                "",
                 "Penne Pasta",
                 "-Penne Pasta noodles\n-Sauce(of choice)\n-1-2lbs Ground Beef",
                 "-Boil Pasta.\n-Cook Meat.\n-Once meat is cooked, add sauce\n" +
                        "-Mix meat sauce with pasta if desired"
            ),
            Recipe(
                "",
                "Banana",
                "",
                ""
            ),
            Recipe(
                "",
                "Late night snack",
                "-Ice Cream",
                "-You know what to do here.."
            ),
            Recipe(
                "",
                "Cup of White Rice",
                "-1 cup of white rice",
                "-cook rice and ready!"
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
                        "-pour scrambled eggs and meat evenly over potatoes and veggies.\n" +
                        "-Wrap in tortillas and freeze if desired."
            ),
            Recipe(
                "",
                "Cheese Quesadilla",
                "-handful of cheese\n"+
                        "-1 flour tortilla",
                "-warm tortilla up\n"+
                        "sprinkle cheese on tortilla\n"+
                        "-continue warming until cheese melts\n"+
                        "-fold and ready"
            )
        )
        //now adding initialRecipes to db
        initialRecipes.forEach { recipe ->
            recipeDataManager.addRecipe(recipe)
        }
        return initialRecipes
    }
    private fun addInitialExercisesForUser(): List<Exercise>{
        val initialExercises = listOf(
            Exercise(
                "",
                "2) You can edit me",
                "-Think of these cards as your personal deck of exercises\n"+
                        "-Your deck is always sorted a-z\n"+
                        "-You can edit and duplicate any of your cards as you please!",
                "Following this card are some pre-made cards.  Enjoy!"
            ),
            Exercise(
                "",
                "Leg Day",
                "Legs, Lower Back",
                "-5 x 12-15 BB Squat slow eccentric\n"+
                        "-5 x 15 BB Romanian Dead-lift slow eccentric\n"+
                        "-4 x 20 DB Walking Lunges\n"+
                        "-3 x Superset of: leg extensions and leg curls\n"+
                        "-5 x 15-20 Weighted calf raises\n"+
                        "-End with core exercise of choice"
            ),
            Exercise(
                "",
                "Stretch",
                "Full Body Relaxed",
                "-Bend and Reach\n"+
                        "-Calf stretch\n"+
                        "-Quad stretch\n"+
                        "-Chest and Arm stretch\n"+
                        "-Roll wrists and neck"
            ),
            Exercise(
                "",
                "Short Walk",
                "Cardio",
                "Goal is at least half of a mile, any pace."
            ),
            Exercise(
                 "",
                 "Hypertrophy",
                 "Chest, Arms, Back",
                 "-BB bench: 4 x 8-10 Slow eccentric.\n" +
                        "-Incline DB bench: 4 x 12-15 slow eccentric.\n" +
                        "-3 x superset of: Bar dips (6-8) and Incline seated DB Curls (8-10).\n" +
                        "-3 x superset of: Cable ext (8-10) and close grip (curl bar) curls (12-15).\n" +
                        "-Flat bar tri-cep pull downs 3 x 12-15.\n" +
                        "-(core): rope pull downs "
            ),
            Exercise(
                "",
                "Long Walk",
                "Cardio",
                "-Goal is at least a mile and a half, any pace."
            ),
            Exercise(
                "",
                "Sauna",
                "Full Body Relaxed, Hot",
                "-Goal is at least 30 minutes of temp over 170."
            ),
            Exercise(
                "",
                "Run",
                "Cardio, Legs, Back, Core",
                "-Goal is at least 8 miles at an average of less than 10 minutes per mile."
            ),
            Exercise(
                "",
                "High Intensity (H.I.I.T)",
                "Whole Body",
                "-4 x 50 KB swings.\n" +
                        "-4 x 20 KB bob and weaves.\n" +
                        "-4 minute Tabata burpees (20work/10rest).\n" +
                        "-3 x 8-10 pull-ups.\n" +
                        "-3 x 8-10 chin-ups.\n" +
                        "-(core) 4 x 12-15 alternating: V-ups and heel touches."
            ),
            Exercise(
                "",
                "Tabata Core",
                "Core",
                        "-4 minutes(20/10 work/rest) russian twists\n" +
                        "-4 minutes(20/10 work/rest) alternating vUps and hell touches\n"
            ),
            Exercise(
                "",
                "Hill Sprints",
                "Legs, Fat burner",
                "-20 x sprintUp/walkDowns"
            )
        )
        //now adding initialExercises to db
        initialExercises.forEach { exercise ->
            exerciseDataManager.addExercise(exercise)
        }
        return initialExercises
    }
    private suspend fun addInitialDayForUser(recipeId: String, exerciseId:String, slot: Int){
        val dateOfRegistration = Date().time
        val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(dateOfRegistration))
        dayDataManager.addRecipeToDay(formattedDate, recipeId, slot)
        dayDataManager.addExerciseToDay(formattedDate, exerciseId, slot)
    }
}