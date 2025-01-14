package com.example.miniproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmRecommendationScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AlgorithmRecommendationViewModel = viewModel()
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val userInput by viewModel.userInput.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Chat history display area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            // Display all previous messages
            if (chatHistory.isEmpty()) {
                Text(
                    text = "Paste your problem statement to get the best algorithm recommendations for efficient solutions!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                chatHistory.forEach { chatMessage ->
                    ChatMessageItem1(chatMessage)
                }
            }
        }

        // Input area for sending new messages
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userInput,
                onValueChange = viewModel::onUserInputChanged,
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                placeholder = { Text("Paste your problem statement...") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            Button(
                onClick = { viewModel.sendAlgorithmRecommendation() },
                enabled = userInput.isNotBlank(),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatMessageItem1(chatMessage: ChatMessage1) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                if (chatMessage.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.shapes.medium
            )
            .padding(12.dp)
    ) {
        Text(
            text = chatMessage.message,
            style = MaterialTheme.typography.bodyLarge,
            color = if (chatMessage.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
