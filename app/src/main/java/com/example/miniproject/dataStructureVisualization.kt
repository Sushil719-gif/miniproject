package com.example.miniproject

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch




//Array
@Composable
fun ArrayVisualizer(navController: NavController) {
    var arraySize by rememberSaveable { mutableStateOf(10) }
    var inputValue by rememberSaveable { mutableStateOf("") }
    var updateIndex by rememberSaveable { mutableStateOf("") }
    var updateValue by rememberSaveable { mutableStateOf("") }
    var deleteIndex by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("Perform array operations!") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    val arrayData = remember { mutableStateListOf<Int?>() }
    val focusManager = LocalFocusManager.current // Manager to dismiss the keyboard

    // Initialize array on size change
    LaunchedEffect(arraySize) {
        arrayData.clear()
        repeat(arraySize) { arrayData.add(null) }
    }

    // Root layout that detects taps
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() }) // Dismiss keyboard on tap
            }
            .padding(16.dp)
    ) {IconButton(
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
        Column(modifier=Modifier.clickable{dismissKeyboard()},
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier=Modifier.height(50.dp))
            Text(
                text = "Array Visualization",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Input for array size
            OutlinedTextField(
                value = arraySize.toString(),
                onValueChange = {
                    val newSize = it.toIntOrNull()
                    if (newSize != null && newSize > 0) {
                        arraySize = newSize
                        message = "Array resized to $newSize elements"
                    } else {
                        message = "Enter a valid array size"
                    }
                },
                label = { Text("Array Size") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Array visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(arrayData) { index, value ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(
                                        if (value != null) Color(0xFF80DEEA) else Color(0xFFEF9A9A),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = value?.toString() ?: "NULL",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Index $index", fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }

            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Add operation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text("Value to Add") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        dismissKeyboard()
                        val value = inputValue.toIntOrNull()
                        if (value != null) {
                            val emptyIndex = arrayData.indexOfFirst { it == null }
                            if (emptyIndex != -1) {
                                arrayData[emptyIndex] = value
                                message = "Added $value at index $emptyIndex"
                            } else {
                                message = "Array is full!"
                            }
                            inputValue = ""
                            focusManager.clearFocus() // Dismiss keyboard
                        } else {
                            message = "Enter a valid number"
                        }
                    }
                ) {
                    Text("Add")
                }
            }

            // Update operation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = updateIndex,
                    onValueChange = { updateIndex = it },
                    label = { Text("Index to Update") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = updateValue,
                    onValueChange = { updateValue = it },
                    label = { Text("New Value") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        dismissKeyboard()
                        val index = updateIndex.toIntOrNull()
                        val value = updateValue.toIntOrNull()
                        if (index != null && value != null && index in arrayData.indices) {
                            arrayData[index] = value
                            message = "Updated index $index with value $value"
                            updateIndex = ""
                            updateValue = ""
                            focusManager.clearFocus() // Dismiss keyboard
                        } else {
                            message = "Enter valid index and value"
                        }
                    }
                ) {
                    Text("Update")
                }
            }

            // Delete operation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = deleteIndex,
                    onValueChange = { deleteIndex = it },
                    label = { Text("Index to Delete") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        dismissKeyboard()
                        val index = deleteIndex.toIntOrNull()
                        if (index != null && index in arrayData.indices) {
                            arrayData[index] = null
                            message = "Deleted value at index $index"
                            deleteIndex = ""
                            focusManager.clearFocus() // Dismiss keyboard
                        } else {
                            message = "Enter a valid index"
                        }
                    }
                ) {
                    Text("Delete")
                }
            }

            // Clear operation
            Button(
                onClick = {
                    arrayData.clear()
                    repeat(arraySize) { arrayData.add(null) }
                    message = "Cleared the array"
                    focusManager.clearFocus() // Dismiss keyboard
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Array")
            }
        }
    }
}


