package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var email: String
    private lateinit var homeMenuButton: ImageButton
    private lateinit var recipeMenuButton: ImageButton
    private lateinit var exerciseMenuButton: ImageButton
    private lateinit var timersMenuButton: ImageButton
    private lateinit var calendarMenuButton: ImageButton
    lateinit var todaysRecipeTextView : TextView
    lateinit var todaysExerciseTextView : TextView
    lateinit var usernameTextView: TextView
    lateinit var recipeAdapter: RecipeAdapter
    lateinit var exerciseAdapter: ExerciseAdapter
    lateinit var weeklyAdapter: WeeklyCalendarAdapter
    private lateinit var recipeDataManager: RecipeDataManager
    private lateinit var exerciseDataManager: ExerciseDataManager
    private lateinit var userProfileDataManager: UserProfileDataManager
    private lateinit var weeklyDataManager: DayDataManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = intent.getStringExtra("USER_EMAIL") ?: ""
        val fx = InteractionEffects()

        usernameTextView = findViewById(R.id.usernameTextView)

        val todaySnapShotLayout = findViewById<LinearLayout>(R.id.todaySnapShotLayout)
        val recipeButton = findViewById<Button>(R.id.recipeButton)
        val exerciseButton = findViewById<Button>(R.id.exerciseButton)
        val logoutButton = findViewById<ImageButton>(R.id.logoutButton)
        val firebaseAuth = FirebaseAuth.getInstance()


        val recipeLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val exerciseLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val weeklyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val recipeRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)
        val exerciseRecyclerView = findViewById<RecyclerView>(R.id.exerciseRecyclerView)
        val weeklyRecyclerView = findViewById<RecyclerView>(R.id.weeeklyRecyclerView)
        homeMenuButton = findViewById(R.id.menuHomeButton)
        recipeMenuButton = findViewById(R.id.menuRecipeButton)
        exerciseMenuButton = findViewById(R.id.menuExerciseButton)
        timersMenuButton = findViewById(R.id.menuTimersButton)
        calendarMenuButton = findViewById(R.id.menuCalendarButton)
        recipeAdapter = RecipeAdapter(email)
        exerciseAdapter = ExerciseAdapter(email)
        weeklyAdapter = WeeklyCalendarAdapter(email, weeklyRecyclerView)
        recipeDataManager = RecipeDataManager(email)
        exerciseDataManager = ExerciseDataManager(email)
        userProfileDataManager = UserProfileDataManager(email)
        recipeRecyclerView.layoutManager = recipeLayoutManager
        exerciseRecyclerView.layoutManager = exerciseLayoutManager
        weeklyRecyclerView.layoutManager = weeklyLayoutManager
        recipeRecyclerView.adapter = recipeAdapter
        exerciseRecyclerView.adapter = exerciseAdapter
        weeklyRecyclerView.adapter = weeklyAdapter


        CoroutineScope(Dispatchers.Main).launch{
            usernameTextView.text = userProfileDataManager.getUsername()
        }

        usernameTextView.setOnClickListener {
            fx.itemViewClickEffect(usernameTextView)
            intentWithEmail(UserProfileActivity(), email)
        }

        logoutButton.setOnClickListener {
            fx.imageButtonClickEffect(logoutButton)
            firebaseAuth.signOut()
            intentWithEmail(LoginActivity(), email)
            finish()
        }
        todaySnapShotLayout.setOnClickListener{
            fx.itemViewClickEffect(todaySnapShotLayout)
            intentWithEmail(CalendarActivity(), email)
        }
        recipeButton.setOnClickListener {
            fx.buttonClickEffect(recipeButton)
            intentWithEmail(RecipeActivity(), email)
        }
        exerciseButton.setOnClickListener {
            fx.buttonClickEffect(exerciseButton)
            intentWithEmail(ExerciseActivity(), email)
        }
        homeMenuButton.setOnClickListener{
            fx.imageButtonClickEffect(homeMenuButton)
            intentWithEmail(MainActivity(), email)
        }
        recipeMenuButton.setOnClickListener{
            fx.imageButtonClickEffect(recipeMenuButton)
            intentWithEmail(RecipeActivity(), email)
        }
        exerciseMenuButton.setOnClickListener {
            fx.imageButtonClickEffect(exerciseMenuButton)
            intentWithEmail(ExerciseActivity(), email)
        }
        timersMenuButton.setOnClickListener{
            fx.imageButtonClickEffect(timersMenuButton)
            intentWithEmail(TimersActivity(), email)
        }
        calendarMenuButton.setOnClickListener{
            fx.imageButtonClickEffect(calendarMenuButton)
            intentWithEmail(CalendarActivity(), email)
        }
        loadWeek()
        loadRecipes(recipeDataManager)
        loadExercises(exerciseDataManager)

    }
    private fun loadExercises(exerciseDataManager:ExerciseDataManager){
        CoroutineScope(Dispatchers.Main).launch {
            val exercises = exerciseDataManager.getAllExercises()
            val sortedExercises = exercises.sortedBy{it.name.lowercase()}
            exerciseAdapter.setExercises(sortedExercises)
        }
    }
    private fun loadRecipes(recipeDataManager:RecipeDataManager){
        CoroutineScope(Dispatchers.Main).launch {
            val recipes = recipeDataManager.getAllRecipes()
            val sortedRecipes = recipes.sortedBy{it.name.lowercase()}
            recipeAdapter.setRecipes(sortedRecipes)
        }
    }
    private fun loadWeek() {
        val cal = Calendar.getInstance()

        val daysOfWeek = arrayListOf<Date>()
        for(i in 0 until 7){
            daysOfWeek.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        weeklyAdapter.updateData(daysOfWeek)
    }
    private fun intentWithEmail(thisActivity: ComponentActivity, email: String) {
        val intent = Intent(this, thisActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        loadRecipes(recipeDataManager)
        loadExercises(exerciseDataManager)
        loadWeek()
        CoroutineScope(Dispatchers.Main).launch{
            usernameTextView.text = userProfileDataManager.getUsername()
        }
    }

}