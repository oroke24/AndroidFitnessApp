package com.example.fitnessapp

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RecipeAdapter : RecyclerView.Adapter<RecipeViewHolder>(){
    private var recipes = listOf<Recipe>()

    //Variables for resizing item holderView because xml is largely static
    private var largestItemWidth = 0
    private var largestItemHeight = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(this, recipe,position)

        //Now using the resizing variables to resize holder if necessary
        holder.itemView.post{
            val width = holder.itemView.width
            val height = holder.itemView.height

            //check and update width
            if(width > largestItemWidth){
                largestItemWidth = width
            }
            //check and update width
            if(height > largestItemHeight ){
                largestItemHeight = height
            }
            //Reset layout parameters for all items to match largest
            val layoutParams = holder.itemView.layoutParams
            layoutParams.width = largestItemWidth
            layoutParams.height = largestItemHeight
            holder.itemView.layoutParams = layoutParams
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setRecipes(recipes: List<Recipe>){
        this.recipes = recipes
        notifyDataSetChanged()
    }
    fun deleteRecipe(position: Int){
        val recipeToDelete = recipes[position] // Retrieve the recipe to delete
        recipes = recipes.toMutableList().also { it.removeAt(position) } // Remove from the list
        notifyItemRemoved(position) // Notify the adapter of item removal

        // Delete the recipe from the database
        val db = FirebaseFirestore.getInstance()
        db.collection("recipes")
            .document(recipeToDelete.id) // Assuming each recipe has an ID
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Recipe deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error deleting recipe", exception)
            }
    }

}

class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.name)
    private val ingredients: TextView = itemView.findViewById(R.id.ingredients)
    private val instructions: TextView = itemView.findViewById(R.id.instructions)
    private val position: TextView = itemView.findViewById(R.id.position)
    private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    // Reference to the adapter to call deleteRecipe method
    private var adapter: RecipeAdapter? = null

    fun bind(adapter: RecipeAdapter,recipe: Recipe, position: Int) {
        //Bind position, recipe.name, recipe.ingredients, and recipe.instructions
        //to views in the item_recipe layout
        val positionText = String.format("%d",position + 1)
        this.position.text = positionText
        name.text = recipe.name
        ingredients.text = recipe.ingredients
        instructions.text = recipe.instructions

        //Setting adapter reference
        this.adapter = adapter
        //Setting listener for deleteButton
        deleteButton.setOnLongClickListener {
            adapter.deleteRecipe(absoluteAdapterPosition)
            true
        }
    }
}