//Stack

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StackVisualizer(navController: NavController) {
    var stack by remember { mutableStateOf(listOf<Int>()) }
    var message by remember { mutableStateOf("Perform stack operations!") }
    var inputValue by remember { mutableStateOf("") }
    var poppedElement by remember { mutableStateOf<Int?>(null) }
    var isPopping by remember { mutableStateOf(false) }
    var isPushing by remember { mutableStateOf(false) }
    var pushedElement by remember { mutableStateOf<Int?>(null) }
    var isPeeking by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
                .clickable{dismissKeyboard()},
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Spacer(modifier=Modifier.height(50.dp))
            Text(
                text = "Stack Visualization",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stack Display using LazyColumn
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (stack.isNotEmpty()) {
                        // Add "Top" message
                        item {
                            Text(
                                text = "Top",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Display stack elements
                        items(stack.reversed()) { value ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(0.6f)
                                    .height(40.dp)
                                    .background(Color(0xFF80DEEA), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = value.toString(), fontSize = 16.sp, color = Color.Black)
                            }
                        }

                        // Add "Bottom" message
                        item {
                            Text(
                                text = "Bottom",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message Display
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input and Buttons (Push operation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text("Enter a Value") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        dismissKeyboard()
                        val value = inputValue.toIntOrNull()
                        if (value != null) {
                            isPushing = true
                            pushedElement = value
                            stack = stack + value
                            message = "Pushed $value onto the stack"
                            inputValue = ""
                        } else {
                            message = "Enter a valid number to push"
                        }
                    }
                ) {
                    Text("Push")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Operations (Pop, Peek, Clear)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        dismissKeyboard()
                        if (stack.isNotEmpty()) {
                            val poppedValue = stack.last()
                            poppedElement = poppedValue
                            isPopping = true
                            stack = stack.dropLast(1)
                            message = "Popped $poppedValue from the stack"
                        } else {
                            message = "Stack is empty, cannot pop"
                        }
                    }
                ) {
                    Text("Pop")
                }

                Button(
                    onClick = {
                        dismissKeyboard()
                        if (stack.isNotEmpty()) {
                            isPeeking = true
                            message = "Peeked at top value: ${stack.last()}"
                        } else {
                            message = "Stack is empty, nothing to peek"
                        }
                    }
                ) {
                    Text("Peek")
                }

                Button(
                    onClick = {
                        stack = listOf()
                        message = "Stack has been cleared"
                        isPeeking = false
                    }
                ) {
                    Text("Clear")
                }
            }

            LaunchedEffect(isPopping) {
                if (isPopping) {
                    delay(300)
                    isPopping = false
                }
            }

            LaunchedEffect(isPushing) {
                if (isPushing) {
                    delay(300)
                    isPushing = false
                }
            }

            LaunchedEffect(isPeeking) {
                if (isPeeking) {
                    delay(1000)
                    isPeeking = false
                }
            }
        }

    }}


//Queue

