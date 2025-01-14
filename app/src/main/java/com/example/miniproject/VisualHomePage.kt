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
    val scrollingState0 = rememberScrollState()
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
            Text("Data Structures", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState0).padding(top = 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Array",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(
                        onClick = { navController.navigate("Array") },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)// Add some padding at the bottom
                    ) {
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Stack",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Button(onClick = {navController.navigate("Stack")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Queue",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Queue")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Linked List",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Linked_list")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Hash Table",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Hash_table")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "BST",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("BST")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }}

            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Sorting Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState1).padding(top = 5.dp)) {

                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Bubble Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Button(onClick = {navController.navigate("BubbleSort")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Insertion Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("InsertionSort")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Selection Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("SelectionSort")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Merge Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("MergeSort")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Quick Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("QuickSort")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }

            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Searching Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState2).padding(top = 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Linear",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Button(onClick = {navController.navigate("LinearSearch")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Binary",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("BinarySearch")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Interpolation Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("IPS")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                        .wrapContentSize()
                        .size(width = 80.dp, height = 35.dp) // Set a fixed size
                        .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Graph Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState2).padding(top = 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "DFS",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Button(onClick = {navController.navigate("DFS")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "BFS",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("BFS")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Topological Sort",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("TopoSort")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Greedy Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState2).padding(top = 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Prim's",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Button(onClick = {navController.navigate("prims")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Kruskal's",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Kruskal")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Dijkstra's",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Dijkstra")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("AI&ML Algorithms", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState2).padding(top = 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "GBFS",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Button(onClick = {navController.navigate("Gbfs")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "UCS",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Ucs")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "Astar",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Astar")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "IDAstar",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Idastar")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text("Transform-and-Conquer Design Technique", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Row(modifier = Modifier.horizontalScroll(scrollingState2).padding(top = 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "AVL-Tree",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Button(onClick = {navController.navigate("avl")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(boxWidth)
                        .padding(4.dp)
                        .background(Color.DarkGray)
                ) {
                    Text(
                        text = "heap",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Button(onClick = {navController.navigate("Heap")},
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .wrapContentSize()
                            .size(width = 80.dp, height = 35.dp) // Set a fixed size
                            .padding(bottom = 4.dp)){
                        Text("Start")
                    }
                }

            }
        }
    }
}
