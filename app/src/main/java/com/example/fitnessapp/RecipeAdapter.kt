package com.example.fitnessapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val email: String) : RecyclerView.Adapter<RecipeViewHolder>() {
    private val recipeDataManager = RecipeDataManager(email)
    private val imageDataManager = ImageDataManager(email)
    private var recipes = listOf<Recipe>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view, parent.context)
    }
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val fx = InteractionEffects()
        val recipe = recipes[position]
        holder.bind(this, recipe, position, imageDataManager)

        holder.itemView.setOnClickListener {
            fx.itemViewClickEffect(holder.itemView)
            val context = holder.itemView.context
            val dataManagerType = "recipes"
            val intent = Intent(context, ItemDetailsActivity::class.java).apply {
                putExtra("email", email)
                putExtra("dataManagerType", dataManagerType)
                putExtra("title", recipe.name)
                putExtra("subOneTitle", "Ingredients:")
                putExtra("subOneDetails", recipe.ingredients)
                putExtra("subTwoTitle", "Instructions:")
                putExtra("subTwoDetails", recipe.instructions)
            }
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return recipes.size
    }
    fun setRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }
    fun deleteRecipe(position: Int) {
        if (position < 0 || position >= recipes.size) {
            return // Invalid position
        }
        val recipeToDelete = recipes[position]
        recipes = recipes.toMutableList().also { it.removeAt(position) }
        notifyItemRemoved(position)
        recipeDataManager.deleteRecipe(recipeToDelete.id)
    }
}

class RecipeViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.name)
    private val itemPhoto: ImageView = itemView.findViewById(R.id.itemPhoto)
    private val ingredients: TextView = itemView.findViewById(R.id.ingredients)
    private val instructions: TextView = itemView.findViewById(R.id.instructions)
    private val position: TextView = itemView.findViewById(R.id.position)
    private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    private var adapter: RecipeAdapter? = null
    fun bind(adapter: RecipeAdapter, recipe: Recipe, position: Int, imageDataManager: ImageDataManager) {

        val positionText = String.format("%d", position + 1)
        this.position.text = positionText
        name.text = recipe.name
        ingredients.text = recipe.ingredients
        instructions.text = recipe.instructions
        imageDataManager.downloadRecipePhoto(recipe.name)?.addOnSuccessListener {uri->
            //itemPhoto.setImageURI(uri)
            if(absoluteAdapterPosition == position) {
                Glide.with(context)
                    .load(uri)
                    .into(itemPhoto)
                itemPhoto.visibility = View.VISIBLE
            }
        }?.addOnFailureListener{
            itemPhoto.visibility = View.GONE
        }

        this.adapter = adapter

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 1000 // Set the duration of the animation (in milliseconds)
        itemView.startAnimation(animation) // Start the animation

        deleteButton.setOnLongClickListener {
            adapter.deleteRecipe(absoluteAdapterPosition)
            true
        }
    }
}