@Composable
fun QueueVisualizer(navController: NavController) {
    var queue by remember { mutableStateOf(listOf<Int>()) }
    var message by remember { mutableStateOf("Perform queue operations!") }
    var inputValue by remember { mutableStateOf("") }
    var dequeuedElement by remember { mutableStateOf<Int?>(null) }
    var isDequeuing by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    val listState = rememberLazyListState() // State for LazyRow scrolling
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for animations

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
                .clickable{dismissKeyboard()},
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Spacer(modifier=Modifier.height(50.dp))
            Text(
                text = "Queue Visualization",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Queue Display using LazyRow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    state = listState, // Attach LazyListState here
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (queue.isNotEmpty()) {
                        item {
                            // Label for "Front"
                            Text(
                                text = "Front",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    items(queue) { value ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .width(60.dp)
                                .height(40.dp)
                                .background(Color(0xFF80DEEA), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = value.toString(), fontSize = 16.sp, color = Color.Black)
                        }
                    }

                    if (queue.isNotEmpty()) {
                        item {
                            // Label for "Rear"
                            Text(
                                text = "Rear",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message Display
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input and Buttons (Enqueue operation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text("Enter a Value") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        dismissKeyboard()
                        val value = inputValue.toIntOrNull()
                        if (value != null) {
                            queue = queue + value
                            message = "Enqueued $value into the queue"
                            inputValue = ""

                            // Scroll to the latest element in the queue
                            coroutineScope.launch {
                                listState.animateScrollToItem(queue.size)
                            }
                        } else {
                            message = "Enter a valid number to enqueue"
                        }
                    }
                ) {
                    Text("Enqueue")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Operations (Dequeue, Clear)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        dismissKeyboard()
                        if (queue.isNotEmpty()) {
                            val dequeuedValue = queue.first()
                            dequeuedElement = dequeuedValue
                            isDequeuing = true
                            queue = queue.drop(1)
                            message = "Dequeued $dequeuedValue from the queue"

                            // Scroll to the first element after deletion
                            coroutineScope.launch {
                                delay(300) // Wait for the animation
                                listState.animateScrollToItem(0)
                            }
                        } else {
                            message = "Queue is empty, cannot dequeue"
                        }
                    }
                ) {
                    Text("Dequeue")
                }

                Button(
                    onClick = {
                        queue = listOf()
                        message = "Queue has been cleared"
                    }
                ) {
                    Text("Clear")
                }
            }

            LaunchedEffect(isDequeuing) {
                if (isDequeuing) {
                    delay(300)
                    isDequeuing = false
                }
            }
        }
    }
}

//Linked List
@Composable
fun LinkedListVisualizer(navController: NavController) {
    var linkedList by rememberSaveable { mutableStateOf(listOf<Pair<Int, String?>>()) } // Data and Pointer
    var message by rememberSaveable { mutableStateOf("Perform linked list operations!") }
    var inputValue by rememberSaveable { mutableStateOf("") }
    var specificPositionValue by rememberSaveable { mutableStateOf("") }
    var deleteIndex by rememberSaveable { mutableStateOf("") }

    var showAddSpecificFields by rememberSaveable { mutableStateOf(false) }
    var showDeleteSpecificFields by rememberSaveable { mutableStateOf(false) }

    val listState = rememberLazyListState() // State for horizontal scrolling
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val nodeBoxHeight =
        if (isLandscape) 150.dp else 250.dp  // Adjust height for non-landscape (portrait) mode

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clickable{dismissKeyboard()}// Use full screen
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier=Modifier.height(50.dp))
                // Title
                Text(
                    text = "Linked List Visualization",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Linked List Display using LazyRow
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(nodeBoxHeight)  // Set dynamic height based on orientation
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (linkedList.isEmpty()) {
                        Text(
                            text = "The linked list is empty!",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    } else {
                        LazyRow(
                            state = listState,
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // HEAD label
                            item {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "HEAD",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp, 10.dp)
                                            .background(Color.Black, RoundedCornerShape(50))
                                    )
                                }
                            }

                            items(linkedList) { (data, pointer) ->
                                // Shrinking and Growing Effect
                                val nodeSize by animateDpAsState(
                                    targetValue = if (linkedList.isNotEmpty()) 100.dp else 50.dp,
                                    animationSpec = tween(durationMillis = 500)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Node Box with Data and Pointer
                                    Column(
                                        modifier = Modifier
                                            .size(nodeSize, 50.dp) // Apply animated size
                                            .background(
                                                Color(0xFF80DEEA),
                                                RoundedCornerShape(4.dp)
                                            ),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Data: $data",
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Next: ${pointer ?: "NULL"}",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                    }

                                    // Arrow
                                    if (pointer != null) {
                                        Text(
                                            text = "→",
                                            fontSize = 24.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Message Display
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Input and Buttons (Add Node)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        label = { Text("Enter a Value") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            dismissKeyboard()
                            val value = inputValue.toIntOrNull()
                            if (value != null) {
                                linkedList = linkedList + (value to null)
                                if (linkedList.size > 1) {
                                    linkedList = linkedList.mapIndexed { index, node ->
                                        if (index == linkedList.lastIndex - 1) node.copy(second = value.toString()) else node
                                    }
                                }
                                coroutineScope.launch {
                                    listState.animateScrollToItem(linkedList.lastIndex) // Scroll to the last node
                                }
                                message = "Added $value to the linked list"
                                inputValue = ""
                            } else {
                                message = "Enter a valid number"
                            }
                        }
                    ) {
                        Text("Add Node")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                // Buttons for Specific Operations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { showAddSpecificFields = !showAddSpecificFields }) {
                        Text("Add at Position")
                    }

                    Button(onClick = { showDeleteSpecificFields = !showDeleteSpecificFields }) {
                        Text("Delete at Position")
                    }
                }
            }

            if (showAddSpecificFields) {
                item {
                    // Fields for Adding at a Specific Position
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = specificPositionValue,
                            onValueChange = { specificPositionValue = it },
                            label = { Text("Position to Add") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text("Value to Add") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val position = specificPositionValue.toIntOrNull()
                                val value = inputValue.toIntOrNull()
                                if (position != null && value != null && position in 0..linkedList.size) {
                                    linkedList = linkedList.toMutableList().apply {
                                        add(
                                            position,
                                            value to if (position < linkedList.size) linkedList[position].first.toString() else null
                                        )
                                        if (position > 0) this[position - 1] =
                                            this[position - 1].copy(second = value.toString())
                                    }
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(position) // Scroll to the added position
                                    }
                                    message = "Added $value at position $position"
                                    specificPositionValue = ""
                                    inputValue = ""
                                } else {
                                    message = "Enter valid inputs"
                                }
                            }
                        ) {
                            Text("Add")
                        }
                    }
                }
            }

            if (showDeleteSpecificFields) {
                item {
                    // Fields for Deleting at a Specific Position
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = deleteIndex,
                            onValueChange = { deleteIndex = it },
                            label = { Text("Position to Delete") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val position = deleteIndex.toIntOrNull()
                                if (position != null && position in linkedList.indices) {
                                    // Convert the list to a mutable list so we can modify it
                                    val mutableList = linkedList.toMutableList()

                                    // Update the "next" pointer of the previous node to null (if it's the last node)
                                    if (position > 0) {
                                        mutableList[position - 1] =
                                            mutableList[position - 1].copy(second = null)
                                    }

                                    // Remove the node at the specified position
                                    mutableList.removeAt(position)

                                    // If there are still nodes after the deleted one, update their "next" pointers
                                    if (position < mutableList.size) {
                                        mutableList[position].let { nextNode ->
                                            if (position > 0) {
                                                mutableList[position - 1] =
                                                    mutableList[position - 1].copy(second = nextNode.second)
                                            }
                                        }
                                    }

                                    // Update the linkedList state with the modified list
                                    linkedList = mutableList

                                    coroutineScope.launch {
                                        listState.animateScrollToItem(
                                            position.coerceAtMost(
                                                linkedList.lastIndex
                                            )
                                        ) // Scroll to the updated position
                                    }
                                    message = "Deleted node at position $position"
                                    deleteIndex = ""
                                } else {
                                    message = "Enter a valid position"
                                }

                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                // Head and Tail Deletion Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (linkedList.isNotEmpty()) {
                                linkedList = linkedList.drop(1)
                                message = "Deleted the head node"
                            } else {
                                message = "The list is already empty"
                            }
                        }
                    ) {
                        Text("Delete Head")
                    }

                    Button(
                        onClick = {
                            if (linkedList.isNotEmpty()) {
                                // Handle deleting the last node and updating the "next" pointer of the previous node
                                val mutableList = linkedList.toMutableList()

                                // If there is more than one node, we need to update the second-to-last node's pointer
                                if (mutableList.size > 1) {
                                    mutableList[mutableList.size - 2] =
                                        mutableList[mutableList.size - 2].copy(second = null) // Update "next" pointer to null
                                }

                                // Remove the last node from the list
                                mutableList.removeAt(mutableList.size - 1)

                                linkedList = mutableList // Update the state with the new list

                                message = "Deleted the tail node"
                            } else {
                                message = "The list is already empty"
                            }
                        }
                    ) {
                        Text("Delete Tail")
                    }

                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Button(
                    onClick = {
                        linkedList = listOf()
                        message = "Cleared the linked list"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear List")
                }
            }
        }
    }
}

