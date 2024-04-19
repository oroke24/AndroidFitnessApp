package com.example.fitnessapp

import java.util.Date

data class Day(
    val id: String,
    val date: String,
    var recipe1Id: String,
    var recipe2Id: String,
    var recipe3Id: String,
    var recipe4Id: String,
    var recipe5Id: String,
    var exercise1Id: String,
    var exercise2Id: String,
    var exercise3Id: String,
    var exercise4Id: String,
    var exercise5Id: String
)