package com.example.miniproject

import android.graphics.Paint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import kotlin.math.max


//Linear Search

@Composable
fun LinearSearchVisualizer(navController: NavController) {
    var inputText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    var targetNumber by remember { mutableStateOf<Int?>(null) }
    var visualizationData by remember { mutableStateOf<List<LinearSearchStep>>(emptyList()) }
    var currentStep by remember { mutableStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    var delayTime by remember { mutableStateOf(500L) }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    var resultComplexity by remember { mutableStateOf("") } // Stores the complexity result

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Only show the IconButton if isIconVisible is true

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable{dismissKeyboard()}
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input Section
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter numbers") },
                    modifier = Modifier
                        .weight(2f)
                        .padding(8.dp)
                )
                TextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Target") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    dismissKeyboard()
                    numbers = inputText.split(",").mapNotNull { it.trim().toIntOrNull() }
                    targetNumber = targetText.toIntOrNull()
                    visualizationData = emptyList()
                    currentStep = 0
                    resultComplexity = "" // Reset complexity result
                },
                enabled = inputText.isNotEmpty() && targetText.isNotEmpty()
            ) {
                Text("Set Data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (numbers.isNotEmpty() && targetNumber != null) {
                Text(
                    "Array: ${numbers.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text("Target: $targetNumber", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Linear Search Button
            Button(
                onClick = {
                    if (!isSearching && numbers.isNotEmpty() && targetNumber != null) {
                        visualizationData = linearSearchVisualization(numbers, targetNumber!!)
                        currentStep = 0
                        isSearching = true
                    }
                },
                enabled = numbers.isNotEmpty() && targetNumber != null && !isSearching
            ) {
                Text("Start Linear Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Adjust Delay Slider
            Text("Adjust Delay (ms): ${delayTime}ms", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = delayTime.toFloat(),
                onValueChange = { delayTime = it.toLong() },
                valueRange = 100f..3000f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Visualization Section
            if (visualizationData.isNotEmpty() && currentStep < visualizationData.size) {
                val step = visualizationData[currentStep]

                Text(
                    text = step.message,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (step.currentIndex in numbers.indices) {
                    LinearSearchCanvas(
                        numbers = step.data,
                        currentIndex = step.currentIndex,
                        target = targetNumber!!,
                        delayTime = delayTime
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Buttons
                Row {
                    Button(
                        onClick = { if (currentStep > 0) currentStep-- },
                        enabled = currentStep > 0
                    ) {
                        Text("Previous")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (currentStep < visualizationData.size - 1) currentStep++ },
                        enabled = currentStep < visualizationData.size - 1
                    ) {
                        Text("Next")
                    }
                }
            }

            // Display Complexity After Search
            if (resultComplexity.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = resultComplexity,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }


        // Auto-advance to the next step
        LaunchedEffect(currentStep) {
            if (isSearching && currentStep < visualizationData.size - 1) {
                delay(delayTime)
                currentStep++
            } else if (currentStep == visualizationData.size - 1) {
                isSearching = false
                // Determine and display the complexity
                resultComplexity = when {
                    visualizationData.last().currentIndex == 0 -> "Complexities....\n Time: O(1) (Best Case)\n Space: O(1)"
                    visualizationData.last().currentIndex == -1 -> "Complexities....\n Time: O(n) (Worst Case)\n Space: O(1)"
                    else -> "Complexities....\n Time: ~O(n/2) (Average Case)\n Space: O(1)"
                }
            }

        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}
}



@Composable
fun LinearSearchCanvas(
    numbers: List<Int>,
    currentIndex: Int,
    target: Int,
    delayTime: Long
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    // Automatically scroll to the current element
    LaunchedEffect(currentIndex) {
        if (currentIndex in numbers.indices) { // Check for valid index
            val elementWidthInDp = 120.dp
            val elementWidthInPx = with(density) { elementWidthInDp.toPx() }
            val scrollToPosition = (currentIndex * elementWidthInPx).toInt()

            // Slow down the scrolling
            scrollState.animateScrollTo(
                value = scrollToPosition,
                animationSpec = tween(durationMillis = (delayTime * 4).toInt())
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFE8F5E9)) // Light green background for the canvas
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .horizontalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp)) // Padding at the start

            numbers.forEachIndexed { index, number ->
                val isTarget = number == target
                val isCurrent = index == currentIndex

                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(50.dp)
                        .background(
                            when {
                                isCurrent && isTarget -> Color.Green
                                isCurrent -> Color(0xFF800080)
                                else -> Color.Gray
                            },
                            shape = CircleShape
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = number.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            // Padding at the end
        }
    }
}

data class LinearSearchStep(
    val data: List<Int>,
    val message: String,
    val currentIndex: Int
)

fun linearSearchVisualization(arr: List<Int>, target: Int): List<LinearSearchStep> {
    val steps = mutableListOf<LinearSearchStep>()

    for (i in arr.indices) {
        val currentNumber = arr[i]

        val message = when {
            currentNumber < target -> "Comparing ${currentNumber} at index $i with target $target. It's smaller than $target. Moving forward!"
            currentNumber > target -> "Comparing ${currentNumber} at index $i with target $target. It's larger than $target. Moving on!"
            else -> " $target found at index $i!"
        }

        steps.add(LinearSearchStep(arr, message, i))

        if (currentNumber == target) {
            steps.add(LinearSearchStep(arr, "Target $target found at index $i. Search complete!", i))
            break
        }
    }

    if (steps.none { it.currentIndex >= 0 && arr[it.currentIndex] == target }) {
        steps.add(
            LinearSearchStep(
                data = arr,
                message = "Searched the entire array, but target is missing!",
                currentIndex = -1
            )
        )

    }


    return steps
}


//Binary Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinarySearchVisualizer(navController: NavController) {
    var inputText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    var targetNumber by remember { mutableStateOf<Int?>(null) }
    var visualizationData by remember { mutableStateOf<List<BinarySearchStep>>(emptyList()) }
    var currentStep by remember { mutableStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    var delayTime by remember { mutableStateOf(500L) }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Dismiss the keyboard on tap outside input fields
                        keyboardController?.hide()
                    }
                )
            }
    ) {

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable{dismissKeyboard()}
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input Section
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter numbers") },
                    modifier = Modifier
                        .weight(2f)
                        .padding(8.dp)
                )
                TextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Target") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    dismissKeyboard()
                    numbers = inputText.split(",").mapNotNull { it.trim().toIntOrNull() }.sorted()
                    targetNumber = targetText.toIntOrNull()
                    visualizationData = emptyList()
                    currentStep = 0
                },
                enabled = inputText.isNotEmpty() && targetText.isNotEmpty()
            ) {
                Text("Set Data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (numbers.isNotEmpty() && targetNumber != null) {
                Text(
                    "Array: ${numbers.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text("Target: $targetNumber", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Binary Search Button
            Button(
                onClick = {
                    if (!isSearching && numbers.isNotEmpty() && targetNumber != null) {
                        visualizationData = binarySearchVisualization(numbers, targetNumber!!)
                        currentStep = 0
                        isSearching = true
                    }
                },
                enabled = numbers.isNotEmpty() && targetNumber != null && !isSearching
            ) {
                Text("Start Binary Search")
            }

            // Adjust Delay Slider
            Spacer(modifier = Modifier.height(16.dp))
            Text("Adjust Delay (ms): ${delayTime}ms", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = delayTime.toFloat(),
                onValueChange = { delayTime = it.toLong() },
                valueRange = 100f..3000f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Visualization Section
            if (visualizationData.isNotEmpty() && currentStep < visualizationData.size) {
                val step = visualizationData[currentStep]

                Text(text = step.message, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                BinarySearchCanvas(
                    numbers = step.data,
                    left = step.left,
                    right = step.right,
                    mid = step.mid,
                    target = targetNumber!!,
                    currentStep = currentStep,
                    visualizationData = visualizationData
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Buttons
                Row {
                    Button(
                        onClick = { if (currentStep > 0) currentStep-- },
                        enabled = currentStep > 0
                    ) {
                        Text("Previous")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (currentStep < visualizationData.size - 1) currentStep++ },
                        enabled = currentStep < visualizationData.size - 1
                    ) {
                        Text("Next")
                    }
                }
            }
        }

        // Auto-advance to the next step
        LaunchedEffect(currentStep) {
            if (isSearching && currentStep < visualizationData.size - 1) {
                delay(delayTime)
                currentStep++
            } else if (currentStep == visualizationData.size - 1) {
                isSearching = false
            }
        }
    }

}

@Composable
fun BinarySearchCanvas(
    numbers: List<Int>,
    left: Int,
    right: Int,
    mid: Int?,
    target: Int,
    currentStep: Int,
    visualizationData: List<BinarySearchStep>
) {
    val circleRadius = 50f
    val gap = 60f
    val totalWidth = (numbers.size * circleRadius * 2) + ((numbers.size - 1) * gap)

    // Create a scroll state for horizontal scrolling
    val scrollState = rememberScrollState()

    // State variable to store the Canvas size
    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    // Using a horizontal scroll modifier to allow scrolling of the elements
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFE8F5E9)) // Light green background for the canvas
            .horizontalScroll(scrollState) // Makes the Box scrollable
    ) {
        Canvas(
            modifier = Modifier
                .width(totalWidth.dp) // Set the width of the Canvas to accommodate all elements
                .height(300.dp)
                .padding(start = circleRadius.dp, end = circleRadius.dp) // Add padding to both start and end
                .onSizeChanged { canvasSize = it.toSize() } // Update canvasSize when size changes
        ) {
            val startX = 0f // Start drawing from the leftmost point
            val arrowMessages = mutableListOf<String>()
            val arrows = mutableListOf<Float>()

            // Iterate through each number and draw it
            numbers.forEachIndexed { index, number ->
                val x = startX + index * (circleRadius * 2 + gap)
                val y = size.height / 2

                // Draw arrow for left, right, and mid based on conditions
                if ((mid != null && left <= right && numbers[mid] != target) ||
                    (mid != null && numbers[mid] == target && index != mid)
                ) {
                    if (index == left) {
                        arrowMessages.add("Left")
                        arrows.add(x)
                    }
                    if (index == right) {
                        arrowMessages.add("Right")
                        arrows.add(x)
                    }
                    if (index == mid) {
                        arrowMessages.add("Middle")
                        arrows.add(x)
                    }
                }

                // Set the color based on the current position of the element
                var color = when {
                    mid != null && numbers[mid] == target && index == mid -> Color.Yellow
                    mid != null && left <= right && index == mid && numbers[mid] != target -> Color.Green
                    mid != null && left <= right && index == left -> Color.Blue
                    mid != null && left <= right && index == right -> Color.Blue
                    else -> Color.Gray
                }

                // Draw the circle for the number
                drawCircle(
                    color = color,
                    radius = when {
                        index == mid -> circleRadius + 20f
                        index in left..right -> circleRadius + 10f
                        else -> circleRadius
                    },
                    center = Offset(x, y)
                )

                // Draw the number text inside the circle
                drawContext.canvas.nativeCanvas.drawText(
                    number.toString(),
                    x,
                    y + 15f,
                    Paint().apply {
                        textSize = circleRadius
                        color = Color.White
                        textAlign = Paint.Align.CENTER
                    }
                )
            }

            // Draw arrows for left, right, and mid indicators
            if ((mid != null && left <= right && numbers[mid] != target) ||
                (mid != null && numbers[mid] == target && currentStep < visualizationData.lastIndex)
            ) {
                arrows.distinct().forEach { x ->
                    val messagesForX = arrowMessages.filterIndexed { index, _ -> arrows[index] == x }
                    val combinedMessage = messagesForX.joinToString(", ")

                    drawArrowAndLabel(x, size.height / 2 - circleRadius - 20f, combinedMessage)
                }
            }
        }
    }

    // Scroll the row automatically based on the current step
    LaunchedEffect(currentStep, canvasSize) { // Add canvasSize as a dependency
        if (canvasSize != androidx.compose.ui.geometry.Size.Zero) { // Check if canvasSize is initialized
            // Calculate the target position for the current step, focusing on the middle element
            val targetX = if (mid != null && currentStep > 0) {
                // Calculate the position of the middle element
                val middleElementPosition = (numbers.size / 2) * (circleRadius * 2 + gap)

                // Adjust the position to keep the middle element in the center
                middleElementPosition - (canvasSize.width / 2) + circleRadius
            } else {
                0f // Scroll to the beginning for the first step
            }

            // Animate the scroll to the target position, ensuring it's within bounds
            scrollState.animateScrollTo(
                targetX.toInt()
                    .coerceAtMost((totalWidth - canvasSize.width).toInt())
                    .coerceAtLeast(0)
            )
        }
    }
}

