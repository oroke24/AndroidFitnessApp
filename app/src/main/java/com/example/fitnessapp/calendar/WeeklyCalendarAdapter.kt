package com.example.fitnessapp.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.InteractionEffects
import com.example.fitnessapp.R
import com.example.fitnessapp.firestore.DayDataManager
import com.example.fitnessapp.firestore.ExerciseDataManager
import com.example.fitnessapp.firestore.RecipeDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class WeeklyCalendarAdapter(email: String, private val recyclerView: RecyclerView) : RecyclerView.Adapter<ViewHolder>() {
    var daysOfWeek: List<Date> = ArrayList()
    private val recipeManager = RecipeDataManager(email)
    private val exerciseManager = ExerciseDataManager(email)
    private val dayManager = DayDataManager(email)
    private val todayInyyyyMMdd: LocalDate = LocalDate.now()
    private val todayString = todayInyyyyMMdd.toString()
    private val tomorrowInyyyyMMdd: LocalDate = todayInyyyyMMdd.plusDays(1)
    private val tomorrowString = tomorrowInyyyyMMdd.toString()


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
        val formattedDateForDB = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
        CoroutineScope(Dispatchers.Main).launch {
            val thisDay: Day = dayManager.getDayFromDate(formattedDateForDB)
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
            recipeItemViewClick(recipeName1TextView, 1, recipeManager, dayManager, formattedDateForDB)
        }
        recipeName2TextView.setOnClickListener {
            recipeItemViewClick(recipeName2TextView, 2, recipeManager, dayManager, formattedDateForDB)
        }
        recipeName3TextView.setOnClickListener {
            recipeItemViewClick(recipeName3TextView, 3, recipeManager, dayManager, formattedDateForDB)
        }
        recipeName4TextView.setOnClickListener {
            recipeItemViewClick(recipeName4TextView, 4, recipeManager, dayManager, formattedDateForDB)
        }
        recipeName5TextView.setOnClickListener {
            recipeItemViewClick(recipeName5TextView, 5, recipeManager, dayManager, formattedDateForDB)
        }
        deleteRecipe1Button.setOnLongClickListener {
            deleteItem(deleteRecipe1Button, recipeName1TextView, "recipe", 1, dayManager, formattedDateForDB)
            true
        }
        deleteRecipe2Button.setOnLongClickListener {
            deleteItem(deleteRecipe2Button, recipeName2TextView, "recipe", 2, dayManager, formattedDateForDB)
            true
        }
        deleteRecipe3Button.setOnLongClickListener {
            deleteItem(deleteRecipe3Button, recipeName3TextView, "recipe", 3, dayManager, formattedDateForDB)
            true
        }
        deleteRecipe4Button.setOnLongClickListener {
            deleteItem(deleteRecipe4Button, recipeName4TextView, "recipe", 4, dayManager, formattedDateForDB)
            true
        }
        deleteRecipe5Button.setOnLongClickListener {
            deleteItem(deleteRecipe5Button, recipeName5TextView, "recipe", 5, dayManager, formattedDateForDB)
            true
        }

        exerciseName1TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName1TextView, 1, exerciseManager, dayManager, formattedDateForDB)
        }
        exerciseName2TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName2TextView, 2, exerciseManager, dayManager, formattedDateForDB)
        }
        exerciseName3TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName3TextView, 3, exerciseManager, dayManager, formattedDateForDB)
        }
        exerciseName4TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName4TextView, 4, exerciseManager, dayManager, formattedDateForDB)
        }
        exerciseName5TextView.setOnClickListener {
            exerciseItemViewClick(exerciseName5TextView, 5, exerciseManager, dayManager, formattedDateForDB)
        }
        deleteExercise1Button.setOnLongClickListener {
            deleteItem(deleteExercise1Button, exerciseName1TextView, "exercise", 1, dayManager, formattedDateForDB)
            true
        }
        deleteExercise2Button.setOnLongClickListener {
            deleteItem(deleteExercise2Button, exerciseName2TextView, "exercise", 2, dayManager, formattedDateForDB)
            true
        }
        deleteExercise3Button.setOnLongClickListener {
            deleteItem(deleteExercise3Button, exerciseName3TextView, "exercise", 3, dayManager, formattedDateForDB)
            true
        }
        deleteExercise4Button.setOnLongClickListener {
            deleteItem(deleteExercise4Button, exerciseName4TextView, "exercise", 4, dayManager, formattedDateForDB)
            true
        }
        deleteExercise5Button.setOnLongClickListener {
            deleteItem(deleteExercise5Button, exerciseName5TextView, "exercise", 5, dayManager, formattedDateForDB)
            true
        }
    }
    private fun exerciseItemViewClick(itemView: View, slot: Int, exerciseManager: ExerciseDataManager, dayManager: DayDataManager, formattedDate: String) {
        fx.itemViewClickEffect(itemView)
        exerciseManager.fetchUserExerciseIds { exercises ->
            fx.selectionDialogReturnItemId(itemView.context, exercises) { selectedExerciseId ->
               CoroutineScope(Dispatchers.Main).launch {
                   val name = exerciseManager.getNameFromId(selectedExerciseId)
                   when(slot){
                       1-> exerciseName1TextView.text = name
                       2-> exerciseName2TextView.text = name
                       3-> exerciseName3TextView.text = name
                       4-> exerciseName4TextView.text = name
                       else-> exerciseName5TextView.text = name
                   }
                   dayManager.addExerciseToDay(formattedDate, name, slot)
                }
            }
        }
    }
    private fun recipeItemViewClick(itemView: View, slot: Int, recipeManager: RecipeDataManager, dayManager: DayDataManager, formattedDate: String){
        fx.itemViewClickEffect(itemView)
        recipeManager.fetchUserRecipeIds { recipes ->
            fx.selectionDialogReturnItemId(itemView.context, recipes) { selectedRecipeId ->
                CoroutineScope(Dispatchers.Main).launch {
                    val name = recipeManager.getNameFromId(selectedRecipeId)
                    when(slot) {
                        1-> recipeName1TextView.text = name
                        2-> recipeName2TextView.text = name
                        3-> recipeName3TextView.text = name
                        4-> recipeName4TextView.text = name
                        else-> recipeName5TextView.text = name
                    }
                    dayManager.addRecipeToDay(formattedDate, name, slot)
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