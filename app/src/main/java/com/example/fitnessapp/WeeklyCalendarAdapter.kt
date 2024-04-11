package com.example.fitnessapp

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class WeeklyCalendarAdapter(private val email: String) : RecyclerView.Adapter<ViewHolder>() {
    var daysOfWeek: List<Date> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daysOfWeek.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this, position, email)
    }
    fun updateData(daysOfWeek: List<Date>){
        this.daysOfWeek = daysOfWeek
        notifyDataSetChanged()
    }
}
class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val fx = InteractionEffects()
    private val dateViewFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
    private val dateTextView : TextView = itemView.findViewById(R.id.dayOfWeek)
    private val recipeNameTextView : TextView = itemView.findViewById(R.id.recipeName)
    private val exerciseNameTextView : TextView = itemView.findViewById(R.id.exerciseName)
    private val recipeButton : ImageButton = itemView.findViewById(R.id.addRecipeButton)
    private val exerciseButton: ImageButton = itemView.findViewById(R.id.addExerciseButton)

    fun bind(adapter: WeeklyCalendarAdapter, position: Int, email: String) {
        val recipeManager = RecipeDataManager(email)
        val exerciseManager = ExerciseDataManager(email)
        val dayManager = DayDataManager(email)
        val date = adapter.daysOfWeek[position]
        dateTextView.text = dateViewFormat.format(date)

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 1000 // Set the duration of the animation (in milliseconds)
        itemView.startAnimation(animation) // Start the animation

        recipeButton.setOnClickListener {
            fx.imageButtonClickEffect(recipeButton)
            recipeManager.fetchUserRecipeIds { recipes ->
                fx.selectionDialogReturnItemId(itemView.context, recipes) { selectedRecipeId ->
                    CoroutineScope(Dispatchers.Main).launch {
                        recipeNameTextView.text = recipeManager.getNameFromId(selectedRecipeId)
                        dayManager.addRecipeToDay(date, selectedRecipeId)
                    }
                }
            }
        }

        exerciseButton.setOnClickListener{
            fx.imageButtonClickEffect(exerciseButton)
            exerciseManager.fetchUserExerciseIds { exercises ->
                exerciseManager.showExerciseSelectionDialog(itemView.context, exercises){selectedExerciseId ->
                    CoroutineScope(Dispatchers.Main).launch{
                        exerciseNameTextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                        dayManager.addExerciseToDay(date, selectedExerciseId)
                    }
                }

            }
        }
    }


}