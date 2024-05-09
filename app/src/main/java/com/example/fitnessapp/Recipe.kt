package com.example.fitnessapp

data class Recipe(
    val id: String,
    val name:String,
    val ingredients:String,
    val instructions:String,
    val imgUrl: String? = null
)
