package com.example.miniproject

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun VisualHomePage(navController: NavController) {

    // Track scroll position
    val verticalScrollState = rememberScrollState()

    // State to control visibility of the IconButton
    var isIconVisible by remember { mutableStateOf(true) }

    // Detect when the user scrolls and show/hide IconButton
    LaunchedEffect(verticalScrollState.value) {
        isIconVisible = verticalScrollState.value == 0
    }

    val scrollingState1 = rememberScrollState()
    val scrollingState2 = rememberScrollState()
    val scrollingState3 = rememberScrollState()
    val scrollingState4 = rememberScrollState()
    val scrollingState5 = rememberScrollState()
    val scrollingState6 = rememberScrollState()

    // Get the screen width
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Calculate the box size dynamically, based on screen width
    // Let's assume we want 3 boxes to fit horizontally with padding in between
    val boxWidth = (screenWidth / 3) - 16.dp // 16.dp padding between boxes

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Only show the IconButton if isIconVisible is true
        if (isIconVisible) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .padding(top = 60.dp)
        ) {
            Text("Sorting Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState1).padding(top = 5.dp)) {

                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.Gray)
                ) {
                    Text(
                        text = "Bubble Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Button(onClick = {navController.navigate("BubbleSort")}, modifier = Modifier.align(Alignment.BottomCenter)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.Gray)
                ) {
                    Text(
                        text = "Insertion Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("InsertionSort")}, modifier = Modifier.align(Alignment.BottomCenter)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.Gray)
                ) {
                    Text(
                        text = "Selection Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("SelectionSort")}, modifier = Modifier.align(Alignment.BottomCenter)){
                        Text("Start")
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Searching Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState2).padding(top = 5.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(boxWidth)
                            .padding(4.dp) // Added padding between boxes
                            .background(Color.Gray)
                    ) {
                        Text("Box ${it + 1}", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Graph Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState3).padding(top = 5.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(boxWidth)
                            .padding(4.dp) // Added padding between boxes
                            .background(Color.Gray)
                    ) {
                        Text("Box ${it + 1}", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Graph Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState4).padding(top = 5.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(boxWidth)
                            .padding(4.dp) // Added padding between boxes
                            .background(Color.Gray)
                    ) {
                        Text("Box ${it + 1}", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Machine Learning Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState5).padding(top = 5.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(boxWidth)
                            .padding(4.dp) // Added padding between boxes
                            .background(Color.Gray)
                    ) {
                        Text("Box ${it + 1}", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Other Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState6).padding(top = 5.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(boxWidth)
                            .padding(4.dp) // Added padding between boxes
                            .background(Color.Gray)
                    ) {
                        Text("Box ${it + 1}", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}
