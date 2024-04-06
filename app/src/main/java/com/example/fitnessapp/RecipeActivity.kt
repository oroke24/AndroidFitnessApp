package com.example.fitnessapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.hashMapOf

class RecipeActivity : ComponentActivity() {
    //A database to hold the recipes
    private lateinit var db: FirebaseFirestore
    //An adapter for RecipeAdapter.kt
    private lateinit var adapter: RecipeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        db = FirebaseFirestore.getInstance()
        adapter = RecipeAdapter()

        //Back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        //Define the values to be retrieved from EditText
        val nameEditText = findViewById<EditText>(R.id.name)
        val ingredientsEditText = findViewById<EditText>(R.id.ingredients)
        val instructionsEditText = findViewById<EditText>(R.id.instructions)
        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"

        //Define the list (recyclerView) to be displayed
        val recipeRecyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)
        //Define the adapter from RecipeAdapter.kt
        //val adapter = RecipeAdapter(recipes)

        loadRecipes()
        /* if HORIZONTAL orientation is desired use the below commented
        out assignment and replace LinearLayoutManager with layoutManager */
        //val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //if  VERTICAL orientation is desired use this one
        //recipeRecyclerView.layoutManager = LinearLayoutManager(this)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recipeRecyclerView.layoutManager = layoutManager
        recipeRecyclerView.adapter = adapter


        //Set a click listener on a button to add recipe
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener{
            val name = nameEditText.text.toString()
            val ingredients = ingredientsEditText.text.toString()
            val instructions = instructionsEditText.text.toString()

            val recipe = Recipe(email,"", name, ingredients, instructions)

            adapter.notifyItemInserted(adapter.itemCount)
            addRecipeToDatabase(recipe)

            //clear editText fields
            nameEditText.text.clear()
            ingredientsEditText.text.clear()
            instructionsEditText.text.clear()

            // Reload recipes after adding a new one
            loadRecipes()
        }


    }
    private fun addRecipeToDatabase(recipe: Recipe){
        db.collection("recipes")
            .add(recipe)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Recipe added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{exception ->
                Log.w(TAG, "Error adding recipe", exception)
            }
    }
    private fun loadRecipes(){
        val myEmail = intent.getStringExtra("USER_EMAIL")?:"no user named"
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                //Define a list to hold the recipes
                val recipes = mutableListOf<Recipe>()
                for(document in result){
                    val email = document.getString("email")?:""
                    if(email == myEmail){
                        val id = document.id
                        val name = document.getString("name")?:""
                        val ingredients = document.getString("ingredients")?:""
                        val instructions = document.getString("instructions")?:""
                        recipes.add(Recipe(email, id, name, ingredients, instructions))
                    }
                }
                adapter.setRecipes(recipes)
            }
            .addOnFailureListener{exception ->
                Toast.makeText(this, "Error Loading recipes", Toast.LENGTH_SHORT).show()
            }
    }
}