//Hash Table
@Composable
fun HashingVisualizer(navController: NavController) {
    var tableSize by rememberSaveable { mutableStateOf(10) }
    var hashTable = remember { mutableStateListOf<Pair<Int?, Boolean>>() }
    var inputValue by rememberSaveable { mutableStateOf("") }
    var searchValue by rememberSaveable { mutableStateOf("") }
    var deleteValue by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("Perform hashing operations!") }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }

    // Call the side effect function
    HashingSideEffects(tableSize, hashTable, listState)

    fun hashFunction(key: Int): Int {
        return key % tableSize
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() }) // Dismiss keyboard on tap
            }
            .padding(16.dp)
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
        Column(modifier=Modifier.clickable{dismissKeyboard()},
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier=Modifier.height(50.dp))
            Text(
                text = "Hash Table Visualization",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = tableSize.toString(),
                onValueChange = {
                    val newSize = it.toIntOrNull()
                    if (newSize != null && newSize > 0) {
                        tableSize = newSize
                        message = "Hash table resized to $newSize"
                    } else {
                        message = "Enter a valid table size"
                    }
                },
                label = { Text("Hash Table Size") },
                singleLine = true
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(hashTable) { index, pair ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(
                                        if (pair.second) Color(0xFF80DEEA) else Color(0xFFEF9A9A),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pair.first?.toString() ?: "NULL",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Index $index", fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }

            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Add operation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text("Enter a Value") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        dismissKeyboard()
                        focusManager.clearFocus() // Dismiss keyboard
                        val key = inputValue.toIntOrNull()
                        if (key != null) {
                            val hashIndex = hashFunction(key)
                            var index = hashIndex
                            var added = false

                            for (i in 0 until tableSize) {
                                if (hashTable[index].first == null) {
                                    hashTable[index] = key to true
                                    added = true
                                    break
                                }
                                index = (index + 1) % tableSize
                            }

                            message = if (added) {
                                "Inserted $key at index $index using formula (key % size = $hashIndex)"
                            } else {
                                "Hash table is full!"
                            }
                            inputValue = ""
                        } else {
                            message = "Enter a valid integer"
                        }
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Add")
                }
            }

            // Search operation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchValue,
                    onValueChange = { searchValue = it },
                    label = { Text("Search Value") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        dismissKeyboard()
                        focusManager.clearFocus() // Dismiss keyboard
                        val key = searchValue.toIntOrNull()
                        if (key != null) {
                            val hashIndex = hashFunction(key)
                            var index = hashIndex
                            var found = false

                            for (i in 0 until tableSize) {
                                if (hashTable[index].first == key) {
                                    found = true
                                    break
                                }
                                if (hashTable[index].first == null) break
                                index = (index + 1) % tableSize
                            }

                            message = if (found) {
                                "Found $key at index $index using formula (key % size = $hashIndex)"
                            } else {
                                "$key not found in the hash table"
                            }
                            searchValue = ""
                        } else {
                            message = "Enter a valid integer"
                        }
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Search")
                }
            }

            // Delete operation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = deleteValue,
                    onValueChange = { deleteValue = it },
                    label = { Text("Delete Value") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        dismissKeyboard()
                        focusManager.clearFocus() // Dismiss keyboard
                        val key = deleteValue.toIntOrNull()
                        if (key != null) {
                            val hashIndex = hashFunction(key)
                            var index = hashIndex
                            var deleted = false

                            for (i in 0 until tableSize) {
                                if (hashTable[index].first == key) {
                                    hashTable[index] = null to false
                                    deleted = true
                                    break
                                }
                                if (hashTable[index].first == null) break
                                index = (index + 1) % tableSize
                            }

                            message = if (deleted) {
                                "Deleted $key from index $index"
                            } else {
                                "$key not found in the hash table"
                            }
                            deleteValue = ""
                        } else {
                            message = "Enter a valid integer"
                        }
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Delete")
                }
            }

            Button(
                onClick = {
                    focusManager.clearFocus() // Dismiss keyboard
                    hashTable.clear()
                    hashTable.addAll(Array(tableSize) { null to false })
                    message = "Cleared the hash table"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Table")
            }
        }
    }
}

