package com.example.fitnessapp

import android.app.AlertDialog
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class WeeklyCalendarAdapter(private val email: String, private val recyclerView: RecyclerView) : RecyclerView.Adapter<ViewHolder>() {
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
        recyclerView.scrollToPosition(0)
        notifyDataSetChanged()
    }
}
class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val fx = InteractionEffects()
    private val dateViewFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
    private val dateTextView : TextView = itemView.findViewById(R.id.dayOfWeek)
    private val recipeNameTextView : TextView = itemView.findViewById(R.id.addRecipeButton)
    private val exerciseNameTextView : TextView = itemView.findViewById(R.id.addExerciseButton)
    private val deleteRecipeButton : ImageButton = itemView.findViewById(R.id.deleteRecipeButton)
    private val deleteExerciseButton: ImageButton = itemView.findViewById(R.id.deleteExerciseButton)

    fun bind(adapter: WeeklyCalendarAdapter, position: Int, email: String) {
        val recipeManager = RecipeDataManager(email)
        val exerciseManager = ExerciseDataManager(email)
        val dayManager = DayDataManager(email)
        val date = adapter.daysOfWeek[position]
        dateTextView.text = dateViewFormat.format(date)
        val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)

        CoroutineScope(Dispatchers.Main).launch {
            val thisDay: Day = dayManager.getDayFromDate(formattedDate)
            recipeNameTextView.text = thisDay.recipeId
            exerciseNameTextView.text = thisDay.exerciseId
        }

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 1000 // Set the duration of the animation (in milliseconds)
        itemView.startAnimation(animation) // Start the animation

        recipeNameTextView.setOnClickListener {
            fx.itemViewClickEffect(recipeNameTextView)
            recipeManager.fetchUserRecipeIds { recipes ->
                fx.selectionDialogReturnItemId(itemView.context, recipes) { selectedRecipeId ->
                    CoroutineScope(Dispatchers.Main).launch {
                        recipeNameTextView.text = recipeManager.getNameFromId(selectedRecipeId)
                        dayManager.addRecipeToDay(formattedDate, selectedRecipeId)
                    }
                }
            }
        }
        deleteRecipeButton.setOnLongClickListener{
            fx.imageButtonClickEffect(deleteRecipeButton)
            CoroutineScope(Dispatchers.Main).launch {
                recipeNameTextView.text = ""
                dayManager.addRecipeToDay(formattedDate,"")
            }
            true
        }

        exerciseNameTextView.setOnClickListener{
            fx.itemViewClickEffect(exerciseNameTextView)
            exerciseManager.fetchUserExerciseIds { exercises ->
                exerciseManager.showExerciseSelectionDialog(itemView.context, exercises){selectedExerciseId ->
                    CoroutineScope(Dispatchers.Main).launch{
                        exerciseNameTextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                        dayManager.addExerciseToDay(formattedDate, selectedExerciseId)
                    }
                }

            }
        }
        deleteExerciseButton.setOnLongClickListener{
            fx.imageButtonClickEffect(deleteExerciseButton)
            CoroutineScope(Dispatchers.Main).launch {
                exerciseNameTextView.text = ""
                dayManager.addExerciseToDay(formattedDate, "")
            }
            true
        }
    }


}