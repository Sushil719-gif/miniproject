package com.example.miniproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage1(val message: String, val isUser: Boolean)

class AlgorithmRecommendationViewModel : ViewModel() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-002", // Replace with the correct model name
        apiKey = "AIzaSyDqwWkqAI9m6X67Wl5OVxooXdKZ512fs60" // Replace with your actual API key
    )

    // List to hold the chat history
    private val _chatHistory = MutableStateFlow<List<ChatMessage1>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage1>> = _chatHistory.asStateFlow()

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    // Update the user input when typing
    fun onUserInputChanged(input: String) {
        _userInput.value = input
    }

    // Handle send message action for algorithm recommendations
    fun sendAlgorithmRecommendation() {
        val problemStatement = _userInput.value
        if (problemStatement.isBlank()) return

        // Add user message to the chat history
        val updatedHistory = _chatHistory.value + ChatMessage1(problemStatement, isUser = true)
        _chatHistory.value = updatedHistory

        // Clear the user input
        _userInput.value = ""

        // Show loading state (you can add this to your UI if needed)
        viewModelScope.launch {
            try {
                // Generate algorithm recommendations
                val response = generativeModel.generateContent(generateAlgorithmPrompt(problemStatement))

                // Format and structure the generated response
                val formattedResponse = formatAlgorithmResponse(response.text)

                // Add the formatted response to the chat history
                val botMessage = ChatMessage1(formattedResponse, isUser = false)

                // Append the bot message to the chat history without overwriting previous messages
                _chatHistory.value = _chatHistory.value + botMessage
            } catch (e: Exception) {
                // If there's an error, show the error message
                val errorMessage = "Error: ${e.message ?: "Unknown error occurred"}"
                _chatHistory.value = _chatHistory.value + ChatMessage1(errorMessage, isUser = false)
            }
        }
    }

    private fun generateAlgorithmPrompt(problem: String): String {
        return """
            Based on the following problem statement, provide the following algorithm approaches:
            1. Brute Force approach Explaination with time and space complexity and its code in C++ without main function.
            2. Better approach Explaination with time and space complexity and its code in C++ without main function.
            3. Optimal approach Explaination with time and space complexity and its code in C++ without main function.
            Problem:
            $problem
        """.trimIndent()
    }

    private fun formatAlgorithmResponse(response: String?): String {
        return """
            ==========================
            Algorithm Recommendations:
            ==========================
            ${response ?: "No recommendations received."}
        """.trimIndent()
    }
}