@Composable
fun HashingSideEffects(
    tableSize: Int,
    hashTable: MutableList<Pair<Int?, Boolean>>,
    listState: LazyListState
) {
    LaunchedEffect(tableSize) {
        hashTable.clear()
        hashTable.addAll(Array(tableSize) { null to false })
    }

    LaunchedEffect(hashTable) {
        val index = hashTable.indexOfLast { it.second }
        if (index != -1) {
            listState.animateScrollToItem(index)
        }
    }
}

//Binary Search Tree
data class TreeNode(
    var value: Int,
    var left: TreeNode? = null,
    var right: TreeNode? = null,
    var parent: TreeNode? = null
)

@Composable
fun BinarySearchTreeVisualizer(navController: NavController) {
    var tree by remember { mutableStateOf<TreeNode?>(null) }
    var inputValue by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Welcome! Enter the root node to start.") }
    var traversalResult by remember { mutableStateOf("") }
    var highlightedNode by remember { mutableStateOf<TreeNode?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard when tapping outside any input field or button
    val dismissKeyboard: () -> Unit = {
        keyboardController?.hide()
    }
    val coroutineScope = rememberCoroutineScope()

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
            .clickable{dismissKeyboard()},
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(50.dp))
        Text(
            text = "BST Visualization",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )

        // Tree Visualization
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (tree != null) {
                    drawTree(tree!!, size.width / 2, 50f, 0, size.width / 2, 50f, highlightedNode, tree)
                }
            }
        }

        // Message display
        Text(
            text = message,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(8.dp)
        )

        // Input field and Add Node button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("Enter Node Value") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    dismissKeyboard()
                    val value = inputValue.toIntOrNull()
                    if (value == null) {
                        message = "Please enter a valid integer value."
                        return@Button
                    }
                    if (tree == null) {
                        tree = TreeNode(value)
                        message = "Root node $value added."
                        highlightedNode = tree
                    } else {
                        message = insertNode(tree!!, value)
                        highlightedNode = searchNode(tree, value)
                    }
                    inputValue = ""
                    keyboardController?.hide()
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Add Node")
            }
        }

        // Search and Delete Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    dismissKeyboard()
                    val value = inputValue.toIntOrNull()
                    if (value == null) {
                        message = "Please enter a valid integer value to search."
                        return@Button
                    }
                    searchNode(tree, value)?.let {
                        highlightedNode = it
                        message = "Node $value found."
                    } ?: run {
                        message = "Node $value not found."
                    }
                    inputValue = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Search Node")
            }

            Button(
                onClick = {
                    dismissKeyboard()
                    val value = inputValue.toIntOrNull()
                    if (value == null) {
                        message = "Please enter a valid integer value to delete."
                        return@Button
                    }

                    var deletionMessages = mutableListOf<String>() // Collect messages from the deletion process
                    var isNodeDeleted = false // Track if the node was actually deleted

                    tree = deleteNode(tree, value) { comparisonMessage, deletionOccurred ->
                        deletionMessages.add(comparisonMessage) // Append messages from `onMessage` callback
                        if (deletionOccurred) isNodeDeleted = true // Update flag if deletion occurred
                    }

                    if (isNodeDeleted) {
                        message = deletionMessages.joinToString("\n") + "\nNode $value deleted."
                        highlightedNode = null
                    } else {
                        message = deletionMessages.joinToString("\n") + "\nNode $value not found for deletion."
                    }

                    inputValue = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete Node")
            }


        }

        // Tree Traversal Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    traversalResult = ""
                    coroutineScope.launch {
                        inOrderTraversal(tree) { node ->
                            highlightedNode = node
                            traversalResult += "${node.value}, "
                            message = "Visiting node ${node.value} (In-order)."
                            delay(500)
                        }
                        message = "In-order Traversal: ${traversalResult.trimEnd(',', ' ')}"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("In-order")
            }

            Button(
                onClick = {
                    traversalResult = ""
                    coroutineScope.launch {
                        preOrderTraversal(tree) { node ->
                            highlightedNode = node
                            traversalResult += "${node.value}, "
                            message = "Visiting node ${node.value} (Pre-order)."
                            delay(500)
                        }
                        message = "Pre-order Traversal: ${traversalResult.trimEnd(',', ' ')}"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Pre-order")
            }

            Button(
                onClick = {
                    traversalResult = ""
                    coroutineScope.launch {
                        postOrderTraversal(tree) { node ->
                            highlightedNode = node
                            traversalResult += "${node.value}, "
                            message = "Visiting node ${node.value} (Post-order)."
                            delay(500)
                        }
                        message = "Post-order Traversal: ${traversalResult.trimEnd(',', ' ')}"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Post-order")
            }
        }
    }
}}

