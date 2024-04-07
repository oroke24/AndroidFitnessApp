package com.example.fitnessapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ExerciseAdapter(email: String) : RecyclerView.Adapter<ExerciseViewHolder>(){
    private var exercises = listOf<Exercise>()
    private var email = email

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(this, exercise, position, email)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val backgroundID = context.resources.getIdentifier("cool_background2", "drawable", context.packageName)
            val intent = Intent(context, ItemDetailsActivity::class.java).apply {
                putExtra("backgroundID", backgroundID)
                putExtra("title", exercise.name)

                putExtra("subOneTitle", "Muscle Group:")
                putExtra("subOneDetails", exercise.muscleGroup)

                putExtra("subTwoTitle", "Instructions:")
                putExtra("subTwoDetails", exercise.instructions)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    fun setExercises(exercises:List<Exercise>){
        this.exercises = exercises
        notifyDataSetChanged()
    }
    fun deleteExercise(position: Int, email: String){
        val exerciseToDelete = exercises[position] //Retrieve the exercise to delete
        exercises = exercises.toMutableList().also{it.removeAt(position)}//Remove from list
        notifyItemRemoved(position)//Notify the adapter of the removal

        //Delete the exercise from the database
        val db =  FirebaseFirestore.getInstance()
        db.collection("users").document(email).collection("exercises")
            .document(exerciseToDelete.id)//Assuming each exercise has an ID
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Exercise deleted successfully")
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG, "Error deleting exercise", exception)
            }
    }

}

class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.name)
    private val muscleGroup: TextView = itemView.findViewById(R.id.muscleGroup)
    private val instructions: TextView = itemView.findViewById(R.id.instructions)
    private val position: TextView = itemView.findViewById(R.id.position)
    private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    //Reference to the adapter to call deleteExercise method
    private var adapter: ExerciseAdapter? = null

    fun bind(adapter: ExerciseAdapter, exercise: Exercise, position: Int, email: String) {
        //Bind recipe.name, recipe.ingredients, and recipe.instructions
        //to views in the item_recipe layout
        val positionText = String.format("%d",position + 1)
        this.position.text = positionText
        name.text = exercise.name
        muscleGroup.text = exercise.muscleGroup
        instructions.text = exercise.instructions

        //Setting adapter reference
        this.adapter = adapter
        //Setting listener for deleteButton
        deleteButton.setOnLongClickListener {
            adapter.deleteExercise(absoluteAdapterPosition, email)
            true
        }
    }
}