// Helper function to draw arrow and label
fun DrawScope.drawArrowAndLabel(x: Float, centerY: Float, label: String) {
    val arrowBottomY = centerY - 50f
    val arrowTopY = arrowBottomY - 40f

    // Draw arrow line
    drawLine(
        color = Color.Black,
        start = Offset(x, arrowTopY),
        end = Offset(x, arrowBottomY),
        strokeWidth = 3f
    )

    // Draw arrowhead pointing to the element
    drawLine(
        color = Color.Black,
        start = Offset(x, arrowBottomY),
        end = Offset(x - 10f, arrowBottomY - 10f),
        strokeWidth = 3f
    )
    drawLine(
        color = Color.Black,
        start = Offset(x, arrowBottomY),
        end = Offset(x + 10f, arrowBottomY - 10f),
        strokeWidth = 3f
    )

    // Draw label above the arrow
    drawContext.canvas.nativeCanvas.drawText(
        label,
        x,
        arrowTopY - 10f, // Adjust for label positioning
        android.graphics.Paint().apply {
            textSize = 40f
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
        }
    )
}



data class BinarySearchStep(
    val data: List<Int>,
    val message: String,
    val left: Int,
    val right: Int,
    val mid: Int?
)

fun binarySearchVisualization(arr: List<Int>, target: Int): List<BinarySearchStep> {
    val steps = mutableListOf<BinarySearchStep>()
    var left = 0
    var right = arr.size - 1

    while (left <= right) {
        val mid = left + (right - left) / 2

        steps.add(BinarySearchStep(arr, "Checking middle element at index $mid", left, right, mid))

        when {
            arr[mid] == target -> {
                steps.add(BinarySearchStep(arr, "Found target at index $mid", left, right, mid))
                break
            }
            arr[mid] < target -> {
                steps.add(BinarySearchStep(arr, "Target is greater than ${arr[mid]}", left, right, mid))
                left = mid + 1
            }
            else -> {
                steps.add(BinarySearchStep(arr, "Target is less than ${arr[mid]}", left, right, mid))
                right = mid - 1
            }
        }
    }

    if (left > right) {
        steps.add(BinarySearchStep(arr, "Target not found in the array", left, right, null))
    }

    return steps
}


