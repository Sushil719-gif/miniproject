//package com.example.miniproject
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.ai.client.generativeai.GenerativeModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//// Sealed class representing different UI states
//sealed class CodeanlysisUiState {
//    object Initial : CodeanlysisUiState()
//    object Loading : CodeanlysisUiState()
//    data class Success(val response: String) : CodeanlysisUiState()
//    data class Error(val message: String) : CodeanlysisUiState()
//}
//
//// ViewModel for chat-based code analysis
//class ChatViewModel : ViewModel() {
//
//    // Initialize the GenerativeModel (Gemini API)
//    private val generativeModel = GenerativeModel(
//        modelName = "gemini-1.0-pro", // Replace with the correct model name
//        apiKey = "AIzaSyDqwWkqAI9m6X67Wl5OVxooXdKZ512fs60" // Replace with your actual API key
//    )
//
//    // Chat state flow to manage loading, success, and error states
//    private val _chatState = MutableStateFlow<CodeanlysisUiState>(CodeanlysisUiState.Initial)
//    val chatState: StateFlow<CodeanlysisUiState> = _chatState.asStateFlow()
//
//    // User input flow
//    private val _userInput = MutableStateFlow("")
//    val userInput: StateFlow<String> = _userInput.asStateFlow()
//
//    // Update the user input when typing
//    fun onUserInputChanged(input: String) {
//        _userInput.value = input
//    }
//
//    // Handle send message action
//    fun sendMessage() {
//        val userMessage = _userInput.value
//        if (userMessage.isBlank()) return
//
//        viewModelScope.launch {
//            // Show loading state
//            _chatState.value = CodeanlysisUiState.Loading
//
//            try {
//                // Send the code to the generative model to analyze it
//                val response = generativeModel.generateContent(generateAnalysisPrompt(userMessage))
//
//                // Extract the relevant details from the response
//                val analysisResult = response.text ?: "No analysis result received"
//
//                // Update the state with the result (analysis result)
//                _chatState.value = CodeanlysisUiState.Success(analysisResult)
//            } catch (e: Exception) {
//                // If there's an error, update the state with the error message
//                _chatState.value = CodeanlysisUiState.Error(e.message ?: "Unknown error occurred")
//            }
//
//            // Clear the user input
//            _userInput.value = ""
//        }
//    }
//
//    // Generate a prompt that will be sent to the generative model (Gemini or any other model)
//    private fun generateAnalysisPrompt(code: String): String {
//        return """
//            Analyze the following code and provide the following:
//            1. Time complexity
//            2. Space complexity
//            3. Performance insights
//            4. Optimized code
//            Code:
//            $code
//        """.trimIndent()
//    }
//}

package com.example.miniproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val message: String, val isUser: Boolean)

class ChatViewModel : ViewModel() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-002", // Replace with the correct model name
        apiKey = "AIzaSyDqwWkqAI9m6X67Wl5OVxooXdKZ512fs60" // Replace with your actual API key
    )

    // List to hold the chat history
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    // Update the user input when typing
    fun onUserInputChanged(input: String) {
        _userInput.value = input
    }

    // Handle send message action
    fun sendMessage() {
        val userMessage = _userInput.value
        if (userMessage.isBlank()) return

        // Add user message to the chat history
        val updatedHistory = _chatHistory.value + ChatMessage(userMessage, isUser = true)
        _chatHistory.value = updatedHistory

        // Clear the user input
        _userInput.value = ""

        // Show loading state (you can add this to your UI if needed)
        viewModelScope.launch {
            try {
                // Send the code to the generative model to analyze it
                val response = generativeModel.generateContent(generateAnalysisPrompt(userMessage))

                // Format and structure the generated response
                val formattedResponse = formatResponse(response.text)

                // Add the formatted response to the chat history
                val botMessage = ChatMessage(formattedResponse, isUser = false)

                // Append the bot message to the chat history without overwriting previous messages
                _chatHistory.value = _chatHistory.value + botMessage
            } catch (e: Exception) {
                // If there's an error, show the error message
                val errorMessage = "Error: ${e.message ?: "Unknown error occurred"}"
                _chatHistory.value = _chatHistory.value + ChatMessage(errorMessage, isUser = false)
            }
        }
    }

    private fun generateAnalysisPrompt(code: String): String {
        return """
            Analyze the following code and provide the following:
            1. Time complexity of given code in C++ without main function.
            2. Space complexity of  given code in C++ without main function.
            3. Insights to Optimize code in C++ without main function.
            4. Optimized code
            Code:
            $code
            5. Time complexity of the Optimized code
        """.trimIndent()
    }

    private fun formatResponse(response: String?): String {
        return """
            ==========================
            Code Analysis Report:
            ==========================
            ${response ?: "No analysis result received."}
        """.trimIndent()
    }
}