suspend fun inOrderTraversal(node: TreeNode?, onVisit: suspend (TreeNode) -> Unit) {
    if (node == null) return
    inOrderTraversal(node.left, onVisit)
    onVisit(node)
    inOrderTraversal(node.right, onVisit)
}

suspend fun preOrderTraversal(node: TreeNode?, onVisit: suspend (TreeNode) -> Unit) {
    if (node == null) return
    onVisit(node)
    preOrderTraversal(node.left, onVisit)
    preOrderTraversal(node.right, onVisit)
}

suspend fun postOrderTraversal(node: TreeNode?, onVisit: suspend (TreeNode) -> Unit) {
    if (node == null) return
    postOrderTraversal(node.left, onVisit)
    postOrderTraversal(node.right, onVisit)
    onVisit(node)
}

// Insert, delete, and drawTree functions remain as updated above

fun insertNode(root: TreeNode, value: Int): String {
    var currentNode: TreeNode? = root
    var parent: TreeNode? = null
    val messageBuilder = StringBuilder()

    while (currentNode != null) {
        parent = currentNode
        // Log the comparison
        messageBuilder.append("Comparing $value with ${currentNode.value}. ")

        if (value == currentNode.value) {
            // Duplicate value, do not allow insertion
            messageBuilder.append("$value already exists in the tree. No node added.")
            return messageBuilder.toString()
        }

        if (value < currentNode.value) {
            messageBuilder.append("$value < ${currentNode.value}, moving to left child. ")
            if (currentNode.left == null) break
            currentNode = currentNode.left
        } else {
            messageBuilder.append("$value > ${currentNode.value}, moving to right child. ")
            if (currentNode.right == null) break
            currentNode = currentNode.right
        }
    }

    // Create and insert the new node
    val newNode = TreeNode(value, parent = parent)
    if (value < parent!!.value) {
        parent.left = newNode
        messageBuilder.append("$value inserted as the left child of ${parent.value}.")
    } else {
        parent.right = newNode
        messageBuilder.append("$value inserted as the right child of ${parent.value}.")
    }

    return messageBuilder.toString()
}

