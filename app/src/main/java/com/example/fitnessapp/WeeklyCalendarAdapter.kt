package com.example.fitnessapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class WeeklyCalendarAdapter : RecyclerView.Adapter<ViewHolder>() {
    var daysOfWeek: List<Date> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daysOfWeek.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this, position)
    }
    fun updateData(daysOfWeek: List<Date>){
        this.daysOfWeek = daysOfWeek
        notifyDataSetChanged()
    }
}
class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
    private val dateTextView : TextView = itemView.findViewById(R.id.dayOfWeek)
    fun bind(adapter: WeeklyCalendarAdapter, position: Int){
        val date = adapter.daysOfWeek[position]
        dateTextView.text = dateFormat.format(date)
    }

}