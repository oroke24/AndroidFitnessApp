package com.example.fitnessapp

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private val dateViewFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
    private val dateStoreFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val dateTextView : TextView = itemView.findViewById(R.id.dayOfWeek)
    private val recipeIdTextView : TextView = itemView.findViewById(R.id.recipeId)
    private val recipeButton : Button = itemView.findViewById(R.id.addRecipeButton)

    fun bind(adapter: WeeklyCalendarAdapter, position: Int, email: String) {
        val recipeManager = RecipeDataManager(email)
        val dayManager = DayDataManager(email)
        val date = adapter.daysOfWeek[position]
        dateTextView.text = dateViewFormat.format(date)

        recipeButton.setOnClickListener {
            recipeManager.fetchUserRecipeIds() { recipes ->
                recipeManager.showRecipeSelectionDialog(itemView.context, recipes) { selectedRecipeId ->
                    CoroutineScope(Dispatchers.Main).launch {
                        // Calling the suspending functions within the coroutine scope
                        recipeIdTextView.text = recipeManager.getNameFromId(selectedRecipeId)
                        dayManager.addRecipetoDay(date, selectedRecipeId)
                    }
                }
            }
        }
    }


}