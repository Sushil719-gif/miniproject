package com.example.miniproject


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlin.math.cos
import kotlin.math.sin
import java.util.PriorityQueue


//Prims's

@Composable
fun PrimsAlgorithmVisualization(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, Map<String, Int>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<List<String>, List<Pair<String, String>>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var startNode by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

// Dismiss keyboard when tapping outside any input field or button
val dismissKeyboard: () -> Unit = {
    keyboardController?.hide()
}
    // Parse adjacency list input
    fun parseAdjacencyList(input: String): Map<String, Map<String, Int>> {
        return try {
            input.split(";").associate { entry ->
                val parts = entry.split(":").map { it.trim() }
                val node = parts[0]
                val neighbors = if (parts.size > 1) {
                    parts[1].split(",").associate {
                        val (neighbor, weight) = it.split("-").map { it.trim() }
                        neighbor to weight.toInt()
                    }
                } else emptyMap()
                node to neighbors
            }
        } catch (e: Exception) {
            errorText = "Invalid adjacency list format! Use A:B-2,C-4;B:D-6;C:D-7;D"
            emptyMap()
        }
    }

    fun runPrimsAlgorithm() {
        if (inputText.isBlank() || startNode.isBlank()) {
            errorText = "Please enter a valid adjacency list and start node."
            return
        }

        val adjacencyListParsed = parseAdjacencyList(inputText)
        if (adjacencyListParsed.isEmpty() || !adjacencyListParsed.containsKey(startNode)) {
            errorText = "Invalid input or start node not present in the graph."
            return
        }

        val stepList = mutableListOf<Pair<String, Pair<List<String>, List<Pair<String, String>>>>>()
        val mstEdges = mutableListOf<Pair<String, String>>() // Minimum Spanning Tree edges
        val visited = mutableSetOf<String>() // Visited nodes
        val priorityQueue =
            PriorityQueue<Triple<String, String, Int>>(compareBy { it.third }) // Min-heap for edges

        // Start with the given start node
        visited.add(startNode)
        adjacencyListParsed[startNode]?.forEach { (neighbor, weight) ->
            priorityQueue.add(Triple(startNode, neighbor, weight))
        }

        while (priorityQueue.isNotEmpty() && mstEdges.size < adjacencyListParsed.size - 1) {
            val (from, to, weight) = priorityQueue.poll()

            // Skip if the destination node is already visited
            if (to in visited) continue

            // Add the edge to the MST
            mstEdges.add(from to to)
            visited.add(to)

            // Record the current step
            stepList.add(
                "Added edge ($from -> $to) with weight $weight to MST." to
                        (visited.toList() to mstEdges.toList())
            )

            // Add all edges from the newly visited node to the priority queue
            adjacencyListParsed[to]?.forEach { (neighbor, neighborWeight) ->
                if (neighbor !in visited) {
                    priorityQueue.add(Triple(to, neighbor, neighborWeight))
                }
            }
        }

        // Final step: Add MST summary
        stepList.add(
            "Minimum Spanning Tree completed! MST edges: ${mstEdges.joinToString()}" to
                    (visited.toList() to mstEdges.toList())
        )

        steps = stepList
        currentStepIndex = 0

        // Check if the graph is connected
        if (visited.size != adjacencyListParsed.size) {
            errorText = "The graph is disconnected. MST may not cover all nodes."
        }
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
            Text("Prim's Algorithm Visualization", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter Adjacency List (e.g., A:B-2,C-4;B:D-6;C:D-7;D)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorText.isNotEmpty(),
                singleLine=true
            )


            TextField(
                value = startNode,
                onValueChange = { startNode = it },
                label = { Text("Enter Start Node (e.g., A)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorText.isNotEmpty()
            )

            if (errorText.isNotEmpty()) {
                Text(errorText, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                dismissKeyboard()
                errorText = ""
                adjacencyList = parseAdjacencyList(inputText)
                runPrimsAlgorithm()
            }) {
                Text("Visualize")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (steps.isNotEmpty()) {
                Text(
                    "Step ${currentStepIndex + 1}/${steps.size}: ${steps[currentStepIndex].first}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimsGraphVisualization(
                    adjacencyList = adjacencyList,
                    currentStep = steps.getOrNull(currentStepIndex),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
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
}
@Composable
fun PrimsGraphVisualization(
    adjacencyList: Map<String, Map<String, Int>>,
    currentStep: Pair<String, Pair<List<String>, List<Pair<String, String>>>>?,
    modifier: Modifier = Modifier
) {
    val visitedNodes = currentStep?.second?.first ?: emptyList()
    val mstEdges = currentStep?.second?.second ?: emptyList()

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
                    val edgeColor = if (mstEdges.contains(node to neighbor) || mstEdges.contains(neighbor to node)) Color.Green else Color.Black
                    drawLine(color = edgeColor, start = start, end = end, strokeWidth = 2f)
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

//Kruskal's

@Composable
fun KruskalsAlgorithmVisualization(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, Map<String, Int>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<List<Pair<String, String>>, List<Pair<String, String>>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var sortedEdgesMessage by remember { mutableStateOf("") }
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
                        val (neighbor, weight) = it.split("-").map { it.trim() }
                        neighbor to weight.toInt()
                    }
                } else emptyMap()
                node to neighbors
            }
        } catch (e: Exception) {
            errorText = "Invalid adjacency list format! Use A:B-2,C-4;B:D-6;C:D-7;D"
            emptyMap()
        }
    }

    fun runKruskalsAlgorithm() {
        if (inputText.isBlank()) {
            errorText = "Please enter a valid adjacency list."
            return
        }

        val adjacencyListParsed = parseAdjacencyList(inputText)
        if (adjacencyListParsed.isEmpty()) {
            errorText = "Invalid input."
            return
        }

        val stepList =
            mutableListOf<Pair<String, Pair<List<Pair<String, String>>, List<Pair<String, String>>>>>()
        val edges = mutableListOf<Triple<String, String, Int>>()
        val mstEdges = mutableListOf<Pair<String, String>>()
        val parent = mutableMapOf<String, String>()
        val rank = mutableMapOf<String, Int>()

        adjacencyListParsed.forEach { (node, neighbors) ->
            neighbors.forEach { (neighbor, weight) ->
                if (!edges.contains(Triple(neighbor, node, weight))) { // Avoid duplicate edges
                    edges.add(Triple(node, neighbor, weight))
                }
            }
        }

        edges.sortBy { it.third } // Sort edges by weight
        sortedEdgesMessage =
            "Sorted Edges: ${edges.joinToString { "(${it.first} -> ${it.second}, weight: ${it.third})" }}"

        adjacencyListParsed.keys.forEach { node ->
            parent[node] = node
            rank[node] = 0
        }

        fun find(node: String): String {
            if (parent[node] != node) {
                parent[node] = find(parent[node]!!)
            }
            return parent[node]!!
        }

        fun union(node1: String, node2: String) {
            val root1 = find(node1)
            val root2 = find(node2)

            if (root1 != root2) {
                if (rank[root1]!! > rank[root2]!!) {
                    parent[root2] = root1
                } else if (rank[root1]!! < rank[root2]!!) {
                    parent[root1] = root2
                } else {
                    parent[root2] = root1
                    rank[root1] = rank[root1]!! + 1
                }
            }
        }

        edges.forEach { (from, to, weight) ->
            if (find(from) != find(to)) {
                union(from, to)
                mstEdges.add(from to to)

                stepList.add(
                    "Added edge ($from -> $to) with weight $weight to MST." to
                            (mstEdges.toList() to edges.map { it.first to it.second })
                )
            }
        }

        stepList.add(
            "Minimum Spanning Tree completed! MST edges: ${mstEdges.joinToString()}" to
                    (mstEdges.toList() to edges.map { it.first to it.second })
        )

        steps = stepList
        currentStepIndex = 0
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
            Text("Kruskal's Algorithm Visualization", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter Adjacency List (e.g., A:B-2,C-4;B:D-6;C:D-7;D)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorText.isNotEmpty(),
                singleLine = true
            )

            if (errorText.isNotEmpty()) {
                Text(errorText, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                dismissKeyboard()
                errorText = ""
                adjacencyList = parseAdjacencyList(inputText)
                runKruskalsAlgorithm()
            }) {
                Text("Visualize")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sortedEdgesMessage.isNotEmpty()) {
                Text(
                    sortedEdgesMessage,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Step ${currentStepIndex + 1}/${steps.size}: ${steps[currentStepIndex].first}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                KruskalGraphVisualization(
                    adjacencyList = adjacencyList,
                    currentStep = steps.getOrNull(currentStepIndex),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
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
}
@Composable
fun KruskalGraphVisualization(
    adjacencyList: Map<String, Map<String, Int>>,
    currentStep: Pair<String, Pair<List<Pair<String, String>>, List<Pair<String, String>>>>?,
    modifier: Modifier = Modifier
) {
    val mstEdges = currentStep?.second?.first ?: emptyList() // Edges in the MST
    val connectedNodes = mstEdges.flatMap { listOf(it.first, it.second) }.toSet() // Nodes connected by MST edges

    Box(modifier = modifier.background(Color.White)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val nodeCount = adjacencyList.size
            val radius = 40f
            val canvasSize = size
            val centerX = canvasSize.width / 2
            val centerY = canvasSize.height / 2
            val nodePositions = mutableMapOf<String, Offset>()

            // Compute positions for nodes in a circular layout
            adjacencyList.keys.forEachIndexed { index, node ->
                val angle = 2 * Math.PI * index / nodeCount
                val x = (centerX + 200 * cos(angle)).toFloat()
                val y = (centerY + 200 * sin(angle)).toFloat()
                nodePositions[node] = Offset(x, y)
            }

            // Draw edges with different colors for included MST edges
            adjacencyList.forEach { (node, neighbors) ->
                val start = nodePositions[node] ?: return@forEach
                neighbors.forEach { (neighbor, _) ->
                    val end = nodePositions[neighbor] ?: return@forEach
                    val edgeColor = if (mstEdges.contains(node to neighbor) || mstEdges.contains(neighbor to node)) Color.Green else Color.Black
                    drawLine(color = edgeColor, start = start, end = end, strokeWidth = 2f)
                }
            }

            // Draw nodes with different colors if connected by MST edges
            nodePositions.forEach { (node, position) ->
                val nodeColor = if (connectedNodes.contains(node)) Color.Green else Color.Gray
                drawCircle(color = nodeColor, center = position, radius = radius)
                drawContext.canvas.nativeCanvas.drawText(
                    node,
                    position.x,
                    position.y + radius / 2,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 40f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}

//Dijkstra's



@Composable
fun DijkstraAlgorithmVisualization(navController: NavController) {
    var adjacencyList by remember { mutableStateOf<Map<String, Map<String, Int>>>(emptyMap()) }
    var steps by remember { mutableStateOf(listOf<Pair<String, Pair<Map<String, String>, List<Pair<String, String>>>>>()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var startNode by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

// Dismiss keyboard when tapping outside any input field or button
val dismissKeyboard: () -> Unit = {
    keyboardController?.hide()
}

    // Parse adjacency list input
    fun parseAdjacencyList(input: String): Map<String, Map<String, Int>> {
        return try {
            input.split(";").associate { entry ->
                val parts = entry.split(":").map { it.trim() }
                val node = parts[0]
                val neighbors = if (parts.size > 1) {
                    parts[1].split(",").associate {
                        val (neighbor, weight) = it.split("-").map { it.trim() }
                        neighbor to weight.toInt()
                    }
                } else emptyMap()
                node to neighbors
            }
        } catch (e: Exception) {
            errorText = "Invalid adjacency list format! Use A:B-2,C-4;B:D-6;C:D-7;D"
            emptyMap()
        }
    }

    fun runDijkstraAlgorithm() {
        if (inputText.isBlank() || startNode.isBlank()) {
            errorText = "Please enter a valid adjacency list and start node."
            return
        }

        val adjacencyListParsed = parseAdjacencyList(inputText)
        if (adjacencyListParsed.isEmpty() || !adjacencyListParsed.containsKey(startNode)) {
            errorText = "Invalid input or start node not present in the graph."
            return
        }

        val stepList =
            mutableListOf<Pair<String, Pair<Map<String, String>, List<Pair<String, String>>>>>()
        val distances = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
        val visited = mutableSetOf<String>()
        val priorityQueue = PriorityQueue<Pair<String, Int>>(compareBy { it.second })
        val shortestPathEdges = mutableListOf<Pair<String, String>>()

        distances[startNode] = 0
        priorityQueue.add(startNode to 0)

        while (priorityQueue.isNotEmpty()) {
            val (currentNode, currentDistance) = priorityQueue.poll()

            if (currentNode in visited) continue
            visited.add(currentNode)

            stepList.add(
                "Visited node $currentNode with current distance $currentDistance. Marked as visited because it has the smallest distance from the unvisited nodes." to
                        (distances.mapValues { if (it.value == Int.MAX_VALUE) "∞" else it.value.toString() } to shortestPathEdges.toList())
            )

            adjacencyListParsed[currentNode]?.forEach { (neighbor, weight) ->
                if (neighbor !in visited) {
                    val newDistance = currentDistance + weight
                    if (newDistance < distances.getValue(neighbor)) {
                        distances[neighbor] = newDistance
                        priorityQueue.add(neighbor to newDistance)
                        shortestPathEdges.add(currentNode to neighbor)

                        stepList.add(
                            "Updated distance for node $neighbor to $newDistance via $currentNode. Chose this path because it has a smaller distance compared to previous calculations." to
                                    (distances.mapValues { if (it.value == Int.MAX_VALUE) "∞" else it.value.toString() } to shortestPathEdges.toList())
                        )
                    }
                }
            }
        }

        stepList.add(
            "Dijkstra's algorithm completed. Final shortest distances: ${distances.mapValues { if (it.value == Int.MAX_VALUE) "∞" else it.value.toString() }}. These distances represent the shortest paths from the start node to all other nodes." to
                    (distances.mapValues { if (it.value == Int.MAX_VALUE) "∞" else it.value.toString() } to shortestPathEdges.toList())
        )

        steps = stepList
        currentStepIndex = 0

        if (visited.size != adjacencyListParsed.size) {
            errorText = "The graph is disconnected. Some nodes may not be reachable."
        }
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
            Text("Dijkstra's Algorithm Visualization", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter Adjacency List (e.g., A:B-2,C-4;B:D-6;C:D-7;D)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorText.isNotEmpty(),
                singleLine = true
            )

            TextField(
                value = startNode,
                onValueChange = { startNode = it },
                label = { Text("Enter Start Node (e.g., A)") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorText.isNotEmpty()
            )

            if (errorText.isNotEmpty()) {
                Text(errorText, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                dismissKeyboard()
                errorText = ""
                adjacencyList = parseAdjacencyList(inputText)
                runDijkstraAlgorithm()
            }) {
                Text("Visualize")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (steps.isNotEmpty()) {
                Text(
                    "Step ${currentStepIndex + 1}/${steps.size}: ${steps[currentStepIndex].first}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                DijGraphVisualization(
                    adjacencyList = adjacencyList,
                    currentStep = steps.getOrNull(currentStepIndex),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
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
}
@Composable
fun DijGraphVisualization(
    adjacencyList: Map<String, Map<String, Int>>,
    currentStep: Pair<String, Pair<Map<String, String>, List<Pair<String, String>>>>?,
    modifier: Modifier = Modifier
) {
    val distances = currentStep?.second?.first ?: emptyMap()
    val shortestPathEdges = currentStep?.second?.second ?: emptyList()

    Box(modifier = modifier.background(Color.White)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val nodeCount = adjacencyList.size
            val radius = 80f // Adjusted radius for better visibility
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
                neighbors.forEach { (neighbor, weight) ->
                    val end = nodePositions[neighbor] ?: return@forEach
                    val edgeColor = if (shortestPathEdges.contains(node to neighbor) || shortestPathEdges.contains(neighbor to node)) Color.Green else Color.Black
                    drawLine(color = edgeColor, start = start, end = end, strokeWidth = 2f)
                }
            }

            nodePositions.forEach { (node, position) ->
                drawCircle(
                    color = if (distances.containsKey(node)) Color.Green else Color.Gray,
                    center = position,
                    radius = radius
                )

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "$node (${distances[node] ?: "∞"})",

                        position.x,
                        position.y + (radius / 3),
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
}
