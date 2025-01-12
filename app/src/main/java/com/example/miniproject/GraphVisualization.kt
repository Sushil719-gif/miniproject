package com.example.miniproject

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin


//DFS

@Composable
fun DFSGraphTraversal(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<List<String>, List<String>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var visitedOrder by remember { mutableStateOf(listOf<String>()) }
    var traversalComplete by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    // Parse adjacency list when input is submitted
    fun parseAdjacencyList(input: String): Map<String, List<String>> {
        return try {
            input.split(";").associate { entry ->
                val (node, neighbors) = entry.split(":").let {
                    it[0] to it.getOrElse(1) { "" }.split(",").filter { it.isNotEmpty() }
                }
                node to neighbors
            }
        } catch (e: Exception) {
            errorText = "Invalid input format! Please use A:B,C;B:D;C:D"
            emptyMap()
        }
    }

    // Run DFS on graph
    fun runDFS() {
        if (inputText.isBlank()) {
            errorText = "Please enter a valid adjacency list"
            return
        }
        val visited = mutableSetOf<String>()
        val stack = mutableListOf<String>()
        val stepList = mutableListOf<Pair<String, Pair<List<String>, List<String>>>>()
        val nodeVisitOrder = mutableListOf<String>() // Track order of traversal

        if (adjacencyList.isNotEmpty()) {
            dfsTraversal(adjacencyList, adjacencyList.keys.first(), visited, stack, stepList, nodeVisitOrder)
        }

        steps = stepList
        visitedOrder = nodeVisitOrder
        currentStepIndex = 0

        // Trigger the update of the steps and mark completion after traversal
        traversalComplete = true // Set traversal as completed
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // This Box keeps the IconButton fixed at the top
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart) // Make sure it's aligned at the top left
                .zIndex(1f) // Keep the button on top of other elements
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable { dismissKeyboard() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(50.dp))
        Text(
            text = "DFS Visualization",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Input field
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Adjacency List (e.g., A:B,C;B:D;C:D;D)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorText.isNotEmpty()
        )
        if (errorText.isNotEmpty()) {
            Text(errorText, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Parse and visualize graph button
        Button(
            onClick = {
                dismissKeyboard()
                errorText = ""
                adjacencyList = parseAdjacencyList(inputText)
                runDFS()
            }
        ) {
            Text("Visualize Graph")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph Visualization
        if (adjacencyList.isNotEmpty()) {


            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GraphVisualization(
                    adjacencyList = adjacencyList,
                    currentStep = steps.getOrNull(currentStepIndex),
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(8.dp)
                )

                StackVisualization(
                    stack = steps.getOrNull(currentStepIndex)?.second?.first ?: emptyList(),
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))



            // Navigation buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Step ${currentStepIndex + 1}/${steps.size}: ${steps.getOrNull(currentStepIndex)?.first ?: "No steps yet."}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                        enabled = currentStepIndex > 0
                    ) {
                        Text("Previous")
                    }

                    Button(
                        onClick = { if (currentStepIndex < steps.size - 1) currentStepIndex++ },
                        enabled = currentStepIndex < steps.size - 1
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}}

// DFS Traversal Logic
fun dfsTraversal(
    adjacencyList: Map<String, List<String>>,
    startNode: String,
    visited: MutableSet<String>,
    stack: MutableList<String>,
    steps: MutableList<Pair<String, Pair<List<String>, List<String>>>>,
    nodeVisitOrder: MutableList<String> // Store traversal order
) {
    stack.add(startNode)
    steps.add("Start DFS traversal at node $startNode." to (stack.toList() to visited.toList()))

    while (stack.isNotEmpty()) {
        val currentNode = stack.removeAt(stack.size - 1)

        if (!visited.contains(currentNode)) {
            visited.add(currentNode)
            nodeVisitOrder.add(currentNode) // Record the node's visit
            steps.add("Visited node $currentNode." to (stack.toList() to visited.toList()))

            adjacencyList[currentNode]?.reversed()?.forEach { neighbor ->
                if (!visited.contains(neighbor)) {
                    stack.add(neighbor)
                    steps.add("Pushed $neighbor to stack." to (stack.toList() to visited.toList()))
                }
            }
        } else {
            steps.add("Backtracking from $currentNode as it's already visited." to (stack.toList() to visited.toList()))
        }
    }
    steps.add("Traversal Completed! Order: ${nodeVisitOrder.joinToString(", ")}" to (stack.toList() to visited.toList()))
}


@Composable
fun StackVisualization(stack: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(80.dp) // Fixed width for stack visualization
            .background(Color.LightGray)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween, // Reserves space from top to bottom
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stack label at the top
        Text(
            text = "Stack",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f)) // Spacer to push items to the bottom

        // Display stack from bottom to top
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            stack.reversed().forEach { element -> // Reverse to display bottom-to-top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = element,
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Graph Visualization with Dynamic Node Placement
@Composable
fun GraphVisualization(
    adjacencyList: Map<String, List<String>>,
    currentStep: Pair<String, Pair<List<String>, List<String>>>?,
    modifier: Modifier = Modifier
) {
    val visitedNodes = currentStep?.second?.second ?: emptyList()

    Box(modifier = modifier.background(Color.White)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val nodeCount = adjacencyList.size
            val radius = 40f
            val canvasSize = size
            val centerX = canvasSize.width / 2
            val centerY = canvasSize.height / 2
            val nodePositions = mutableMapOf<String, Offset>()

            // Calculate positions for nodes in a circular layout
            adjacencyList.keys.forEachIndexed { index, node ->
                val angle = 2 * Math.PI * index / nodeCount
                val x = (centerX + 200 * Math.cos(angle)).toFloat()
                val y = (centerY + 200 * Math.sin(angle)).toFloat()
                nodePositions[node] = Offset(x, y)
            }

            // Draw edges
            adjacencyList.forEach { (node, neighbors) ->
                val start = nodePositions[node] ?: return@forEach
                neighbors.forEach { neighbor ->
                    val end = nodePositions[neighbor] ?: return@forEach
                    drawLine(
                        color = Color.Black,
                        start = start,
                        end = end,
                        strokeWidth = 2f
                    )
                }
            }

            // Draw nodes
            nodePositions.forEach { (node, position) ->
                drawCircle(
                    color = if (visitedNodes.contains(node)) Color.Green else Color.Gray,
                    center = position,
                    radius = radius
                )
                drawContext.canvas.nativeCanvas.drawText(
                    node,
                    position.x - 15f,
                    position.y + 15f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 40f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}


//BFS

@Composable
fun BFSGraphTraversal(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<List<String>, List<String>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var visitedOrder by remember { mutableStateOf(listOf<String>()) }
    var traversalComplete by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    fun parseAdjacencyList(input: String): Map<String, List<String>> {
        return try {
            input.split(";").associate { entry ->
                val (node, neighbors) = entry.split(":").let {
                    it[0] to it.getOrElse(1) { "" }.split(",").filter { it.isNotEmpty() }
                }
                node to neighbors
            }
        } catch (e: Exception) {
            errorText = "Invalid input format! Please use (A:B,C;B:D;C:D;D) format"
            emptyMap()
        }
    }

    fun runBFS() {
        if (inputText.isBlank()) {
            errorText = "Please enter a valid adjacency list"
            return
        }
        val visited = mutableSetOf<String>()
        val queue = mutableListOf<String>()
        val stepList = mutableListOf<Pair<String, Pair<List<String>, List<String>>>>()
        val nodeVisitOrder = mutableListOf<String>()

        if (adjacencyList.isNotEmpty()) {
            bfsTraversal(adjacencyList, adjacencyList.keys.first(), visited, queue, stepList, nodeVisitOrder)
        }

        steps = stepList
        visitedOrder = nodeVisitOrder
        currentStepIndex = 0

        traversalComplete = true
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // This Box keeps the IconButton fixed at the top
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart) // Make sure it's aligned at the top left
                .zIndex(1f) // Keep the button on top of other elements
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable { dismissKeyboard() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(50.dp))
        Text(
            text = "BFS Visualization",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Adjacency List (e.g., A:B,C;B:D;C:D;D)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorText.isNotEmpty()
        )
        if (errorText.isNotEmpty()) {
            Text(errorText, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                dismissKeyboard()
                errorText = ""
                adjacencyList = parseAdjacencyList(inputText)
                runBFS()
            }
        ) {
            Text("Visualize Graph")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph Visualization
        if (adjacencyList.isNotEmpty()) {
            GraphVisualization(
                adjacencyList = adjacencyList,
                currentStep = steps.getOrNull(currentStepIndex),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display Messages Below Graph
            if (steps.isNotEmpty()) {
                Text(
                    text = "Step ${currentStepIndex + 1}/${steps.size}: ${
                        steps.getOrNull(
                            currentStepIndex
                        )?.first ?: "No steps yet."
                    }",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Queue Visualization Below Messages
            HorizontalQueueVisualization(
                queue = steps.getOrNull(currentStepIndex)?.second?.first ?: emptyList(),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step navigation
        if (steps.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                    enabled = currentStepIndex > 0
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = { if (currentStepIndex < steps.size - 1) currentStepIndex++ },
                    enabled = currentStepIndex < steps.size - 1
                ) {
                    Text("Next")
                }
            }
        }
    }

    }
}

fun bfsTraversal(
    adjacencyList: Map<String, List<String>>,
    startNode: String,
    visited: MutableSet<String>,
    queue: MutableList<String>,
    steps: MutableList<Pair<String, Pair<List<String>, List<String>>>>,
    nodeVisitOrder: MutableList<String>
) {
    queue.add(startNode)
    steps.add("Start BFS traversal at node $startNode." to (queue.toList() to visited.toList()))

    while (queue.isNotEmpty()) {
        val currentNode = queue.removeAt(0)

        if (!visited.contains(currentNode)) {
            visited.add(currentNode)
            nodeVisitOrder.add(currentNode)
            steps.add("Visited node $currentNode." to (queue.toList() to visited.toList()))

            adjacencyList[currentNode]?.forEach { neighbor ->
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor)
                    steps.add("Added $neighbor to queue." to (queue.toList() to visited.toList()))
                }
            }
        }
    }

    steps.add("Traversal Completed! Order: ${nodeVisitOrder.joinToString(", ")}" to (queue.toList() to visited.toList()))
}



@Composable
fun HorizontalQueueVisualization(queue: List<String>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Color.LightGray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Queue:", style = MaterialTheme.typography.bodyLarge)

        queue.forEach { element ->
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = element,
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}

//Topological sorting....

@Composable
fun TopologicalSortVisualization(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, List<String>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var sortingMethod by remember { mutableStateOf("DFS") }
    var result by remember { mutableStateOf(listOf<String>()) }
    var isSorting by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    fun parseAdjacencyList(input: String): Map<String, List<String>> {
        return try {
            input.split(";").associate { entry ->
                val (node, neighbors) = entry.split(":").let {
                    it[0] to it.getOrElse(1) { "" }.split(",").filter { it.isNotEmpty() }
                }
                node to neighbors
            }
        } catch (e: Exception) {
            errorText = "Invalid input format! Please use format: A:B,C;B:D;C:D;D"
            emptyMap()
        }
    }
    fun runTopologicalSort() {
        // Check if input is valid
        if (inputText.isBlank()) {
            errorText = "Please enter a valid adjacency list."
            return
        }

        // Initialize variables
        val visited = mutableSetOf<String>()
        result = mutableListOf()
        val stepList = mutableListOf<Pair<String, List<String>>>()
        var cycleDetected = false

        // DFS implementation
        fun dfs(node: String, tempVisited: MutableSet<String>) {
            if (node in tempVisited) {
                errorText = "Cycle detected! Sorting is not possible."
                cycleDetected = true
                return
            }

            if (node !in visited) {
                val neighbors = adjacencyList[node]?.joinToString(", ") ?: "None"
                stepList.add("Processing node $node with neighbors: $neighbors" to result.toList())

                // Mark node as temporarily visited to detect cycles
                tempVisited.add(node)

                // Traverse all the neighbors of the current node
                adjacencyList[node]?.forEach { neighbor ->
                    dfs(neighbor, tempVisited)
                }

                // After finishing the traversal of neighbors
                tempVisited.remove(node)
                visited.add(node)

                // Add node to the result (sorted list) and log the message
                result = listOf(node) + result // Add node to the front of the result

                // Log the node's addition to the sorted list
                stepList.add(
                    "Node $node is added to the sorted list because all its dependencies (neighbors: $neighbors) have been processed."
                            to result.toList()
                )
            }
        }

        // Source removal method
        fun sourceRemoval() {
            val inDegree = mutableMapOf<String, Int>().apply {
                adjacencyList.keys.forEach { put(it, 0) }
            }

            // Compute in-degrees
            adjacencyList.forEach { (_, neighbors) ->
                neighbors.forEach { neighbor ->
                    inDegree[neighbor] = inDegree.getOrDefault(neighbor, 0) + 1
                }
            }

            val queue = mutableListOf<String>().apply {
                addAll(inDegree.filter { it.value == 0 }.keys)
            }

            val localAdjacencyList = adjacencyList.toMutableMap()
            val visitedNodes = mutableSetOf<String>() // Track visited nodes to avoid double-processing

            result = emptyList() // Initialize the result list

            while (queue.isNotEmpty()) {
                val node = queue.removeAt(0)
                if (visitedNodes.contains(node)) continue // Skip if already processed
                visitedNodes.add(node)

                val queueState = queue.joinToString(", ").ifEmpty { "Empty" }
                stepList.add("Node $node is removed from the graph (Queue: $queueState)" to result.toList())

                result = result + node // Add to sorted list
                stepList.add("Node $node is added to the sorted list." to result.toList())

                // Remove edges from the node
                localAdjacencyList[node]?.forEach { neighbor ->
                    inDegree[neighbor] = inDegree[neighbor]!! - 1
                    stepList.add("Edge from $node to $neighbor is removed." to result.toList())

                    if (inDegree[neighbor] == 0) {
                        queue.add(neighbor)
                        val updatedQueue = queue.joinToString(", ")
                        stepList.add("Node $neighbor is added to the queue (Queue: $updatedQueue)" to result.toList())
                    }
                }

                localAdjacencyList.remove(node)
            }

            // Check for cycles
            if (visitedNodes.size != adjacencyList.keys.size) {
                errorText = "Cycle detected! Sorting is not possible."
                cycleDetected = true
            }
        }

        // Run the selected sorting method
        if (sortingMethod == "DFS") {
            adjacencyList.keys.forEach { node ->
                if (!cycleDetected && node !in visited) {
                    dfs(node, mutableSetOf())
                }
            }
        } else {
            sourceRemoval()
        }

        // Update state after sorting
        if (!cycleDetected) {
            // Add the sorting complete message only at the last step
            stepList.add("Sorting complete." to result.toList())
        }

        steps = stepList
        currentStepIndex = 0
        isSorting = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // This Box keeps the IconButton fixed at the top
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart) // Make sure it's aligned at the top left
                .zIndex(1f) // Keep the button on top of other elements
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

    // UI Components
    Column(
        modifier = Modifier
            .clickable{dismissKeyboard()}
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Topological Sort Visualization",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Adjacency List (e.g., A:B,C;B:D;C:D;D)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorText.isNotEmpty()
        )

        if (errorText.isNotEmpty()) {
            Text(errorText, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    dismissKeyboard()
                    sortingMethod = "DFS"
                    errorText = ""
                },
                colors = if (sortingMethod == "DFS") ButtonDefaults.buttonColors(containerColor = Color.Gray) else ButtonDefaults.buttonColors()
            ) {
                Text("Use DFS")
            }

            Button(
                onClick = {
                    dismissKeyboard()
                    sortingMethod = "Source Removal"
                    errorText = ""
                },
                colors = if (sortingMethod == "Source Removal") ButtonDefaults.buttonColors(containerColor = Color.Gray) else ButtonDefaults.buttonColors()
            ) {
                Text("Use Source Removal")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                dismissKeyboard()
                errorText = ""
                adjacencyList = parseAdjacencyList(inputText)
                runTopologicalSort()
                isSorting = true
            }
        ) {
            Text("Visualize Sorting")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph Visualization
        if (adjacencyList.isNotEmpty() && isSorting) {
            GraphVisualization(
                adjacencyList = adjacencyList,
                currentStep = steps.getOrNull(currentStepIndex)?.second ?: emptyList(),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display Messages Below Graph
            if (steps.isNotEmpty()) {
                Text(
                    text = "Step ${currentStepIndex + 1}/${steps.size}: ${steps.getOrNull(currentStepIndex)?.first ?: "No steps yet."}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Sorted List: ${steps.getOrNull(currentStepIndex)?.second?.joinToString() ?: "None"}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step navigation
        if (steps.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                    enabled = currentStepIndex > 0
                ) {
                    Text("Previous Step")
                }

                Button(
                    onClick = { if (currentStepIndex < steps.size - 1) currentStepIndex++ },
                    enabled = currentStepIndex < steps.size - 1
                ) {
                    Text("Next Step")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Button
//        Button(
//            onClick = {
//                adjacencyList = emptyMap()
//                inputText = ""
//                errorText = ""
//                steps = emptyList()
//                result = emptyList()
//                currentStepIndex = 0
//                isSorting = false
//            }
//        ) {
//            Text("Reset")
//        }
    }}
}
@Composable
fun GraphVisualization(
    adjacencyList: Map<String, List<String>>,
    currentStep: List<String>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(Color.White)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val nodeCount = adjacencyList.size
            val radius = 40f
            val arrowHeadSize = 20f
            val canvasSize = size
            val centerX = canvasSize.width / 2
            val centerY = canvasSize.height / 2
            val nodePositions = mutableMapOf<String, Offset>()

            // Calculate positions for nodes in a circular layout
            adjacencyList.keys.forEachIndexed { index, node ->
                val angle = 2 * Math.PI * index / nodeCount
                val x = (centerX + 200 * cos(angle)).toFloat()
                val y = (centerY + 200 * sin(angle)).toFloat()
                nodePositions[node] = Offset(x, y)
            }

            // Draw edges
            adjacencyList.forEach { (node, neighbors) ->
                val start = nodePositions[node] ?: return@forEach
                neighbors.forEach { neighbor ->
                    val end = nodePositions[neighbor] ?: return@forEach
                    drawArrow(start, end, radius, arrowHeadSize)
                }
            }

            // Draw nodes
            nodePositions.forEach { (node, position) ->
                drawCircle(
                    color = if (currentStep.contains(node)) Color.Green else Color.Gray,
                    center = position,
                    radius = radius
                )
                drawContext.canvas.nativeCanvas.drawText(
                    node,
                    position.x,
                    position.y + 15f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 40f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}

// Helper function to draw an arrow with an adjustable radius and arrowhead size
fun DrawScope.drawArrow(start: Offset, end: Offset, radius: Float, arrowHeadSize: Float) {
    val direction = (end - start).normalize()
    val adjustedStart = start + direction * radius
    val adjustedEnd = end - direction * radius

    // Draw the main line
    drawLine(
        color = Color.Black,
        start = adjustedStart,
        end = adjustedEnd,
        strokeWidth = 2f
    )

    // Calculate arrowhead points
    val angle = Math.atan2(
        (adjustedEnd.y - adjustedStart.y).toDouble(),
        (adjustedEnd.x - adjustedStart.x).toDouble()
    )
    val leftArrowPoint = Offset(
        (adjustedEnd.x - arrowHeadSize * cos(angle - Math.PI / 6)).toFloat(),
        (adjustedEnd.y - arrowHeadSize * sin(angle - Math.PI / 6)).toFloat()
    )
    val rightArrowPoint = Offset(
        (adjustedEnd.x - arrowHeadSize * cos(angle + Math.PI / 6)).toFloat(),
        (adjustedEnd.y - arrowHeadSize * sin(angle + Math.PI / 6)).toFloat()
    )

    // Draw the arrowhead
    drawLine(
        color = Color.Black,
        start = adjustedEnd,
        end = leftArrowPoint,
        strokeWidth = 2f
    )
    drawLine(
        color = Color.Black,
        start = adjustedEnd,
        end = rightArrowPoint,
        strokeWidth = 2f
    )
}

// Extension function to normalize an Offset vector
private fun Offset.normalize(): Offset {
    val length = hypot(x, y)
    return if (length > 0f) Offset(x / length, y / length) else Offset.Zero
}