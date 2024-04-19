package com.example.fitnessapp

import android.app.AlertDialog
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
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
import java.time.LocalDate
import java.util.*

class WeeklyCalendarAdapter(private val email: String, private val recyclerView: RecyclerView) : RecyclerView.Adapter<ViewHolder>() {
    var daysOfWeek: List<Date> = ArrayList()
    //var daysFromManager: List<Day> = ArrayList()
    val recipeManager = RecipeDataManager(email)
    val exerciseManager = ExerciseDataManager(email)
    val dayManager = DayDataManager(email)
    val todayInyyyyMMdd = LocalDate.now()
    val todayString = todayInyyyyMMdd.toString()
    val tomorrowInyyyyMMdd = todayInyyyyMMdd.plusDays(1)
    val tomorrowString = tomorrowInyyyyMMdd.toString()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        view.layoutParams.height = MATCH_PARENT
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daysOfWeek.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this, position, recipeManager, exerciseManager, dayManager, todayString, tomorrowString)
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
    private val recipeName4TextView: TextView = itemView.findViewById(R.id.addRecipe4Button)
    private val recipeName5TextView: TextView = itemView.findViewById(R.id.addRecipe5Button)
    private val exerciseName1TextView: TextView = itemView.findViewById(R.id.addExercise1Button)
    private val exerciseName2TextView: TextView = itemView.findViewById(R.id.addExercise2Button)
    private val exerciseName3TextView: TextView = itemView.findViewById(R.id.addExercise3Button)
    private val exerciseName4TextView: TextView = itemView.findViewById(R.id.addExercise4Button)
    private val exerciseName5TextView: TextView = itemView.findViewById(R.id.addExercise5Button)
    private val deleteRecipe1Button: ImageButton = itemView.findViewById(R.id.deleteRecipeButton)
    private val deleteRecipe2Button: ImageButton = itemView.findViewById(R.id.deleteRecipe2Button)
    private val deleteRecipe3Button: ImageButton = itemView.findViewById(R.id.deleteRecipe3Button)
    private val deleteRecipe4Button: ImageButton = itemView.findViewById(R.id.deleteRecipe4Button)
    private val deleteRecipe5Button: ImageButton = itemView.findViewById(R.id.deleteRecipe5Button)
    private val deleteExercise1Button: ImageButton = itemView.findViewById(R.id.deleteExercise1Button)
    private val deleteExercise2Button: ImageButton = itemView.findViewById(R.id.deleteExercise2Button)
    private val deleteExercise3Button: ImageButton = itemView.findViewById(R.id.deleteExercise3Button)
    private val deleteExercise4Button: ImageButton = itemView.findViewById(R.id.deleteExercise4Button)
    private val deleteExercise5Button: ImageButton = itemView.findViewById(R.id.deleteExercise5Button)

    fun bind(adapter: WeeklyCalendarAdapter, position: Int, recipeManager: RecipeDataManager, exerciseManager: ExerciseDataManager, dayManager: DayDataManager, todayString: String, tomorrowString: String) {
        val date = adapter.daysOfWeek[position]

        val placeHolder: String = when(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)) {
            todayString-> "Today: ${dateViewFormat.format(date)}"
            tomorrowString-> "Tomorrow: ${dateViewFormat.format(date)}"
            else-> dateViewFormat.format(date)
        }

        dateTextView.text = placeHolder
        val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)

        CoroutineScope(Dispatchers.Main).launch {
            val thisDay: Day = dayManager.getDayFromDate(formattedDate)
            recipeName1TextView.text = thisDay.recipe1Id
            recipeName2TextView.text = thisDay.recipe2Id
            recipeName3TextView.text = thisDay.recipe3Id
            recipeName4TextView.text = thisDay.recipe4Id
            recipeName5TextView.text = thisDay.recipe5Id
            exerciseName1TextView.text = thisDay.exercise1Id
            exerciseName2TextView.text = thisDay.exercise2Id
            exerciseName3TextView.text = thisDay.exercise3Id
            exerciseName4TextView.text = thisDay.exercise4Id
            exerciseName5TextView.text = thisDay.exercise5Id
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
        recipeName4TextView.setOnClickListener {
            recipeItemViewClick(recipeName4TextView, 4, recipeManager, dayManager, formattedDate)
        }
        recipeName5TextView.setOnClickListener {
            recipeItemViewClick(recipeName5TextView, 5, recipeManager, dayManager, formattedDate)
        }
        deleteRecipe1Button.setOnLongClickListener {
            deleteItem(deleteRecipe1Button, recipeName1TextView, "recipe", 1, dayManager, formattedDate)
            true
        }
        deleteRecipe2Button.setOnLongClickListener {
            deleteItem(deleteRecipe2Button, recipeName2TextView, "recipe", 2, dayManager, formattedDate)
            true
        }
        deleteRecipe3Button.setOnLongClickListener {
            deleteItem(deleteRecipe3Button, recipeName3TextView, "recipe", 3, dayManager, formattedDate)
            true
        }
        deleteRecipe4Button.setOnLongClickListener {
            deleteItem(deleteRecipe4Button, recipeName4TextView, "recipe", 4, dayManager, formattedDate)
            true
        }
        deleteRecipe5Button.setOnLongClickListener {
            deleteItem(deleteRecipe5Button, recipeName5TextView, "recipe", 5, dayManager, formattedDate)
            true
        }

        exerciseName1TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName1TextView, 1, exerciseManager, dayManager, formattedDate)
        }
        exerciseName2TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName2TextView, 2, exerciseManager, dayManager, formattedDate)
        }
        exerciseName3TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName3TextView, 3, exerciseManager, dayManager, formattedDate)
        }
        exerciseName4TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName4TextView, 4, exerciseManager, dayManager, formattedDate)
        }
        exerciseName5TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName5TextView, 5, exerciseManager, dayManager, formattedDate)
        }
        deleteExercise1Button.setOnLongClickListener {
            deleteItem(deleteExercise1Button, exerciseName1TextView, "exercise", 1, dayManager, formattedDate)
            true
        }
        deleteExercise2Button.setOnLongClickListener {
            deleteItem(deleteExercise2Button, exerciseName2TextView, "exercise", 2, dayManager, formattedDate)
            true
        }
        deleteExercise3Button.setOnLongClickListener {
            deleteItem(deleteExercise3Button, exerciseName3TextView, "exercise", 3, dayManager, formattedDate)
            true
        }
        deleteExercise4Button.setOnLongClickListener {
            deleteItem(deleteExercise4Button, exerciseName4TextView, "exercise", 4, dayManager, formattedDate)
            true
        }
        deleteExercise5Button.setOnLongClickListener {
            deleteItem(deleteExercise5Button, exerciseName5TextView, "exercise", 5, dayManager, formattedDate)
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
                   when(slot){
                       1-> exerciseName1TextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                       2-> exerciseName2TextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                       3-> exerciseName3TextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                       4-> exerciseName4TextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                       else-> exerciseName5TextView.text = exerciseManager.getNameFromId(selectedExerciseId)
                   }
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
                    when(slot) {
                        1-> recipeName1TextView.text = recipeManager.getNameFromId(selectedRecipeId)
                        2-> recipeName2TextView.text = recipeManager.getNameFromId(selectedRecipeId)
                        3-> recipeName3TextView.text = recipeManager.getNameFromId(selectedRecipeId)
                        4-> recipeName4TextView.text = recipeManager.getNameFromId(selectedRecipeId)
                        else-> recipeName5TextView.text = recipeManager.getNameFromId(selectedRecipeId)
                    }
                    dayManager.addRecipeToDay(formattedDate, selectedRecipeId, slot)
                }
            }
        }
    }
    private fun deleteItem(imageButton: ImageButton, textView: TextView, item: String, slot: Int, dayManager: DayDataManager, formattedDate: String){
        fx.imageButtonClickEffect(imageButton)
        CoroutineScope(Dispatchers.Main).launch {
            textView.text = ""
            if(item == "exercise") dayManager.addExerciseToDay(formattedDate, "", slot)
            else dayManager.addRecipeToDay(formattedDate, "", slot)
        }
    }

}