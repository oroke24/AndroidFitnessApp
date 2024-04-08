package com.example.fitnessapp

import java.util.Date

data class Day(
    val id: String,
    val date: Date,
    var recipeId: String
)