fun deleteNode(
    root: TreeNode?,
    value: Int,
    onMessage: (String, Boolean) -> Unit
): TreeNode? {
    if (root == null) {
        onMessage("Tree is empty or node $value not found.", false)
        return null
    }

    var current = root
    var parent: TreeNode? = null
    var isLeftChild = false

    // Traverse the tree to find the node
    while (current != null) {
        when {
            value < current.value -> {
                onMessage("Comparing $value with ${current.value}, $value < ${current.value}, moving to the left.", false)
                parent = current
                current = current.left
                isLeftChild = true
            }
            value > current.value -> {
                onMessage("Comparing $value with ${current.value}, $value > ${current.value}, moving to the right.", false)
                parent = current
                current = current.right
                isLeftChild = false
            }
            else -> {
                onMessage("Node $value found for deletion.", false)
                break
            }
        }
    }

    if (current == null) {
        onMessage("Node $value not found in the tree.", false)
        return root
    }

    // Case 1: Node has no children
    if (current.left == null && current.right == null) {
        if (parent == null) {
            onMessage("Deleting root node $value with no children.", true)
            return null // The tree becomes empty
        }

        if (isLeftChild) {
            parent.left = null
        } else {
            parent.right = null
        }

        onMessage("Node $value has no children, Deleting.....", true)
    }
    // Case 2: Node has one child
    else if (current.left == null || current.right == null) {
        val child = current.left ?: current.right

        if (parent == null) {
            onMessage("Root node $value deleted and replaced by its only child ${child?.value}.", true)
            child?.parent = null
            return child
        }

        if (isLeftChild) {
            parent.left = child
        } else {
            parent.right = child
        }

        child?.parent = parent
        onMessage("Node $value deleted and replaced by its only child ${child?.value}.", true)
    }
    // Case 3: Node has two children
    else {
        // Find the in-order successor (smallest node in the right subtree)
        var successor = current.right
        var successorParent = current

        while (successor?.left != null) {
            successorParent = successor
            successor = successor.left
        }

        onMessage("In-order successor of $value is ${successor?.value}.", false)

        // Replace current node's value with the successor's value
        current.value = successor!!.value

        // Delete the successor node (it will have at most one child)
        if (successorParent != null) {
            if (successorParent.left == successor) {
                successorParent.left = successor.right
            } else {
                successorParent.right = successor.right
            }
        }

        if (successor.right != null) {
            successor.right?.parent = successorParent
        }

        onMessage("Node $value deleted and replaced with its in-order successor ${successor.value}.", true)
    }

    return root
}


