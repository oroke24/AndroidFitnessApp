package com.example.fitnessapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeActivity : ComponentActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var adapter: RecipeAdapter
    private lateinit var homeButton: ImageButton
    private lateinit var exerciseButton: ImageButton
    private lateinit var timersButton: ImageButton
    private lateinit var calendarButton: ImageButton
    private lateinit var recipeDataManager: RecipeDataManager
    private lateinit var nameEditText: EditText
    private lateinit var ingredientsEditText: EditText
    private lateinit var instructionsEditText: EditText
    private lateinit var addButton: Button
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    val fx = InteractionEffects()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.menuHomeButton)
        exerciseButton = findViewById(R.id.menuExerciseButton)
        timersButton = findViewById(R.id.menuTimersButton)
        calendarButton = findViewById(R.id.menuCalendarButton)
        nameEditText = findViewById(R.id.name)
        ingredientsEditText = findViewById(R.id.ingredients)
        instructionsEditText = findViewById(R.id.instructions)
        addButton = findViewById(R.id.addButton)
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"
        recipeDataManager = RecipeDataManager(email)
        adapter = RecipeAdapter(email)
        loadRecipes(recipeDataManager)

        recipeRecyclerView.layoutManager = layoutManager
        recipeRecyclerView.adapter = adapter

        backButton.setOnClickListener {
            fx.imageButtonClickEffect(backButton)
            finish()
        }

        homeButton.setOnClickListener{
            fx.imageButtonClickEffect(homeButton)
            intentWithEmail(MainActivity(), email)
        }
        exerciseButton.setOnClickListener{
            fx.imageButtonClickEffect(exerciseButton)
            intentWithEmail(ExerciseActivity(), email)
        }
        timersButton.setOnClickListener{
            fx.imageButtonClickEffect(timersButton)
            intentWithEmail(TimersActivity(), email)
        }
        calendarButton.setOnClickListener{
            fx.imageButtonClickEffect(calendarButton)
            intentWithEmail(CalendarActivity(), email)
        }

        addButton.setOnClickListener{
            fx.buttonClickEffect(addButton)
            if(nameEditText.text.isBlank()){
                Toast.makeText(this,"Recipe must have a name", Toast.LENGTH_LONG).show()
            }else {
                val name = nameEditText.text.toString()
                val ingredients = ingredientsEditText.text.toString()
                val instructions = instructionsEditText.text.toString()
                val recipe = Recipe(name, name, ingredients, instructions)
                val context: Context = this
                var wasAdded: Boolean

                CoroutineScope(Dispatchers.Main).launch {
                    wasAdded = recipeDataManager.addRecipeWithPermission(context, recipe)
                    if(wasAdded){
                        loadRecipes(recipeDataManager)
                        adapter.notifyDataSetChanged()
                        nameEditText.text.clear()
                        ingredientsEditText.text.clear()
                        instructionsEditText.text.clear()
                        loadRecipes(recipeDataManager)
                    }
                }
                intentWithEmail(RecipeActivity(), email)
            }
        }
    }
    private fun loadRecipes(recipeDataManager: RecipeDataManager){
        CoroutineScope(Dispatchers.Main).launch {
            val recipes = recipeDataManager.getAllRecipes()
            val sortedRecipes = recipes.sortedBy{it.name.lowercase()}
            adapter.setRecipes(sortedRecipes)
        }
    }
    private fun intentWithEmail(thisActivity: ComponentActivity, email: String) {
        val intent = Intent(this, thisActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadRecipes(recipeDataManager)
        adapter.notifyDataSetChanged()
    }
}