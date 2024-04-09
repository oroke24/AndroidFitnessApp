package com.example.fitnessapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeActivity : ComponentActivity() {
    private lateinit var adapter: RecipeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val fx = InteractionEffects()
        val nameEditText = findViewById<EditText>(R.id.name)
        val ingredientsEditText = findViewById<EditText>(R.id.ingredients)
        val instructionsEditText = findViewById<EditText>(R.id.instructions)
        val addButton = findViewById<Button>(R.id.addButton)
        val recipeRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"
        val recipeDataManager = RecipeDataManager(email)
        adapter = RecipeAdapter(email,recipeDataManager)
        loadRecipes(recipeDataManager)

        recipeRecyclerView.layoutManager = layoutManager
        recipeRecyclerView.adapter = adapter

        backButton.setOnClickListener {
            fx.imageButtonClickEffect(backButton)
            finish()
        }

        addButton.setOnClickListener{
            fx.buttonClickEffect(addButton)
            val name = nameEditText.text.toString()
            val ingredients = ingredientsEditText.text.toString()
            val instructions = instructionsEditText.text.toString()
            val recipe = Recipe("", name, ingredients, instructions)

            adapter.notifyItemInserted(adapter.itemCount)
            recipeDataManager.addRecipe(recipe)

            nameEditText.text.clear()
            ingredientsEditText.text.clear()
            instructionsEditText.text.clear()

            loadRecipes(recipeDataManager)
        }
    }
    private fun loadRecipes(recipeDataManager: RecipeDataManager){
        CoroutineScope(Dispatchers.Main).launch {
            val recipes = recipeDataManager.getAllRecipes()
            val sortedRecipes = recipes.sortedBy{it.name.lowercase()}
            adapter.setRecipes(sortedRecipes)
        }
    }
}