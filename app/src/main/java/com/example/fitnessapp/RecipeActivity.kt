package com.example.fitnessapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecipeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        //Define a list to hold the recipes
        val recipes = mutableListOf<Recipe>()

        //Define the values to be retrieved from EditText
        val nameEditText = findViewById<EditText>(R.id.name)
        val ingredientsEditText = findViewById<EditText>(R.id.ingredients)
        val instructionsEditText = findViewById<EditText>(R.id.instructions)

        //Define the list (recyclerView) to be displayed
        val recipeRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)
        //Define the adapter from RecipeAdapter.kt
        val adapter = RecipeAdapter(recipes)

        /* if HORIZONTAL orientation is desired use the below commented
        out assignment and replace LinearLayoutManager with layoutManager */
        //val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recipeRecyclerView.layoutManager = LinearLayoutManager(this)
        recipeRecyclerView.adapter = adapter

        //Set a click listener on a button to add recipe
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener{
            val name = nameEditText.text.toString()
            val ingredients = ingredientsEditText.text.toString()
            val instructions = instructionsEditText.text.toString()

            val recipe = Recipe(name, ingredients, instructions)
            recipes.add(recipe)

            //Notify the adapter that the data set has changed
            adapter.notifyItemInserted(recipes.size - 1)
            //clear editText fields
            nameEditText.text.clear()
            ingredientsEditText.text.clear()
            instructionsEditText.text.clear()
        }

        //Back button
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }


    }
}