//Interpolation Search

@Composable
fun InterpolationSearchVisualizer(navController: NavController) {
    var inputText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    var targetNumber by remember { mutableStateOf<Int?>(null) }
    var visualizationData by remember { mutableStateOf<List<InterpolationSearchStep>>(emptyList()) }
    var currentStep by remember { mutableStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    Box() {

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable{dismissKeyboard()}
                .verticalScroll(rememberScrollState()), // Make the entire Column vertically scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input Section
            Spacer(modifier=Modifier.height(50.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter numbers (comma-separated)") },
                    modifier = Modifier.weight(2f)
                )
                TextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Target") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    dismissKeyboard()
                    numbers = inputText.split(",").mapNotNull { it.trim().toIntOrNull() }.sorted()
                    targetNumber = targetText.toIntOrNull()

                    // Reset visualization data and current step when inputs change
                    visualizationData = emptyList()
                    currentStep = 0
                    isSearching = false // Ensure the search state is reset
                },
                enabled = inputText.isNotEmpty() && targetText.isNotEmpty()
            ) {
                Text("Set Data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (numbers.isNotEmpty() && targetNumber != null) {
                Text(
                    "Array: ${numbers.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text("Target: $targetNumber", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Interpolation Search Button
            Button(
                onClick = {
                    if (!isSearching && numbers.isNotEmpty() && targetNumber != null) {
                        visualizationData = interpolationSearchVisualization(numbers, targetNumber!!)
                        currentStep = 0
                        isSearching = true
                    }
                },
                enabled = numbers.isNotEmpty() && targetNumber != null && !isSearching
            ) {
                Text("Start Interpolation Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visualization Section
            if (isSearching) {
                val step = visualizationData.getOrNull(currentStep)

                // Render the canvas
                InterpolationSearchCanvas(
                    numbers = step?.data ?: numbers,
                    left = step?.left ?: 0,
                    right = step?.right ?: numbers.lastIndex,
                    mid = step?.pos,
                    target = targetNumber,
                    currentStep = currentStep,
                    visualizationData = visualizationData
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Display navigation buttons or message
                if (visualizationData.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = { currentStep-- },
                            enabled = currentStep > 0
                        ) {
                            Text("Previous")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { currentStep++ },
                            enabled = currentStep < visualizationData.size - 1
                        ) {
                            Text("Next")
                        }
                    }
                }
            }

        }
    }
}
@Composable
fun InterpolationSearchCanvas(
    numbers: List<Int>,
    left: Int,
    right: Int,
    mid: Int?,
    target: Int?,
    currentStep: Int,
    visualizationData: List<InterpolationSearchStep>
) {
    val circleRadius = 50f
    val gap = 60f
    val totalWidth = (numbers.size * circleRadius * 2) + ((numbers.size - 1) * gap)
    val scrollState = rememberScrollState()


    // Auto-scroll to the mid element if applicable
    LaunchedEffect(mid) {
        if (mid != null && mid in numbers.indices) {
            val scrollTo = (mid * (circleRadius * 2 + gap)).toInt()
            scrollState.animateScrollTo(scrollTo.coerceIn(0, (totalWidth - 1).toInt()))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFE8F5E9)) // Light green background for the canvas
            .horizontalScroll(scrollState)
    ) {
        Canvas(
            modifier = Modifier
                .width(with(LocalDensity.current) { (totalWidth + circleRadius * 2).toDp() }) // Add padding for the last element
                .height(300.dp)
                .padding(horizontal = circleRadius.dp) // Ensure first and last elements are fully visible
        ) {
            val startX = 0f // Start drawing from the leftmost point

            numbers.forEachIndexed { index, number ->
                val x = startX + index * (circleRadius * 2 + gap)
                val y = size.height / 2

                val color = when {
                    mid == null -> Color.Gray // Initial state
                    mid == -1 -> Color.Gray // Target not found: All gray
                    target == number && mid == index -> Color.Green // Highlight target when found
                    else -> Color.Gray
                }

                drawCircle(
                    color = color,
                    radius = circleRadius,
                    center = Offset(x, y)
                )

                // Drawing the number text using drawIntoCanvas
                drawIntoCanvas { canvas ->
                    val textPaint = Paint().apply {
                        textSize = 40f
                        textAlign = Paint.Align.CENTER
                    }

                    canvas.nativeCanvas.drawText(
                        number.toString(),
                        x,
                        y + textPaint.textSize / 3, // Adjust text vertically
                        textPaint
                    )
                }
            }
        }
    }

    // Display the appropriate message
    if (visualizationData.isNotEmpty() && currentStep < visualizationData.size) {
        val step = visualizationData[currentStep]
        Text(
            text = step.message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    } else if (currentStep == visualizationData.size - 1 && numbers.getOrNull(mid ?: -1) != target) {
        // Show "Target not found" message when search completes and target is not found
        Text(
            text = "Target not found in the array.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    } else if (mid != null && numbers.getOrNull(mid) == target) {
        Text(
            text = "Target found at index $mid",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun interpolationSearchVisualization(arr: List<Int>, target: Int): List<InterpolationSearchStep> {
    val steps = mutableListOf<InterpolationSearchStep>()

    // Check if the target is outside the bounds of the array
    if (target < arr.firstOrNull() ?: Int.MIN_VALUE || target > arr.lastOrNull() ?: Int.MAX_VALUE) {
        steps.add(InterpolationSearchStep(arr, "Target not found in the array.", 0, arr.lastIndex, -1))
        return steps
    }

    var left = 0
    var right = arr.size - 1
    var mid: Int? = null

    while (left <= right && target >= arr[left] && target <= arr[right]) {
        if (arr[right] == arr[left]) break

        // Calculate mid using the interpolation formula
        mid = left + ((target - arr[left]) * (right - left)) / (arr[right] - arr[left])

        if (mid !in left..right) break

        // Add visualization step with formula details
        steps.add(
            InterpolationSearchStep(
                arr,
                "Formula: pos = left + ((target - arr[left]) * (right - left)) / (arr[right] - arr[left])\n" +
                        "pos = $left + (($target - ${arr[left]}) * ($right - $left)) / (${arr[right]} - ${arr[left]})\n" +
                        "pos = $mid",
                left,
                right,
                mid
            )
        )

        when {
            arr[mid] == target -> {
                steps.add(InterpolationSearchStep(arr, "Target found at index $mid", left, right, mid))
                break
            }
            arr[mid] < target -> {
                steps.add(
                    InterpolationSearchStep(
                        arr,
                        "Value at pos (${arr[mid]}) is less than target ($target). Moving left to ${mid + 1}.",
                        left,
                        right,
                        mid
                    )
                )
                left = mid + 1
            }
            else -> {
                steps.add(
                    InterpolationSearchStep(
                        arr,
                        "Value at pos (${arr[mid]}) is greater than target ($target). Moving right to ${mid - 1}.",
                        left,
                        right,
                        mid
                    )
                )
                right = mid - 1
            }
        }
    }

    // Step when the target is not found
    if (left > right) {
        steps.add(InterpolationSearchStep(arr, "Target not found in the array.", left, right, -1))
    }

    return steps
}

data class InterpolationSearchStep(
    val data: List<Int>,
    val message: String,
    val left: Int,
    val right: Int,
    val pos: Int
)