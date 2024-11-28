package com.example.fitnessapp.ai

import android.content.ContentValues.TAG
import android.util.Log
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.fitnessapp.firestore.AiDataManager
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
class AiHome(public final val email: String) {
    private lateinit var  aiDataManager: AiDataManager
    lateinit var aiCard: AiCard
    suspend fun aiCall(cardType: String, content: String){
        aiDataManager = AiDataManager(email)
        Log.e("CardType", cardType)
        var aiRole = when(cardType){
            "exercises" -> "You are a professional fitness trainer, building exercise cards. 'Group_one' is for muscle group(s) and 'group_two' is for roughly one hour of exercises (Be specific and include sets, reps, or time intervals when helpful). "
            else -> "You are a professional dietitian/chef, building recipe cards.  'Group_one' is for muscle ingredients and 'group_two' is for instructions. "
        }
        aiRole += "Format the response to be a JSON (only raw JSON data, without any markdown like '``` Json... ```').  It should contain a list of strings named 'group_one', and a list of strings named 'group_two'. Begin each string with a hyphen.  For 'group_two' Use digits for numbers, 18 word limit per string."
        val userMessage = "Build a card based on this information, feel free to add, modify, or remove as you want: $content"
        val openai = OpenAI(
            token = getKey(),
            timeout = Timeout(socket = 60.seconds)
        )
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = aiRole
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = userMessage
                )
            )
        )
        val messageContent = openai.chatCompletion(chatCompletionRequest).choices[0].message.content?:""
        Log.d(TAG, messageContent)

        // Parse the JSON into the aiCard object
        try {
            aiCard = Json.decodeFromString<AiCard>(messageContent)
            // Log the parsed data
            Log.d(TAG, "Group One: ${aiCard.groupOne}")
            Log.d(TAG, "Group Two: ${aiCard.groupTwo}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON response: $e")
        }
    }
    private suspend fun getKey(): String {
        return aiDataManager.accessKey()
    }
}