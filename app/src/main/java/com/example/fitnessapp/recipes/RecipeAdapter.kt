package com.example.fitnessapp.recipes

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.fitnessapp.InteractionEffects
import com.example.fitnessapp.ItemDetailsActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.firestore.ImageDataManager
import com.example.fitnessapp.firestore.RecipeDataManager

class RecipeAdapter(private val email: String) : RecyclerView.Adapter<RecipeViewHolder>() {
    private val recipeDataManager = RecipeDataManager(email)
    private val imageDataManager = ImageDataManager(email)
    private var recipes = listOf<Recipe>()
    private var recipePhotosMap: Map<String, Uri> = emptyMap()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view, parent.context)
    }
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val fx = InteractionEffects()
        val recipe = recipes[position]
        holder.bind(this, recipe, position, recipePhotosMap)

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
        setRecipePhotos()
    }
    private fun setRecipePhotos(){
        imageDataManager.downloadAllRecipePhotos()
            .addOnSuccessListener {uriList ->
                recipePhotosMap = uriList.mapNotNull{ uri->
                    val recipeName = uri.lastPathSegment?: return@mapNotNull null
                    recipeName to uri
                }.toMap()
                notifyDataSetChanged()
            }
            .addOnFailureListener{exception ->
                Log.e("RecipeAdapter", "Error downloading recipe photos: ${exception.message}")
            }

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
    fun bind(adapter: RecipeAdapter, recipe: Recipe, position: Int, recipePhotosMap: Map<String, Uri>) {
        val positionText = String.format("%d", position + 1)
        this.position.text = positionText
        name.text = recipe.name
        ingredients.text = recipe.ingredients
        instructions.text = recipe.instructions

        val recipePhotoUri = recipePhotosMap[recipe.name]
        Log.d("PhotoUri", "name: $name, recipePhotoUri: $recipePhotoUri")
        //itemPhoto.setImageURI(uri)
        if(recipePhotoUri != null) {
            Glide.with(context)
                .load(recipePhotoUri)
                .into(itemPhoto)
            itemPhoto.visibility = View.VISIBLE
        }else{
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
