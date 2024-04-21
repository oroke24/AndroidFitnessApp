package com.example.fitnessapp

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class InteractionEffects {
    fun buttonClickEffect(button: Button){
        button.alpha = 0.5F
        Handler(Looper.getMainLooper()).postDelayed({button.alpha = 1f}, 1000)
    }
    fun imageButtonClickEffect(imageButton: ImageButton){
        imageButton.alpha = 0.5F
        Handler(Looper.getMainLooper()).postDelayed({imageButton.alpha = 1f}, 1000)
    }
    fun imageButtonClickEffectQuick(imageButton: ImageButton){
        imageButton.alpha = 0.5F
        Handler(Looper.getMainLooper()).postDelayed({imageButton.alpha = 1f}, 100)
    }
    fun itemViewClickEffect(itemView: View){
        itemView.alpha = 0.5F
        Handler(Looper.getMainLooper()).postDelayed({itemView.alpha = 1f}, 1000)

    }
    fun selectionDialogReturnItemId(context: Context, items: List<Pair<String, String>>, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select One")
        val listOfNames = items.map { it.second }.toTypedArray()
        builder.setItems(listOfNames) { dialog, which ->
            val selectedItemId = items[which].first
            callback(selectedItemId)
        }
        .show()
    }
    suspend fun userApprovesOverwrite(context: Context, name: String): Boolean = suspendCoroutine { continuation ->
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_user_approval, null)
        val messageString = "$name already exists, overwrite?"
        val messageTextView = itemView.findViewById<TextView>(R.id.message)
        messageTextView.text = messageString
        val overwriteButton = itemView.findViewById<Button>(R.id.overwriteButton)
        val cancelButton = itemView.findViewById<Button>(R.id.cancelButton)

        val dialog = AlertDialog.Builder(context, R.style.TransparentDialogTheme)
            .setView(itemView)
            .create()

        overwriteButton.setOnClickListener {
            dialog.dismiss()
            continuation.resume(true)
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
            continuation.resume(false)
        }
        dialog.show()
    }
    suspend fun userApproveGeneral(context: Context, message: String): Boolean = suspendCoroutine { continuation ->
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_user_approval, null)
        val messageTextView = itemView.findViewById<TextView>(R.id.message)
        messageTextView.text = message
        val overwriteButton = itemView.findViewById<Button>(R.id.overwriteButton)
        val cancelButton = itemView.findViewById<Button>(R.id.cancelButton)

        val dialog = AlertDialog.Builder(context, R.style.TransparentDialogTheme)
            .setView(itemView)
            .create()

        overwriteButton.setOnClickListener {
            dialog.dismiss()
            continuation.resume(true)
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
            continuation.resume(false)
        }
        dialog.show()
    }
    fun hideViewAndButton(itemView: View, imageButton: ImageButton){
        itemView.visibility = View.GONE
        imageButton.visibility = View.GONE
    }
    fun showViewAndButton(itemView: View, imageButton: ImageButton){
        itemView.visibility = View.VISIBLE
        imageButton.visibility = View.VISIBLE
    }
}
