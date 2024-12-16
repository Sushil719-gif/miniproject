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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
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
        Column(
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
                .padding(16.dp),
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
                .padding(16.dp),
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
                .fillMaxSize() // Use full screen
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
        Column(
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


