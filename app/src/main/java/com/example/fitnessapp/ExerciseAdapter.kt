package com.example.fitnessapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ExerciseAdapter(private val email: String) : RecyclerView.Adapter<ExerciseViewHolder>(){
    private var exercises = listOf<Exercise>()
    private val exerciseDataManager = ExerciseDataManager(email)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        val fx = InteractionEffects()
        holder.bind(this, exercise, position, email)
        holder.itemView.setOnClickListener {
            fx.itemViewClickEffect(holder.itemView)
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
    fun deleteExercise(position: Int){
        val exerciseToDelete = exercises[position]
        exercises = exercises.toMutableList().also{it.removeAt(position)}
        notifyItemRemoved(position)
        exerciseDataManager.deleteExercise(exerciseToDelete.id)
    }

}

class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.name)
    private val muscleGroup: TextView = itemView.findViewById(R.id.muscleGroup)
    private val instructions: TextView = itemView.findViewById(R.id.instructions)
    private val position: TextView = itemView.findViewById(R.id.position)
    private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    private var adapter: ExerciseAdapter? = null

    fun bind(adapter: ExerciseAdapter, exercise: Exercise, position: Int, email: String) {
        val positionText = String.format("%d",position + 1)
        this.position.text = positionText
        name.text = exercise.name
        muscleGroup.text = exercise.muscleGroup
        instructions.text = exercise.instructions

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 1000 // Set the duration of the animation (in milliseconds)
        itemView.startAnimation(animation) // Start the animation

        this.adapter = adapter
        deleteButton.setOnLongClickListener {
            adapter.deleteExercise(absoluteAdapterPosition)
            true
        }
    }
}