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

class ExerciseAdapter : RecyclerView.Adapter<ExerciseViewHolder>(){
    private var exercises = listOf<Exercise>()

    //Variables for resizing item holderView because xml is largely static
    private var largestItemWidth = 0
    private var largestItemHeight = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(this, exercise, position)

        //Now using the resizing variables
        holder.itemView.post{
            val width = holder.itemView.width
            val height = holder.itemView.height

            //check and update width
            if(width > largestItemWidth){
                largestItemWidth = width
            }
            //check and update height
            if(height > largestItemHeight){
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
        return exercises.size
    }

    fun setExercises(exercises:List<Exercise>){
        this.exercises = exercises
        notifyDataSetChanged()
    }
    fun deleteExercise(position: Int){
        val exerciseToDelete = exercises[position] //Retrieve the exercise to delete
        exercises = exercises.toMutableList().also{it.removeAt(position)}//Remove from list
        notifyItemRemoved(position)//Notify the adapter of the removal

        //Delete the exercise from the database
        val db =  FirebaseFirestore.getInstance()
        db.collection("exercises")
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
    private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    //Reference to the adapter to call deleteExercise method
    private var adapter: ExerciseAdapter? = null

    fun bind(adapter: ExerciseAdapter, exercise: Exercise, position: Int) {
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
            adapter.deleteExercise(absoluteAdapterPosition)
            true
        }
    }
}