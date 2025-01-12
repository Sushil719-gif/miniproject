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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import java.util.PriorityQueue
import kotlin.math.cos
import kotlin.math.sin


//GBFS..

@Composable
fun GBFSGraphTraversal(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, Map<String, Int>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<List<String>, List<String>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var heuristicText by remember { mutableStateOf("") }
    var startGoalInput by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var visitedOrder by remember { mutableStateOf(listOf<String>()) }
    var traversalComplete by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

// Dismiss keyboard when tapping outside any input field or button
val dismissKeyboard: () -> Unit = {
    keyboardController?.hide()
}

    fun parseAdjacencyList(input: String): Map<String, Map<String, Int>> {
        return try {
            input.split(";").associate { entry ->
                val parts = entry.split(":").map { it.trim() }
                val node = parts[0] // The node key
                val neighbors = if (parts.size > 1) {
                    parts[1].split(",").associateWith { 1 } // Default weight to 1 for all neighbors
                } else {
                    emptyMap() // No neighbors if ':' is not followed by values
                }
                node to neighbors
            }
        } catch (e: Exception) {
            println("Invalid input format! Please use the format: A:B,C;B:D;C:D;D (Variable before : is the node and" +
                    "variables after : are neighbours ")
            emptyMap()
        }
    }



    fun parseHeuristic(input: String): Map<String, Int> {
        return try {
            input.split(",").associate {
                val (node, value) = it.trim().split(":")
                node to value.toInt()
            }
        } catch (e: Exception) {
            errorText = "Invalid heuristic format! Please use (A:2,B:3,C:1) format"
            emptyMap()
        }
    }

    fun parseStartAndGoal(input: String): Pair<String, String>? {
        return try {
            val (start, goal) = input.split("-").map { it.trim() }
            start to goal
        } catch (e: Exception) {
            errorText = "Invalid start-goal format! Please use (A-D) format"
            null
        }
    }

    fun runGBFS() {
        if (inputText.isBlank() || heuristicText.isBlank() || startGoalInput.isBlank()) {
            errorText = "Please enter a valid adjacency list, heuristic values, and start-goal nodes"
            return
        }

        val adjacencyListParsed = parseAdjacencyList(inputText)
        val heuristicParsed = parseHeuristic(heuristicText)
        val startGoalParsed = parseStartAndGoal(startGoalInput)

        if (adjacencyListParsed.isEmpty() || heuristicParsed.isEmpty() || startGoalParsed == null) {
            return
        }

        val (startNode, goalNode) = startGoalParsed
        val visited = mutableSetOf<String>()
        val stepList = mutableListOf<Pair<String, Pair<List<String>, List<String>>>>()
        val nodeVisitOrder = mutableListOf<String>()
        val priorityQueue = PriorityQueue<Pair<String, Int>>(compareBy { it.second })

        if (adjacencyListParsed.isNotEmpty()) {
            gbfsTraversal(
                adjacencyListParsed, heuristicParsed, startNode, goalNode, visited, priorityQueue, stepList, nodeVisitOrder
            )
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
            .clickable{dismissKeyboard()}
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(50.dp))
        Text(
            text = "Greedy Best First Search Visualization",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Adjacency List (e.g., A:B,C;B:D;C:D;D)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorText.isNotEmpty(),
            singleLine = true
        )


        TextField(
            value = heuristicText,
            onValueChange = { heuristicText = it },
            label = { Text("Enter Heuristic Values (e.g., A:2,B:3,C:1,D:0)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorText.isNotEmpty(),
            singleLine = true
        )

        TextField(
            value = startGoalInput,
            onValueChange = { startGoalInput = it },
            label = { Text("Enter Start and Goal Nodes (e.g., A-D)") },
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
                runGBFS()
            }
        ) {
            Text("Visualize Graph")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (adjacencyList.isNotEmpty()) {
            GraphVisualization(
                adjacencyList = adjacencyList,
                currentStep = steps.getOrNull(currentStepIndex),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (steps.isNotEmpty()) {
                Text(
                    text = "Step ${currentStepIndex + 1}/${steps.size}: ${steps.getOrNull(currentStepIndex)?.first ?: "No steps yet."}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            HorizontalQueueVisualization1(
                queue = steps.getOrNull(currentStepIndex)?.second?.first ?: emptyList(),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
}}

fun gbfsTraversal(
    adjacencyList: Map<String, Map<String, Int>>,
    heuristic: Map<String, Int>,
    startNode: String,
    goalNode: String,
    visited: MutableSet<String>,
    priorityQueue: PriorityQueue<Pair<String, Int>>,
    steps: MutableList<Pair<String, Pair<List<String>, List<String>>>>,
    nodeVisitOrder: MutableList<String>
) {
    priorityQueue.add(startNode to (heuristic[startNode] ?: Int.MAX_VALUE))

    while (priorityQueue.isNotEmpty()) {
        val (currentNode, currentCost) = priorityQueue.poll()

        steps.add(
            "Considering node $currentNode with heuristic value $currentCost." to
                    (priorityQueue.map { it.first }.toList() to visited.toList())
        )

        if (currentNode == goalNode) {
            if (!visited.contains(currentNode)) {
                visited.add(currentNode)
                nodeVisitOrder.add(currentNode)
                steps.add(
                    "Reached goal node $currentNode. Added to the final path as it is the destination." to
                            (priorityQueue.map { it.first }.toList() to visited.toList())
                )
            }
            break
        }

        if (!visited.contains(currentNode)) {
            visited.add(currentNode)
            nodeVisitOrder.add(currentNode)
            steps.add(
                "Visited node $currentNode. Added to the final path because it has the lowest heuristic value and leads towards the goal." to
                        (priorityQueue.map { it.first }.toList() to visited.toList())
            )

            adjacencyList[currentNode]?.forEach { (neighbor, _) ->
                if (!visited.contains(neighbor)) {
                    val neighborCost = heuristic[neighbor] ?: Int.MAX_VALUE
                    priorityQueue.add(neighbor to neighborCost)
                    steps.add(
                        "Added $neighbor to the priority queue with heuristic value $neighborCost because it is connected to $currentNode and has not been visited yet." to
                                (priorityQueue.map { it.first }.toList() to visited.toList())
                    )
                } else {
                    steps.add(
                        "$neighbor was not added to the queue because it has already been visited." to
                                (priorityQueue.map { it.first }.toList() to visited.toList())
                    )
                }
            }
        } else {
            steps.add(
                "$currentNode was not visited again because it is already in the visited set." to
                        (priorityQueue.map { it.first }.toList() to visited.toList())
            )
        }
    }

    // Ensure the final path (including goal node) is highlighted
    if (!nodeVisitOrder.contains(goalNode)) {
        nodeVisitOrder.add(goalNode)
        visited.add(goalNode)
    }
    steps.add(
        "Traversal Completed! \n Final Path: ${nodeVisitOrder.joinToString(" -> ")}. \n The nodes in the path were chosen because they minimized the heuristic cost towards the goal." to
                (priorityQueue.map { it.first }.toList() to visited.toList())
    )
}



@Composable
fun HorizontalQueueVisualization1(queue: List<String>, modifier: Modifier = Modifier) {
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

@Composable
fun GraphVisualization(
    adjacencyList: Map<String, Map<String, Int>>,
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

            adjacencyList.keys.forEachIndexed { index, node ->
                val angle = 2 * Math.PI * index / nodeCount
                val x = (centerX + 200 * cos(angle)).toFloat()
                val y = (centerY + 200 * sin(angle)).toFloat()
                nodePositions[node] = Offset(x, y)
            }

            adjacencyList.forEach { (node, neighbors) ->
                val start = nodePositions[node] ?: return@forEach
                neighbors.forEach { (neighbor, _) ->
                    val end = nodePositions[neighbor] ?: return@forEach
                    drawLine(color = Color.Black, start = start, end = end, strokeWidth = 2f)
                }
            }

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


//Astar..


@Composable
fun AStarGraphTraversal(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, Map<String, Int>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<List<String>, List<String>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var heuristicText by remember { mutableStateOf("") }
    var startGoalInput by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var visitedOrder by remember { mutableStateOf(listOf<String>()) }
    var traversalComplete by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

// Dismiss keyboard when tapping outside any input field or button
val dismissKeyboard: () -> Unit = {
    keyboardController?.hide()
}

    fun parseAdjacencyList(input: String): Map<String, Map<String, Int>> {
        return try {
            input.split(";").associate { entry ->
                val parts = entry.split(":").map { it.trim() }
                val node = parts[0] // The node key
                val neighbors = if (parts.size > 1) {
                    parts[1].split(",").associate {
                        val neighborParts = it.split(" ")
                        neighborParts[0] to neighborParts[1].toInt() // Edge weight
                    }
                } else {
                    emptyMap() // No neighbors if ':' is not followed by values
                }
                node to neighbors
            }
        } catch (e: Exception) {
            println("Invalid input format! Please use the format: A:B 2,C 4;B:D 6;C:D 7;D (Node, Neighbor and edge weight)")
            emptyMap()
        }
    }

    fun parseHeuristic(input: String): Map<String, Int> {
        return try {
            input.split(",").associate {
                val (node, value) = it.trim().split(":")
                node to value.toInt()
            }
        } catch (e: Exception) {
            errorText = "Invalid heuristic format! Please use (A:2,B:3,C:1,D:0) format"
            emptyMap()
        }
    }

    fun parseStartAndGoal(input: String): Pair<String, String>? {
        return try {
            val (start, goal) = input.split("-").map { it.trim() }
            start to goal
        } catch (e: Exception) {
            errorText = "Invalid start-goal format! Please use (A-D) format"
            null
        }
    }

    fun runAStar() {
        if (inputText.isBlank() || heuristicText.isBlank() || startGoalInput.isBlank()) {
            errorText = "Please enter a valid adjacency list, heuristic values, and start-goal nodes"
            return
        }

        val adjacencyListParsed = parseAdjacencyList(inputText)
        val heuristicParsed = parseHeuristic(heuristicText)
        val startGoalParsed = parseStartAndGoal(startGoalInput)

        if (adjacencyListParsed.isEmpty() || heuristicParsed.isEmpty() || startGoalParsed == null) {
            return
        }

        val (startNode, goalNode) = startGoalParsed
        val visited = mutableSetOf<String>()
        val stepList = mutableListOf<Pair<String, Pair<List<String>, List<String>>>>()
        val nodeVisitOrder = mutableListOf<String>()
        val priorityQueue = PriorityQueue<Pair<String, Int>>(compareBy { it.second })

        if (adjacencyListParsed.isNotEmpty()) {
            aStarTraversal(
                adjacencyListParsed, heuristicParsed, startNode, goalNode, visited, priorityQueue, stepList, nodeVisitOrder
            )
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
            .clickable{dismissKeyboard()}
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier= Modifier.height(50.dp))
        Text(
            text = "A* Search Visualization",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Adjacency List (e.g., A:B 2,C 4;B:D 6;C:D 7;D)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorText.isNotEmpty(),
            singleLine = true
        )

        TextField(
            value = heuristicText,
            onValueChange = { heuristicText = it },
            label = { Text("Enter Heuristic Values (e.g., A:2,B:3,C:1,D:0)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorText.isNotEmpty(),
            singleLine = true
        )

        TextField(
            value = startGoalInput,
            onValueChange = { startGoalInput = it },
            label = { Text("Enter Start and Goal Nodes (e.g., A-D)") },
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
                runAStar()
            }
        ) {
            Text("Visualize Graph")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (adjacencyList.isNotEmpty()) {
            GraphVisualization1(
                adjacencyList = adjacencyList,
                currentStep = steps.getOrNull(currentStepIndex),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (steps.isNotEmpty()) {
                Text(
                    text = "Step ${currentStepIndex + 1}/${steps.size}: ${steps.getOrNull(currentStepIndex)?.first ?: "No steps yet."}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            HorizontalQueueVisualization2(
                queue = steps.getOrNull(currentStepIndex)?.second?.first ?: emptyList(),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
fun aStarTraversal(
    adjacencyList: Map<String, Map<String, Int>>,
    heuristic: Map<String, Int>,
    startNode: String,
    goalNode: String,
    visited: MutableSet<String>,
    priorityQueue: PriorityQueue<Pair<String, Int>>,
    steps: MutableList<Pair<String, Pair<List<String>, List<String>>>>,
    nodeVisitOrder: MutableList<String>
) {
    val costFromStart = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
    val estimatedCost = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
    val cameFrom = mutableMapOf<String, String>()

    priorityQueue.add(startNode to 0)
    costFromStart[startNode] = 0
    estimatedCost[startNode] = heuristic[startNode] ?: Int.MAX_VALUE

    while (priorityQueue.isNotEmpty()) {
        val (currentNode, _) = priorityQueue.poll()

        if (currentNode == startNode) {
            steps.add(
                "Starting traversal from node $currentNode with total estimated cost ${
                    costFromStart[currentNode]?.plus(heuristic[currentNode] ?: Int.MAX_VALUE)
                } (costFromStart(${costFromStart[currentNode]}) + heuristic(${heuristic[currentNode] ?: Int.MAX_VALUE}))." to
                        (priorityQueue.map { it.first }.toList() to visited.toList())
            )
        } else {
            steps.add(
                "Considering node $currentNode with total estimated cost ${
                    costFromStart[currentNode]?.plus(heuristic[currentNode] ?: Int.MAX_VALUE)
                } (costFromStart(${costFromStart[currentNode]}) + heuristic(${heuristic[currentNode] ?: Int.MAX_VALUE}))." to
                        (priorityQueue.map { it.first }.toList() to visited.toList())
            )
        }

        if (currentNode == goalNode) {
            if (!visited.contains(currentNode)) {
                visited.add(currentNode)
                steps.add(
                    "Reached goal node $currentNode. Added to the final path as it is the destination." to
                            (priorityQueue.map { it.first }.toList() to visited.toList())
                )
            }
            break
        }

        if (!visited.contains(currentNode)) {
            visited.add(currentNode)

            if (currentNode == startNode) {
                steps.add(
                    "Visited node $currentNode. As the starting node, it is naturally part of the final path." to
                            (priorityQueue.map { it.first }.toList() to visited.toList())
                )
            } else {
                steps.add(
                    "Visited node $currentNode. It is being considered with total cost ${
                        costFromStart[currentNode]
                    } and heuristic ${heuristic[currentNode] ?: Int.MAX_VALUE}, but it may not be part of the final path." to
                            (priorityQueue.map { it.first }.toList() to visited.toList())
                )
            }

            adjacencyList[currentNode]?.forEach { (neighbor, edgeWeight) ->
                if (!visited.contains(neighbor)) {
                    val g = costFromStart[currentNode]!! + edgeWeight
                    val h = heuristic[neighbor] ?: Int.MAX_VALUE
                    val f = g + h

                    if (f < (costFromStart[neighbor] ?: Int.MAX_VALUE)) {
                        costFromStart[neighbor] = g
                        estimatedCost[neighbor] = h
                        priorityQueue.add(neighbor to f)
                        cameFrom[neighbor] = currentNode
                        steps.add(
                            "Added $neighbor to the priority queue with estimated cost $f (costFromStart(${g}) + heuristic(${h})). It is connected to $currentNode with edge weight $edgeWeight." to
                                    (priorityQueue.map { it.first }.toList() to visited.toList())
                        )
                    }
                }
            }
        }
    }

    // Reconstruct the shortest path
    val shortestPath = mutableListOf<String>()
    var current = goalNode
    while (current != startNode) {
        shortestPath.add(0, current)
        current = cameFrom[current] ?: break
    }
    shortestPath.add(0, startNode)

    nodeVisitOrder.clear()
    nodeVisitOrder.addAll(shortestPath)

    // Highlight only nodes in the shortest path
    val shortestPathSet = nodeVisitOrder.toSet()
    visited.retainAll(shortestPathSet)

    // Final step logging
    steps.add(
        "Traversal completed! Final Path: ${nodeVisitOrder.joinToString(" -> ")}. Only the nodes in the final path are highlighted." to
                (priorityQueue.map { it.first }.toList() to visited.toList())
    )
}




@Composable
fun HorizontalQueueVisualization2(queue: List<String>, modifier: Modifier = Modifier) {
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

@Composable
fun GraphVisualization1(
    adjacencyList: Map<String, Map<String, Int>>,
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

            adjacencyList.keys.forEachIndexed { index, node ->
                val angle = 2 * Math.PI * index / nodeCount
                val x = (centerX + 200 * cos(angle)).toFloat()
                val y = (centerY + 200 * sin(angle)).toFloat()
                nodePositions[node] = Offset(x, y)
            }

            adjacencyList.forEach { (node, neighbors) ->
                val start = nodePositions[node] ?: return@forEach
                neighbors.forEach { (neighbor, _) ->
                    val end = nodePositions[neighbor] ?: return@forEach
                    drawLine(color = Color.Black, start = start, end = end, strokeWidth = 2f)
                }
            }

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

//IDAstar..


@Composable
fun IDAStarGraphTraversal(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, Map<String, Int>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<List<String>, List<String>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var heuristicText by remember { mutableStateOf("") }
    var startGoalInput by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var visitedOrder by remember { mutableStateOf(listOf<String>()) }
    var traversalComplete by remember { mutableStateOf(false) }
    var shortestPath by remember { mutableStateOf(listOf<String>()) }
    val keyboardController = LocalSoftwareKeyboardController.current

// Dismiss keyboard when tapping outside any input field or button
val dismissKeyboard: () -> Unit = {
    keyboardController?.hide()
}

    fun parseAdjacencyList(input: String): Map<String, Map<String, Int>> {
        return try {
            input.split(";").associate { entry ->
                val parts = entry.split(":").map { it.trim() }
                val node = parts[0]
                val neighbors = if (parts.size > 1) {
                    parts[1].split(",").associate {
                        val (neighbor, weight) = it.split("-").map { part -> part.trim() }
                        neighbor to weight.toInt()
                    }
                } else {
                    emptyMap()
                }
                node to neighbors
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun parseHeuristic(input: String): Map<String, Int> {
        return try {
            input.split(",").associate {
                val (node, value) = it.trim().split(":")
                node to value.toInt()
            }
        } catch (e: Exception) {
            errorText = "Invalid heuristic format! Ensure it follows this pattern: A:2,B:3,C:1"
            emptyMap()
        }
    }

    fun parseStartAndGoal(input: String): Pair<String, String>? {
        return try {
            val (start, goal) = input.split("-").map { it.trim() }
            start to goal
        } catch (e: Exception) {
            errorText = "Invalid start-goal format! Use the pattern: A-D"
            null
        }
    }

    fun runIDAStar() {
        if (inputText.isBlank() || heuristicText.isBlank() || startGoalInput.isBlank()) {
            errorText = "Please provide an adjacency list, heuristic values, and start-goal nodes."
            return
        }

        val adjacencyListParsed = parseAdjacencyList(inputText)
        val heuristicParsed = parseHeuristic(heuristicText)
        val startGoalParsed = parseStartAndGoal(startGoalInput)

        if (adjacencyListParsed.isEmpty() || heuristicParsed.isEmpty() || startGoalParsed == null) {
            return
        }

        val (startNode, goalNode) = startGoalParsed
        val visited = mutableSetOf<String>()
        val stepList = mutableListOf<Pair<String, Pair<List<String>, List<String>>>>()
        val nodeVisitOrder = mutableListOf<String>()
        val path = mutableListOf<String>()

        idaStarTraversal(
            adjacencyListParsed,
            heuristicParsed,
            startNode,
            goalNode,
            visited,
            stepList,
            nodeVisitOrder,
            path
        )

        // Set the state after traversal completes
        steps = stepList
        visitedOrder = nodeVisitOrder
        shortestPath = path
        currentStepIndex = 0
        traversalComplete = true // Set traversalComplete to true after the search is finished
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
                .clickable{dismissKeyboard()}
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "IDA* Search Visualization",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Adjacency List (e.g., A:B-1,C-2;B:D-3;C:D-2;D)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorText.isNotEmpty(),
                singleLine = true
            )

            TextField(
                value = heuristicText,
                onValueChange = { heuristicText = it },
                label = { Text("Heuristic Values (e.g., A:2,B:3,C:1,D:0)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorText.isNotEmpty(),
                singleLine = true
            )

            TextField(
                value = startGoalInput,
                onValueChange = { startGoalInput = it },
                label = { Text("Start and Goal Nodes (e.g., A-D)") },
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
                    runIDAStar()
                }
            ) {
                Text("Visualize Graph")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (adjacencyList.isNotEmpty()) {
                GraphVisualization(
                    adjacencyList = adjacencyList,
                    currentStep = steps.getOrNull(currentStepIndex),
                    shortestPath = shortestPath,
                    traversalComplete = traversalComplete, // Pass the state here
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )


                Spacer(modifier = Modifier.height(16.dp))

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
            }

            Spacer(modifier = Modifier.height(16.dp))

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
fun idaStarTraversal(
    adjacencyList: Map<String, Map<String, Int>>,
    heuristic: Map<String, Int>,
    startNode: String,
    goalNode: String,
    visited: MutableSet<String>,
    steps: MutableList<Pair<String, Pair<List<String>, List<String>>>>,
    nodeVisitOrder: MutableList<String>,
    path: MutableList<String>
) {
    fun search(node: String, g: Int, bound: Int, currentPath: MutableList<String>): Int {
        currentPath.add(node)
        val f = g + (heuristic[node] ?: Int.MAX_VALUE)
        steps.add(
            "Current node: $node, g = $g, heuristic = ${heuristic[node]}, f = $f." to Pair(visited.toList(), currentPath.toList())
        )

        if (f > bound) {
            steps.add(
                "Node $node exceeds current bound $bound (f=$f). Backtracking." to Pair(visited.toList(), currentPath.toList())
            )
            currentPath.remove(node)
            return f
        }
        if (node == goalNode) {
            steps.add(
                "Goal reached at node $node. Traversal completed! Shortest Path: ${currentPath.joinToString(", ")}" to Pair(visited.toList(), currentPath.toList())
            )

            path.clear()
            path.addAll(currentPath) // Set shortest path here
            return -1
        }

        visited.add(node)
        nodeVisitOrder.add(node)
        steps.add(
            "Visiting node $node. Exploring neighbors." to Pair(visited.toList(), currentPath.toList())
        )

        var min = Int.MAX_VALUE
        adjacencyList[node]?.forEach { (neighbor, weight) ->
            if (!visited.contains(neighbor)) {
                steps.add(
                    "Exploring edge $node -> $neighbor with cost $weight." to Pair(visited.toList(), currentPath.toList())
                )
                val t = search(neighbor, g + weight, bound, currentPath)
                if (t == -1) return -1
                min = minOf(min, t)
            } else {
                steps.add(
                    "Neighbor $neighbor already visited. Skipping." to Pair(visited.toList(), currentPath.toList())
                )
            }
        }

        visited.remove(node)
        currentPath.remove(node)
        steps.add(
            "Finished exploring all neighbors of $node. Backtracking." to Pair(visited.toList(), currentPath.toList())
        )
        return min
    }

    var bound = heuristic[startNode] ?: Int.MAX_VALUE
    steps.add(
        "Initial bound set to heuristic value of start node $startNode: $bound." to Pair(listOf(), listOf(startNode))
    ) // Wrap in Pair
    while (true) {
        steps.add("Starting search with bound $bound." to Pair(listOf(), listOf())) // Wrap in Pair
        val t = search(startNode, 0, bound, mutableListOf())
        if (t == -1) {
//            steps.add(
//                "Traversal completed successfully! Goal node $goalNode reached." to Pair(listOf(), listOf())
//            ) // Wrap in Pair
            return
        }
        if (t == Int.MAX_VALUE) {
            steps.add(
                "No solution found. Exceeded all possible bounds." to Pair(listOf(), listOf())
            ) // Wrap in Pair
            return
        }
        steps.add(
            "Increasing bound to $t and retrying." to Pair(listOf(), listOf())
        ) // Wrap in Pair
        bound = t
    }
}



@Composable
fun GraphVisualization(
    adjacencyList: Map<String, Map<String, Int>>,
    currentStep: Pair<String, Pair<List<String>, List<String>>>?,
    shortestPath: List<String>,
    traversalComplete: Boolean,  // Pass the traversalComplete flag
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

            adjacencyList.keys.forEachIndexed { index, node ->
                val angle = 2 * Math.PI * index / nodeCount
                val x = (centerX + 200 * cos(angle)).toFloat()
                val y = (centerY + 200 * sin(angle)).toFloat()
                nodePositions[node] = Offset(x, y)
            }

            adjacencyList.forEach { (node, neighbors) ->
                val start = nodePositions[node] ?: return@forEach
                neighbors.forEach { (neighbor, _) ->
                    val end = nodePositions[neighbor] ?: return@forEach
                    drawLine(color = Color.Black, start = start, end = end, strokeWidth = 2f)
                }
            }

            nodePositions.forEach { (node, position) ->
                drawCircle(
                    color = when {
                        traversalComplete && shortestPath.contains(node) && currentStep == null -> Color.Blue
                        visitedNodes.contains(node) -> Color.Green
                        else -> Color.Gray
                    },
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

//UCS...

@Composable
fun UCSGraphTraversal(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, Map<String, Int>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<List<String>, List<String>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var startGoalInput by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var visitedOrder by remember { mutableStateOf(listOf<String>()) }
    var traversalComplete by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

// Dismiss keyboard when tapping outside any input field or button
val dismissKeyboard: () -> Unit = {
    keyboardController?.hide()
}

    fun parseAdjacencyList(input: String): Map<String, Map<String, Int>> {
        return try {
            input.split(";").associate { entry ->
                val parts = entry.split(":").map { it.trim() }
                val node = parts[0] // The node key
                val neighbors = if (parts.size > 1) {
                    parts[1].split(",").associate {
                        val (neighbor, cost) = it.split("-")
                        neighbor to cost.toInt()
                    }
                } else {
                    emptyMap() // No neighbors if ':' is not followed by values
                }
                node to neighbors
            }
        } catch (e: Exception) {
            errorText = "Invalid input format! Use A:B-2,C-4;B:D-1;C:D-3;D."
            emptyMap()
        }
    }

    fun parseStartAndGoal(input: String): Pair<String, String>? {
        return try {
            val (start, goal) = input.split("-").map { it.trim() }
            start to goal
        } catch (e: Exception) {
            errorText = "Invalid start-goal format! Use A-D."
            null
        }
    }

    fun runUCS() {
        if (inputText.isBlank() || startGoalInput.isBlank()) {
            errorText = "Please enter a valid adjacency list and start-goal nodes."
            return
        }

        val adjacencyListParsed = parseAdjacencyList(inputText)
        val startGoalParsed = parseStartAndGoal(startGoalInput)

        if (adjacencyListParsed.isEmpty() || startGoalParsed == null) {
            return
        }

        val (startNode, goalNode) = startGoalParsed
        val visited = mutableSetOf<String>()
        val stepList = mutableListOf<Pair<String, Pair<List<String>, List<String>>>>()
        val nodeVisitOrder = mutableListOf<String>()
        val priorityQueue = PriorityQueue<Pair<String, Int>>(compareBy { it.second })

        if (adjacencyListParsed.isNotEmpty()) {
            ucsTraversal(
                adjacencyListParsed,
                startNode,
                goalNode,
                visited,
                priorityQueue,
                stepList,
                nodeVisitOrder
            )
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
                .clickable{dismissKeyboard()}
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Uniform Cost Search Visualization",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter Adjacency List (A:B-2,C-4;B:D-1;C:D-3;D)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorText.isNotEmpty(),
                singleLine = true
            )

            TextField(
                value = startGoalInput,
                onValueChange = { startGoalInput = it },
                label = { Text("Enter Start and Goal Nodes (A-D)") },
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
                    runUCS()
                }
            ) {
                Text("Visualize Graph")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (adjacencyList.isNotEmpty()) {
                GraphVisualization3(
                    adjacencyList = adjacencyList,
                    currentStep = steps.getOrNull(currentStepIndex),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                HorizontalQueueVisualization(
                    queue = steps.getOrNull(currentStepIndex)?.second?.first ?: emptyList(),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
fun ucsTraversal(
    adjacencyList: Map<String, Map<String, Int>>,
    startNode: String,
    goalNode: String,
    visited: MutableSet<String>,
    priorityQueue: PriorityQueue<Pair<String, Int>>,
    steps: MutableList<Pair<String, Pair<List<String>, List<String>>>>,
    nodeVisitOrder: MutableList<String>
) {
    val pathCosts = mutableMapOf(startNode to 0)
    priorityQueue.add(startNode to 0)

    while (priorityQueue.isNotEmpty()) {
        val (currentNode, currentCost) = priorityQueue.poll()

        steps.add(
            "Considering node $currentNode with path cost $currentCost," +
                    " as it has low cumulative cost among other nodes in the queue." to
                    (priorityQueue.map { it.first }.toList() to visited.toList())
        )

        if (currentNode == goalNode) {
            if (!visited.contains(currentNode)) {
                visited.add(currentNode)
                nodeVisitOrder.add(currentNode)
                steps.add(
                    "Reached goal node $currentNode. Added to the final path as it is the destination." to
                            (priorityQueue.map { it.first }.toList() to visited.toList())
                )
            }
            break
        }

        if (!visited.contains(currentNode)) {
            visited.add(currentNode)
            nodeVisitOrder.add(currentNode)
            steps.add(
                "Visited node $currentNode. Added to the path." to
                        (priorityQueue.map { it.first }.toList() to visited.toList())
            )

            adjacencyList[currentNode]?.forEach { (neighbor, cost) ->
                if (!visited.contains(neighbor)) {
                    val newCost = currentCost + cost
                    if (newCost < (pathCosts[neighbor] ?: Int.MAX_VALUE)) {
                        pathCosts[neighbor] = newCost
                        priorityQueue.add(neighbor to newCost)
                        steps.add(
                            "Added $neighbor to the queue with cumulative cost $newCost ($currentCost + $cost)." to
                                    (priorityQueue.map { it.first }.toList() to visited.toList())
                        )
                    }
                }
            }
        }
    }

    if (!nodeVisitOrder.contains(goalNode)) {
        nodeVisitOrder.add(goalNode)
        visited.add(goalNode)
    }
    steps.add(
        "Traversal Completed! Final Path: ${nodeVisitOrder.joinToString(" -> ")}." to
                (priorityQueue.map { it.first }.toList() to visited.toList())
    )
}

@Composable
fun HorizontalQueueVisualization3(queue: List<String>, modifier: Modifier = Modifier) {
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

@Composable
fun GraphVisualization3(
    adjacencyList: Map<String, Map<String, Int>>,
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

            adjacencyList.keys.forEachIndexed { index, node ->
                val angle = 2 * Math.PI * index / nodeCount
                val x = (centerX + 200 * cos(angle)).toFloat()
                val y = (centerY + 200 * sin(angle)).toFloat()
                nodePositions[node] = Offset(x, y)
            }

            adjacencyList.forEach { (node, neighbors) ->
                val start = nodePositions[node] ?: return@forEach
                neighbors.forEach { (neighbor, _) ->
                    val end = nodePositions[neighbor] ?: return@forEach
                    drawLine(color = Color.Black, start = start, end = end, strokeWidth = 2f)
                }
            }

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
