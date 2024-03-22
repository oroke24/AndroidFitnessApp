package com.example.fitnessapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExerciseAdapter(private val exercises: List<Exercise>) : RecyclerView.Adapter<ExerciseViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}

class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(exercise: Exercise) {
        //Bind recipe.name, recipe.ingredients, and recipe.instructions
        //to views in the item_recipe layout
        itemView.findViewById<TextView>(R.id.name).text = exercise.name
        itemView.findViewById<TextView>(R.id.muscleGroup).text = exercise.muscleGroup
        itemView.findViewById<TextView>(R.id.instructions).text = exercise.instructions
    }
}