package com.example.fitnessapp

import java.util.Date

data class Day(
    val id: String,
    val date: String,
    var recipeId: String,
    var exerciseId: String
)