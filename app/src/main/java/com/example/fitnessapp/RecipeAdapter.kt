package com.example.fitnessapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}

class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(recipe: Recipe) {
        //Bind recipe.name, recipe.ingredients, and recipe.instructions
        //to views in the item_recipe layout
        itemView.findViewById<TextView>(R.id.name).text = recipe.name
        itemView.findViewById<TextView>(R.id.ingredients).text = recipe.ingredients
        itemView.findViewById<TextView>(R.id.instructions).text = recipe.instructions
    }
}