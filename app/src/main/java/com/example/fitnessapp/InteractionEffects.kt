package com.example.fitnessapp

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton

class InteractionEffects {
    fun buttonClickEffect(button: Button){
        button.alpha = 0.5F
        Handler(Looper.getMainLooper()).postDelayed({button.alpha = 1f}, 1000)
    }
    fun imageButtonClickEffect(imageButton: ImageButton){
        imageButton.alpha = 0.5F
        Handler(Looper.getMainLooper()).postDelayed({imageButton.alpha = 1f}, 1000)
    }
    fun itemViewClickEffect(itemView: View){
        itemView.alpha = 0.5F
        Handler(Looper.getMainLooper()).postDelayed({itemView.alpha = 1f}, 1000)

    }
}