fun searchNode(root: TreeNode?, value: Int): TreeNode? {
    var currentNode = root
    while (currentNode != null) {
        if (value == currentNode.value) {
            return currentNode
        }
        currentNode = if (value < currentNode.value) currentNode.left else currentNode.right
    }
    return null
}
fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTree(
    node: TreeNode?,
    x: Float,
    y: Float,
    depth: Int,
    parentX: Float,
    parentY: Float,
    highlightedNode: TreeNode?,
    root: TreeNode?
) {
    // Ensure root is passed and is null to check for an empty tree
    if (root == null) {
        // Draw message in the center of the canvas
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 50f // Font size for the message
                color = android.graphics.Color.BLACK
            }
            canvas.nativeCanvas.drawText(
                "The tree is empty!",
                size.width / 2, // Center horizontally
                size.height / 2, // Center vertically
                paint
            )
        }
        return
    }

    if (node == null) return

    val nodeRadius = 40f
    val verticalSpacing = 150f
    val horizontalSpacing = 200f / (depth + 1)

    // Draw line from parent to current node if not the root
    if (depth > 0) {
        drawLine(
            color = Color.Black,
            start = Offset(parentX, parentY + nodeRadius),
            end = Offset(x, y - nodeRadius),
            strokeWidth = 4f
        )
    }

    // Draw the node circle
    drawCircle(
        color = when {
            node == highlightedNode -> Color.Green // Highlighted node
            node == root -> Color.Red             // Root node
            else -> Color.Cyan                    // Regular node
        },
        radius = nodeRadius,
        center = Offset(x, y)
    )

    // Draw the node value inside the circle
    drawContext.canvas.nativeCanvas.drawText(
        node.value.toString(),
        x,
        y + 10f, // Center the text vertically in the circle
        android.graphics.Paint().apply {
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 40f // Font size for the node value
            color = android.graphics.Color.BLACK
        }
    )

    // Add a label for the root node only
    if (node == root) {
        drawContext.canvas.nativeCanvas.drawText(
            "Root",
            x,
            y - nodeRadius - 10f, // Position above the root node
            android.graphics.Paint().apply {
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 30f // Font size for the label
                color = android.graphics.Color.BLACK
            }
        )
    }

    // Recursively draw the left and right children
    drawTree(
        node.left,
        x - horizontalSpacing, // Shift left child horizontally
        y + verticalSpacing,   // Move down vertically
        depth + 1,
        x,
        y,
        highlightedNode,
        root
    )
    drawTree(
        node.right,
        x + horizontalSpacing, // Shift right child horizontally
        y + verticalSpacing,   // Move down vertically
        depth + 1,
        x,
        y,
        highlightedNode,
        root
    )
}
