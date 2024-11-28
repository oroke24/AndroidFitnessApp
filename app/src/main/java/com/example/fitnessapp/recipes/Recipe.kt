package com.example.fitnessapp.recipes

data class Recipe(
    val id: String,
    val name:String,
    val ingredients:String,
    val instructions:String,
    val imgUrl: String? = null
)
