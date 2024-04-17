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
class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val fx = InteractionEffects()
    private val dateViewFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
    private val dateTextView: TextView = itemView.findViewById(R.id.dayOfWeek)

    private val recipeName1TextView: TextView = itemView.findViewById(R.id.addRecipe1Button)
    private val recipeName2TextView: TextView = itemView.findViewById(R.id.addRecipe2Button)
    private val recipeName3TextView: TextView = itemView.findViewById(R.id.addRecipe3Button)
    private val exerciseName1TextView: TextView = itemView.findViewById(R.id.addExercise1Button)
    private val exerciseName2TextView: TextView = itemView.findViewById(R.id.addExercise2Button)

    private val deleteRecipe1Button: ImageButton = itemView.findViewById(R.id.deleteRecipeButton)
    private val deleteRecipe2Button: ImageButton = itemView.findViewById(R.id.deleteRecipeButton)
    private val deleteRecipe3Button: ImageButton = itemView.findViewById(R.id.deleteRecipeButton)
    private val deleteExercise1Button: ImageButton = itemView.findViewById(R.id.deleteExercise1Button)
    private val deleteExercise2Button: ImageButton = itemView.findViewById(R.id.deleteExercise2Button)

    fun bind(adapter: WeeklyCalendarAdapter, position: Int, email: String) {
        val recipeManager = RecipeDataManager(email)
        val exerciseManager = ExerciseDataManager(email)
        val dayManager = DayDataManager(email)
        val date = adapter.daysOfWeek[position]
        dateTextView.text = dateViewFormat.format(date)
        val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)

        CoroutineScope(Dispatchers.Main).launch {
            val thisDay: Day = dayManager.getDayFromDate(formattedDate)
            recipeName1TextView.text = thisDay.recipe1Id
            recipeName2TextView.text = thisDay.recipe2Id
            recipeName3TextView.text = thisDay.recipe3Id
            exerciseName1TextView.text = thisDay.exercise1Id
            exerciseName2TextView.text = thisDay.exercise2Id
        }

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 1000 // Set the duration of the animation (in milliseconds)
        itemView.startAnimation(animation) // Start the animation

        recipeName1TextView.setOnClickListener {
            recipeItemViewClick(recipeName1TextView, 1, recipeManager, dayManager, formattedDate)
        }
        recipeName2TextView.setOnClickListener {
            recipeItemViewClick(recipeName2TextView, 2, recipeManager, dayManager, formattedDate)
        }
        recipeName3TextView.setOnClickListener {
            recipeItemViewClick(recipeName3TextView, 3, recipeManager, dayManager, formattedDate)
        }
        deleteRecipe1Button.setOnLongClickListener {
            fx.imageButtonClickEffect(deleteRecipe1Button)
            CoroutineScope(Dispatchers.Main).launch {
                recipeName1TextView.text = ""
                dayManager.addRecipeToDay(formattedDate, "", 1)
            }
            true
        }
        deleteRecipe2Button.setOnLongClickListener {
            fx.imageButtonClickEffect(deleteRecipe2Button)
            CoroutineScope(Dispatchers.Main).launch {
                recipeName2TextView.text = ""
                dayManager.addRecipeToDay(formattedDate, "", 2)
            }
            true
        }
        deleteRecipe3Button.setOnLongClickListener {
            fx.imageButtonClickEffect(deleteRecipe3Button)
            CoroutineScope(Dispatchers.Main).launch {
                recipeName3TextView.text = ""
                dayManager.addRecipeToDay(formattedDate, "", 3)
            }
            true
        }

        exerciseName1TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName1TextView, 1, exerciseManager, dayManager, formattedDate)
        }
        exerciseName2TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName2TextView, 2, exerciseManager, dayManager, formattedDate)
        }
        deleteExercise1Button.setOnLongClickListener {
            fx.imageButtonClickEffect(deleteExercise1Button)
            CoroutineScope(Dispatchers.Main).launch {
                exerciseName1TextView.text = ""
                dayManager.addExerciseToDay(formattedDate, "", 1)
            }
            true
        }
        deleteExercise2Button.setOnLongClickListener {
            fx.imageButtonClickEffect(deleteExercise2Button)
            CoroutineScope(Dispatchers.Main).launch {
                exerciseName2TextView.text = ""
                dayManager.addExerciseToDay(formattedDate, "", 2)
            }
            true
        }
    }

    private fun exerciseItemViewClick(itemView: View, slot: Int, exerciseManager: ExerciseDataManager, dayManager: DayDataManager, formattedDate: String) {
        fx.itemViewClickEffect(itemView)
        exerciseManager.fetchUserExerciseIds { exercises ->
            exerciseManager.showExerciseSelectionDialog(
                itemView.context,
                exercises
            ) { selectedExerciseId ->
                CoroutineScope(Dispatchers.Main).launch {
                    if(slot == 1) exerciseName1TextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                    else exerciseName2TextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                    dayManager.addExerciseToDay(formattedDate, selectedExerciseId, slot)
                }
            }
        }
    }
    private fun recipeItemViewClick(itemView: View, slot: Int,  recipeManager: RecipeDataManager, dayManager: DayDataManager, formattedDate: String){
        fx.itemViewClickEffect(itemView)
        recipeManager.fetchUserRecipeIds { recipes ->
            fx.selectionDialogReturnItemId(itemView.context, recipes) { selectedRecipeId ->
                CoroutineScope(Dispatchers.Main).launch {
                    if(slot == 1) recipeName1TextView.text = recipeManager.getNameFromId(selectedRecipeId)
                    else if(slot == 2) recipeName2TextView.text = recipeManager.getNameFromId(selectedRecipeId)
                    else recipeName3TextView.text = recipeManager.getNameFromId(selectedRecipeId)
                    dayManager.addRecipeToDay(formattedDate, selectedRecipeId, slot)
                }
            }
        }
    }
}