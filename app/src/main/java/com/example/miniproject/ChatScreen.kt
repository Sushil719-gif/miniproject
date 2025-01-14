//package com.example.miniproject
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatScreen(
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    viewModel: ChatViewModel = viewModel()
//) {
//    val chatState by viewModel.chatState.collectAsState()
//    val userInput by viewModel.userInput.collectAsState()
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//            .padding(16.dp)
//    ) {
//        // Chat messages display area
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth()
//                .verticalScroll(rememberScrollState())
//                .background(MaterialTheme.colorScheme.surface)
//                .padding(8.dp)
//        ) {
//            when (chatState) {
//                is CodeanlysisUiState.Initial -> {
//                    Text(
//                        text = "Type your code to start analyzing it",
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier
//                            .align(Alignment.Center)
//                            .padding(16.dp),
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//                is CodeanlysisUiState.Loading -> {
//                    CircularProgressIndicator(
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//                is CodeanlysisUiState.Success -> {
//                    Text(
//                        text = (chatState as CodeanlysisUiState.Success).response,
//                        modifier = Modifier
//                            .align(Alignment.TopStart)
//                            .padding(8.dp)
//                            .background(
//                                MaterialTheme.colorScheme.primaryContainer,
//                                MaterialTheme.shapes.medium
//                            )
//                            .padding(12.dp),
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
//                }
//                is CodeanlysisUiState.Error -> {
//                    Text(
//                        text = (chatState as CodeanlysisUiState.Error).message,
//                        modifier = Modifier
//                            .align(Alignment.Center)
//                            .padding(16.dp),
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            }
//        }
//
//        // Input area
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            TextField(
//                value = userInput,
//                onValueChange = viewModel::onUserInputChanged,
//                modifier = Modifier
//                    .weight(1f)
//                    .background(MaterialTheme.colorScheme.surfaceVariant),
//                placeholder = { Text("Type your code...") },
//                colors = TextFieldDefaults.textFieldColors(
//                    containerColor = MaterialTheme.colorScheme.surface
//                )
//            )
//
//            Button(
//                onClick = { viewModel.sendMessage() }, // Trigger the message sending
//                enabled = userInput.isNotBlank(),
//                modifier = Modifier.height(56.dp)
//            ) {
//                Text("Send")
//            }
//        }
//    }
//}
package com.example.miniproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
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
                    text = "Start chatting to analyze your code!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                chatHistory.forEach { chatMessage ->
                    ChatMessageItem(chatMessage)
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
                placeholder = { Text("Type your code...") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            Button(
                onClick = { viewModel.sendMessage() },
                enabled = userInput.isNotBlank(),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatMessageItem(chatMessage: ChatMessage) {
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
