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

class AiHome(public final val email: String) {
    private lateinit var  aiDataManager: AiDataManager
    private lateinit var subGroupOne: String
    private lateinit var subGroupTwo: String
    suspend fun aiCall(cardType: String, content: String){
        aiDataManager = AiDataManager(email)
        Log.e("CardType", cardType)
        val aiRole = when(cardType){
            "exercises" -> "You are a professional fitness trainer, building exercise cards.  Format the response to be a JSON with a list of strings named 'muscle_groups', and a list of strings named 'instructions'. Begin each string with a hyphen (Be concise, 18 word limit per string)"
            else -> "You are a professional dietitian/chef, building recipe cards.  Format the response to be a JSON with a list of strings named 'ingredients' and a list of strings named 'instructions'. Begin each string with a hyphen (Be concise, 18 word limit per string). "
        }
        val userMessage = "Build a card based on this information feel free to add or remove as you want: $content"
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
        val completion: ChatCompletion = openai.chatCompletion(chatCompletionRequest)
        Log.d(TAG, completion.toString())
    }
    private suspend fun getKey(): String {
        return aiDataManager.accessKey()
    }
}