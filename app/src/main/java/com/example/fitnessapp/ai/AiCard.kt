package com.example.fitnessapp.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiCard(
    @SerialName("group_one") val groupOne: List<String>,
    @SerialName("group_two") val groupTwo: List<String>
)
