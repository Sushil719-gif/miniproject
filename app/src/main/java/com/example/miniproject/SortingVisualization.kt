package com.example.miniproject

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.delay



/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingVisualizer(navController: NavController) {
    var inputText by remember { mutableStateOf("") }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    var isSorting by remember { mutableStateOf(false) }
    var timeComplexity by remember { mutableStateOf("Time Complexity: O(n²)") }
    var spaceComplexity by remember { mutableStateOf("Space Complexity: O(1)") }
    var elementsBeingSwapped by remember { mutableStateOf<List<Int>>(emptyList()) }
    var delayTime by remember { mutableStateOf(500L) }
    var swapOffsets by remember { mutableStateOf<List<Float>>(emptyList()) }
    var sortAscending by remember { mutableStateOf(true) }
    var stepDescription by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()

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
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier=Modifier.height(50.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter numbers separated by commas") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        numbers = inputText.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .toMutableList()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Set Numbers")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Sort Order: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = sortAscending,
                    onClick = { sortAscending = true },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text("Ascending", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = !sortAscending,
                    onClick = { sortAscending = false },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text("Descending", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isSorting) {
                        isSorting = true
                        timeComplexity = "Time Complexity: O(n²)"
                        spaceComplexity = "Space Complexity: O(1)"
                        numbers = numbers.toMutableList()
                    }
                },
                enabled = numbers.isNotEmpty() && !isSorting
            ) {
                Text("Start Sorting")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Adjust Delay (ms): ${delayTime}ms", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = delayTime.toFloat(),
                onValueChange = { delayTime = it.toLong() },
                valueRange = 100f..2000f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )

            LaunchedEffect(isSorting) {
                if (isSorting && numbers.isNotEmpty()) {
                    selectionSort(
                        numbers.toMutableList(),
                        delayTime,
                        sortAscending
                    ) { sortedArray, elementsToSwap, offsets, stepDesc ->
                        numbers = sortedArray
                        elementsBeingSwapped = elementsToSwap
                        swapOffsets = offsets
                        stepDescription = stepDesc
                    }
                    isSorting = false
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = lazyListState
            ) {
                items(numbers.size) { index ->
                    val isSwapped = index in elementsBeingSwapped
                    val offset = swapOffsets.getOrElse(index) { 0f }

                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(50.dp)
                            .offset(x = animateDpAsState(offset.dp).value)
                            .background(
                                color = if (isSwapped) Color(0xFFff7043) else Color.Gray,
                                shape = CircleShape
                            )
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = numbers[index].toString(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            LaunchedEffect(elementsBeingSwapped) {
                if (elementsBeingSwapped.isNotEmpty()) {
                    val firstIndex = elementsBeingSwapped[0]
                    lazyListState.animateScrollToItem(firstIndex)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stepDescription, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = timeComplexity, style = MaterialTheme.typography.bodyLarge)
            Text(text = spaceComplexity, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

suspend fun selectionSort(
    array: MutableList<Int>,
    delayTime: Long,
    sortAscending: Boolean,
    onUpdate: (List<Int>, List<Int>, List<Float>, String) -> Unit
) {
    val n = array.size

    for (i in 0 until n - 1) {
        var minMaxIndex = i
        for (j in i + 1 until n) {
            // Adjust the comparison based on ascending or descending order
            val comparisonResult = if (sortAscending) {
                array[j] < array[minMaxIndex]  // Ascending: looking for the smallest
            } else {
                array[j] > array[minMaxIndex]  // Descending: looking for the largest
            }

            // Change comparison message based on the order
            val comparisonDesc = if (sortAscending) {
                "Comparing ${array[minMaxIndex]} (current minimum) with ${array[j]}"
            } else {
                "Comparing ${array[minMaxIndex]} (current maximum) with ${array[j]}"
            }

            // Update UI with the comparison step
            onUpdate(array, listOf(i, j), listOf(i.toFloat(), j.toFloat()), comparisonDesc)
            delay(delayTime)

            // Update the minimum/maximum index if needed
            if (comparisonResult) {
                minMaxIndex = j
            }
        }

        // If the min/max index is different from the current position, swap the elements
        if (minMaxIndex != i) {
            val temp = array[i]
            array[i] = array[minMaxIndex]
            array[minMaxIndex] = temp
            val swapDesc = "Swapping ${array[i]} with ${array[minMaxIndex]}"
            onUpdate(array, listOf(i, minMaxIndex), listOf(i.toFloat(), minMaxIndex.toFloat()), swapDesc)
            delay(delayTime)
        } else {
            onUpdate(array, emptyList(), emptyList(), "No swapping needed.")
            delay(delayTime)
        }

        // End of pass message
        onUpdate(array, emptyList(), emptyList(), "End of pass ${i + 1}.")
        delay(delayTime)
    }

    // Final message indicating sorting is complete
    onUpdate(array, emptyList(), emptyList(), "Sorting complete!")
}*/


//Bubble Sort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubbleSort(navController: NavController) {
    var inputText by remember { mutableStateOf("") }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    var isSorting by remember { mutableStateOf(false) }
    var timeComplexity by remember { mutableStateOf("Time Complexity: O(n²)") }
    var spaceComplexity by remember { mutableStateOf("Space Complexity: O(1)") }
    var elementsBeingSwapped by remember { mutableStateOf<List<Int>>(emptyList()) }
    var delayTime by remember { mutableStateOf(500L) }
    var swapOffsets by remember { mutableStateOf<List<Float>>(emptyList()) }
    var sortAscending by remember { mutableStateOf(true) }
    var stepDescription by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState() // Remember the state of LazyRow
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
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
                .clickable{dismissKeyboard()}
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier=Modifier.height(50.dp))
            Row() {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter numbers") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        dismissKeyboard()
                        numbers = inputText.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .toMutableList()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Set")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Sort Order: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = sortAscending,
                    onClick = { sortAscending = true },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text("Ascending", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = !sortAscending,
                    onClick = { sortAscending = false },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text("Descending", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isSorting) {
                        isSorting = true
                        timeComplexity = "Time Complexity: O(n²)"
                        spaceComplexity = "Space Complexity: O(1)"
                        numbers = numbers.toMutableList()
                    }
                },
                enabled = numbers.isNotEmpty() && !isSorting
            ) {
                Text("Start Sorting")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Adjust Delay (ms): ${delayTime}ms", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = delayTime.toFloat(),
                onValueChange = { delayTime = it.toLong() },
                valueRange = 100f..2000f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )

            LaunchedEffect(isSorting) {
                if (isSorting && numbers.isNotEmpty()) {
                    bubbleSort(
                        numbers.toMutableList(),
                        delayTime,
                        sortAscending
                    ) { sortedArray, elementsToSwap, offsets, stepDesc ->
                        numbers = sortedArray
                        elementsBeingSwapped = elementsToSwap
                        swapOffsets = offsets
                        stepDescription = stepDesc
                    }
                    isSorting = false
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Displaying Numbers with Animation (using LazyRow)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = lazyListState // Assign the state
            ) {
                items(numbers.size) { index ->
                    val isSwapped = index in elementsBeingSwapped
                    val offset = swapOffsets.getOrElse(index) { 0f }

                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(50.dp)
                            .offset(x = animateDpAsState(offset.dp).value)
                            .background(
                                color = if (isSwapped) Color(0xFFff7043) else Color.Gray,
                                shape = CircleShape
                            )
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = numbers[index].toString(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Scroll to the comparing elements
            LaunchedEffect(elementsBeingSwapped) {
                if (elementsBeingSwapped.isNotEmpty()) {
                    val firstIndex = elementsBeingSwapped[0]
                    lazyListState.animateScrollToItem(firstIndex)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stepDescription, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = timeComplexity, style = MaterialTheme.typography.bodyLarge)
            Text(text = spaceComplexity, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// ... (bubbleSort function remains the same)


suspend fun bubbleSort(
    array: MutableList<Int>,
    delayTime: Long,
    sortAscending: Boolean,
    onUpdate: (List<Int>, List<Int>, List<Float>, String) -> Unit
) {
    val n = array.size
    var swapped: Boolean

    for (i in 0 until n - 1) {
        swapped = false
        for (j in 0 until n - i - 1) {
            val elementsToSwap = listOf(j, j + 1)

            val comparisonResult = if (sortAscending) {
                array[j] > array[j + 1]
            } else {
                array[j] < array[j + 1]
            }

            val comparisonDesc = "Comparing ${array[j]} (left) ${if (sortAscending) ">" else "<"} ${array[j + 1]} (right) = $comparisonResult"
            onUpdate(array, elementsToSwap, listOf(j.toFloat(), (j + 1).toFloat()), comparisonDesc)
            delay(delayTime)

            if (comparisonResult) {
                val temp = array[j]
                array[j] = array[j + 1]
                array[j + 1] = temp
                swapped = true

                val swapDesc = "Swapping ${array[j + 1]} with ${array[j]}"
                onUpdate(array, elementsToSwap, listOf(j.toFloat(), (j + 1).toFloat()), swapDesc)
                delay(delayTime)
            } else {
                // Display "no swapping" message when comparison is false
                onUpdate(array, emptyList(), emptyList(), "No swapping needed.")
                delay(delayTime)
            }
        }

        if (!swapped) {
            onUpdate(array, emptyList(), emptyList(), "No swaps in this pass. The list is already sorted.")
            break
        }

        onUpdate(array, emptyList(), emptyList(), "End of pass ${i + 1}.")
        delay(delayTime)
    }

    onUpdate(array, emptyList(), emptyList(), "Sorting complete!")
}




//Insertion Sort


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertionSort(navController: NavController) {
    var inputText by remember { mutableStateOf("") }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    var isSorting by remember { mutableStateOf(false) }
    var timeComplexity by remember { mutableStateOf("Time Complexity: O(n²)") }
    var spaceComplexity by remember { mutableStateOf("Space Complexity: O(1)") }
    var elementsBeingSwapped by remember { mutableStateOf<List<Int>>(emptyList()) }
    var delayTime by remember { mutableStateOf(500L) }
    var swapOffsets by remember { mutableStateOf<List<Float>>(emptyList()) }
    var sortAscending by remember { mutableStateOf(true) }
    var stepDescription by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

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
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier=Modifier.height(50.dp))
            Row() {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter numbers") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        dismissKeyboard()
                        numbers = inputText.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .toMutableList()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Set")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Sort Order: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = sortAscending,
                    onClick = { sortAscending = true },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text("Ascending", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = !sortAscending,
                    onClick = { sortAscending = false },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text("Descending", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isSorting) {
                        isSorting = true
                        timeComplexity = "Time Complexity: O(n²)"
                        spaceComplexity = "Space Complexity: O(1)"
                        numbers = numbers.toMutableList()
                    }
                },
                enabled = numbers.isNotEmpty() && !isSorting
            ) {
                Text("Start Sorting")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Adjust Delay (ms): ${delayTime}ms", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = delayTime.toFloat(),
                onValueChange = { delayTime = it.toLong() },
                valueRange = 100f..3000f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )

            LaunchedEffect(isSorting) {
                if (isSorting && numbers.isNotEmpty()) {
                    insertionSort(
                        numbers.toMutableList(),
                        delayTime,
                        sortAscending
                    ) { sortedArray, elementsToSwap, offsets, stepDesc ->
                        numbers = sortedArray
                        elementsBeingSwapped = elementsToSwap
                        swapOffsets = offsets
                        stepDescription = stepDesc
                    }
                    isSorting = false
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = lazyListState
            ) {
                items(numbers.size) { index ->
                    val isSwapped = index in elementsBeingSwapped
                    val offset = swapOffsets.getOrElse(index) { 0f }

                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(50.dp)
                            .offset(x = animateDpAsState(offset.dp).value)
                            .background(
                                color = if (isSwapped) Color(0xFFff7043) else Color.Gray,
                                shape = CircleShape
                            )
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = numbers[index].toString(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            LaunchedEffect(elementsBeingSwapped) {
                if (elementsBeingSwapped.isNotEmpty()) {
                    val firstIndex = elementsBeingSwapped[0]
                    lazyListState.animateScrollToItem(firstIndex)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stepDescription, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = timeComplexity, style = MaterialTheme.typography.bodyLarge)
            Text(text = spaceComplexity, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

suspend fun insertionSort(
    array: MutableList<Int>,
    delayTime: Long,
    sortAscending: Boolean,
    onUpdate: (List<Int>, List<Int>, List<Float>, String) -> Unit
) {
    val n = array.size

    // Loop through the list starting from the second element
    for (i in 1 until n) {
        val key = array[i]  // The current element to be inserted in the sorted part of the list
        var j = i - 1  // The index for comparing elements to the left of 'key'

        // Compare 'key' with elements to its left (depending on ascending/descending order)
        while (j >= 0 && (if (sortAscending) array[j] > key else array[j] < key)) {
            // Message: Explain the comparison and the movement of elements
            onUpdate(array, listOf(j + 1, j), listOf((j + 1).toFloat(), j.toFloat()),
                "Comparing $key with ${array[j]}. Since ${array[j]} is ${if (sortAscending) "greater" else "smaller"} than $key, moving ${array[j]} to the right.")

            // Move the larger (or smaller) element to the right to make space for 'key'
            array[j + 1] = array[j]
            j -= 1  // Move left to the next element
            delay(delayTime)  // Delay for animation or visualization
        }

        // Insert the key into its correct position
        array[j + 1] = key

        // Message: Explain where the key is inserted and why it's placed there (sorted part of the list)
        onUpdate(array, emptyList(), emptyList(), "Inserting $key at position ${j + 1} because it's the correct spot to maintain order.")
        delay(delayTime)  // Delay for animation or visualization
    }

    // Final message when the sorting is complete
    onUpdate(array, emptyList(), emptyList(), "Sorting complete! The list is now in ${if (sortAscending) "ascending" else "descending"} order.")
}

// Selection Sort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionSort(navController: NavController) {
    var inputText by remember { mutableStateOf("") }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    var isSorting by remember { mutableStateOf(false) }
    var timeComplexity by remember { mutableStateOf("Time Complexity: O(n²)") }
    var spaceComplexity by remember { mutableStateOf("Space Complexity: O(1)") }
    var elementsBeingSwapped by remember { mutableStateOf<List<Int>>(emptyList()) }
    var delayTime by remember { mutableStateOf(500L) }
    var swapOffsets by remember { mutableStateOf<List<Float>>(emptyList()) }
    var sortAscending by remember { mutableStateOf(true) }
    var stepDescription by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState() // Remember the state of LazyRow
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
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
                .clickable{dismissKeyboard()}
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier=Modifier.height(50.dp))
            Row() {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter numbers") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        dismissKeyboard()
                        numbers = inputText.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .toMutableList()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Set")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Sort Order: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = sortAscending,
                    onClick = { sortAscending = true },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text("Ascending", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = !sortAscending,
                    onClick = { sortAscending = false },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text("Descending", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isSorting) {
                        isSorting = true
                        timeComplexity = "Time Complexity: O(n²)"
                        spaceComplexity = "Space Complexity: O(1)"
                        numbers = numbers.toMutableList()
                    }
                },
                enabled = numbers.isNotEmpty() && !isSorting
            ) {
                Text("Start Sorting")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Adjust Delay (ms): ${delayTime}ms", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = delayTime.toFloat(),
                onValueChange = { delayTime = it.toLong() },
                valueRange = 100f..3000f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )

            LaunchedEffect(isSorting) {
                if (isSorting && numbers.isNotEmpty()) {
                    selectionSort(
                        numbers.toMutableList(),
                        delayTime,
                        sortAscending
                    ) { sortedArray, elementsToSwap, offsets, stepDesc ->
                        numbers = sortedArray
                        elementsBeingSwapped = elementsToSwap
                        swapOffsets = offsets
                        stepDescription = stepDesc
                    }
                    isSorting = false
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Displaying Numbers with Animation (using LazyRow)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = lazyListState // Assign the state
            ) {
                items(numbers.size) { index ->
                    val isSwapped = index in elementsBeingSwapped
                    val offset = swapOffsets.getOrElse(index) { 0f }

                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(50.dp)
                            .offset(x = animateDpAsState(offset.dp).value)
                            .background(
                                color = if (isSwapped) Color(0xFFff7043) else Color.Gray,
                                shape = CircleShape
                            )
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = numbers[index].toString(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Scroll to the comparing elements
            LaunchedEffect(elementsBeingSwapped) {
                if (elementsBeingSwapped.isNotEmpty()) {
                    val firstIndex = elementsBeingSwapped[0]
                    lazyListState.animateScrollToItem(firstIndex)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stepDescription, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = timeComplexity, style = MaterialTheme.typography.bodyLarge)
            Text(text = spaceComplexity, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
// Selection Sort Algorithm with Improved Comparisons and No Swap Case
suspend fun selectionSort(
    array: MutableList<Int>,
    delayTime: Long,
    sortAscending: Boolean,
    onUpdate: (List<Int>, List<Int>, List<Float>, String) -> Unit
) {
    val n = array.size

    for (i in 0 until n - 1) {
        var selectedIndex = i

        // Iterate over the remaining unsorted part of the array
        for (j in i + 1 until n) {
            // Perform the comparison based on the sorting order
            val comparisonResult = if (sortAscending) {
                array[j] < array[selectedIndex]  // Looking for minimum for ascending
            } else {
                array[j] > array[selectedIndex]  // Looking for maximum for descending
            }

            // Log the comparison result message
            val comparisonDesc = if (sortAscending) {
                "Comparing ${array[selectedIndex]} (current minimum) > ${array[j]} = $comparisonResult"
            } else {
                "Comparing ${array[selectedIndex]} (current maximum) < ${array[j]} = $comparisonResult"
            }

            // Update the UI with the current comparison description
            onUpdate(array, listOf(selectedIndex, j), listOf(i.toFloat(), j.toFloat()), comparisonDesc)
            delay(delayTime)

            // If comparison is true, update selectedIndex to the new min/max element
            if (comparisonResult) {
                selectedIndex = j
            }
        }

        // If the selectedIndex changed, swap the elements
        if (selectedIndex != i) {
            val elementsToSwap = listOf(i, selectedIndex)
            val swapDesc = "Swapping ${array[i]} with ${array[selectedIndex]}"

            // Swap the elements
            val temp = array[i]
            array[i] = array[selectedIndex]
            array[selectedIndex] = temp

            // Update the UI with the swap message
            onUpdate(array, elementsToSwap, listOf(i.toFloat(), selectedIndex.toFloat()), swapDesc)
            delay(delayTime)
        } else {
            // If no swap is needed, show the "No swapping needed" message
            onUpdate(array, emptyList(), emptyList(), "No swapping needed.")
            delay(delayTime)
        }

        // End of pass message
        onUpdate(array, emptyList(), emptyList(), "End of pass ${i + 1}.")
        delay(delayTime)
    }

    // Sorting complete message
    onUpdate(array, emptyList(), emptyList(), "Sorting complete!")
}


//

