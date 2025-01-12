package com.example.miniproject




import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.max


//AVL Tree
data class AVLNode(
    var value: Int,
    var height: Int = 1,
    var left: AVLNode? = null,
    var right: AVLNode? = null,
    var parent: AVLNode? = null
)

@Composable
fun AVLTreeBuilder(navController: NavController) {
    var tree by remember { mutableStateOf<AVLNode?>(null) }
    var inputValue by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Welcome! Enter the root node to start.") }
    var traversalResult by remember { mutableStateOf("") }
    var highlightedNode by remember { mutableStateOf<AVLNode?>(null) }


    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
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
            .pointerInput(Unit) {
                detectTapGestures { keyboardController?.hide() }
            },
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AVL Tree Visualization",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (tree != null) {
                    drawAVLTree(tree!!, size.width / 2, 50f, 0, size.width / 2, 50f, highlightedNode, tree)
                }
            }
        }

        Text(
            text = message,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(8.dp)
        )

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
                    val value = inputValue.toIntOrNull()
                    if (value == null) {
                        message = "Please enter a valid integer value."
                        return@Button
                    }
                    tree = avlInsert(tree, value) { msg -> message = msg }
                    inputValue = ""
                    keyboardController?.hide()
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Add Node")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val value = inputValue.toIntOrNull()
                    if (value == null) {
                        message = "Please enter a valid integer value to delete."
                        return@Button
                    }
                    tree = avlDelete(tree, value) { msg -> message = msg }
                    inputValue = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete Node")
            }

            Button(
                onClick = {
                    val value = inputValue.toIntOrNull()
                    if (value == null) {
                        message = "Please enter a valid integer value to search."
                        return@Button
                    }
                    val foundNode = searchAVLNode(tree, value)
                    if (foundNode != null) {
                        highlightedNode = foundNode
                        message = "Node $value found."
                    } else {
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
                    tree = null
                    message = "The tree has been reset."
                    highlightedNode = null
                    inputValue = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }
        }
    }
}}
// AVL Tree Insertion
fun avlInsert(root: AVLNode?, value: Int, onMessage: (String) -> Unit): AVLNode? {
    if (root == null) {
        onMessage("The tree is empty. Adding node $value as the root of the tree.")
        return AVLNode(value)
    }

    if (value < root.value) {
        onMessage("Value $value is smaller than ${root.value}. Going to the left side of the tree.")
        root.left = avlInsert(root.left, value, onMessage)
        root.left?.parent = root
        onMessage("Node $value added as the left child of ${root.value}.")
    } else if (value > root.value) {
        onMessage("Value $value is larger than ${root.value}. Going to the right side of the tree.")
        root.right = avlInsert(root.right, value, onMessage)
        root.right?.parent = root
        onMessage("Node $value added as the right child of ${root.value}.")
    } else {
        onMessage("Node $value already exists in the tree. No duplicates allowed.")
        return root
    }

    // Update height with 0-based indexing
    root.height = max(height(root.left), height(root.right)) + 1
    onMessage("Node added. Updated height of tree to ${root.height-1}.")

    val balance = getBalance(root)

    // Show balance factor message only if unbalanced
    if (balance > 1 || balance < -1) {
        onMessage("Node ${root.value} added to the tree.")
    }

    // Perform rotations if needed to keep the tree balanced
    if (balance > 1 && value < root.left!!.value) {
        onMessage("The tree is too heavy on the left side. Rotating right at node ${root.value}.")
        return rightRotate(root)
    }
    if (balance < -1 && value > root.right!!.value) {
        onMessage("The tree is too heavy on the right side. Rotating left at node ${root.value}.")
        return leftRotate(root)
    }
    if (balance > 1 && value > root.left!!.value) {
        onMessage("The tree is left-heavy, but $value is on the right side of the left child. Doing two rotations: left then right at node ${root.value}.")
        root.left = leftRotate(root.left!!)
        return rightRotate(root)
    }
    if (balance < -1 && value < root.right!!.value) {
        onMessage("The tree is right-heavy, but $value is on the left side of the right child. Doing two rotations: right then left at node ${root.value}.")
        root.right = rightRotate(root.right!!)
        return leftRotate(root)
    }

    return root
}

// AVL Tree Deletion
fun avlDelete(root: AVLNode?, value: Int, onMessage: (String) -> Unit): AVLNode? {
    if (root == null) {
        onMessage("Node $value not found. Nothing to delete.")
        return null
    }

    when {
        value < root.value -> {
            onMessage("Value $value is smaller than ${root.value}. Going to the left side.")
            root.left = avlDelete(root.left, value, onMessage)
        }
        value > root.value -> {
            onMessage("Value $value is larger than ${root.value}. Going to the right side.")
            root.right = avlDelete(root.right, value, onMessage)
        }
        else -> {
            onMessage("Found node $value. Deleting it.")
            if (root.left == null || root.right == null) {
                val temp = root.left ?: root.right
                onMessage("Node $value deleted. Replacing with its child (if any).")
                return temp
            }

            val successor = getMinValueNode(root.right!!)
            onMessage("Node $value has two children. Replacing it with its next smallest node ${successor.value}.")
            root.value = successor.value
            root.right = avlDelete(root.right, successor.value, onMessage)
        }
    }

    // Update height with 0-based indexing
    root.height = max(height(root.left), height(root.right)) + 1
    onMessage("Updated height of node ${root.value} to ${root.height}.")

    val balance = getBalance(root)

    // Show balance factor message only if unbalanced
    if (balance > 1 || balance < -1) {
        onMessage("Balance of node ${root.value} is $balance (difference between left and right side heights).")
    }

    // Perform rotations if needed to keep the tree balanced
    if (balance > 1 && getBalance(root.left) >= 0) {
        onMessage("The tree is too heavy on the left side. Rotating right at node ${root.value}.")
        return rightRotate(root)
    }
    if (balance > 1 && getBalance(root.left) < 0) {
        onMessage("The tree is left-heavy, but the left child is too heavy on the right. Doing two rotations: left then right at node ${root.value}.")
        root.left = leftRotate(root.left!!)
        return rightRotate(root)
    }
    if (balance < -1 && getBalance(root.right) <= 0) {
        onMessage("The tree is too heavy on the right side. Rotating left at node ${root.value}.")
        return leftRotate(root)
    }
    if (balance < -1 && getBalance(root.right) > 0) {
        onMessage("The tree is right-heavy, but the right child is too heavy on the left. Doing two rotations: right then left at node ${root.value}.")
        root.right = rightRotate(root.right!!)
        return leftRotate(root)
    }

    return root
}

// Utility Functions
fun height(node: AVLNode?): Int = node?.height ?: 0  // Leaf nodes will have height 0
fun getBalance(node: AVLNode?): Int = height(node?.left) - height(node?.right)
fun getMinValueNode(node: AVLNode): AVLNode = node.left?.let { getMinValueNode(it) } ?: node

fun rightRotate(y: AVLNode): AVLNode {
    val x = y.left!!
    val T2 = x.right

    x.right = y
    y.left = T2

    y.height = max(height(y.left), height(y.right)) + 1
    x.height = max(height(x.left), height(x.right)) + 1

    return x
}

fun leftRotate(x: AVLNode): AVLNode {
    val y = x.right!!
    val T2 = y.left

    y.left = x
    x.right = T2

    x.height = max(height(x.left), height(x.right)) + 1
    y.height = max(height(y.left), height(y.right)) + 1

    return y
}


fun searchAVLNode(root: AVLNode?, value: Int): AVLNode? {
    var current = root
    while (current != null) {
        if (value == current.value) return current
        current = if (value < current.value) current.left else current.right
    }
    return null
}

// Visualization
fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAVLTree(
    node: AVLNode?,
    x: Float,
    y: Float,
    depth: Int,
    parentX: Float,
    parentY: Float,
    highlightedNode: AVLNode?,
    root: AVLNode?
) {
    if (node == null) return

    val nodeRadius = 40f
    val verticalSpacing = 150f
    val horizontalSpacing = 200f / (depth + 1)

    if (depth > 0) {
        drawLine(
            color = Color.Black,
            start = Offset(parentX, parentY + nodeRadius),
            end = Offset(x, y - nodeRadius),
            strokeWidth = 4f
        )
    }

    drawCircle(
        color = when {
            node == highlightedNode -> Color.Green
            node == root -> Color.Red
            else -> Color.Cyan
        },
        radius = nodeRadius,
        center = Offset(x, y)
    )

    drawContext.canvas.nativeCanvas.drawText(
        node.value.toString(),
        x,
        y + 10f,
        android.graphics.Paint().apply {
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 40f
            color = android.graphics.Color.BLACK
        }
    )

    drawAVLTree(
        node.left,
        x - horizontalSpacing,
        y + verticalSpacing,
        depth + 1,
        x,
        y,
        highlightedNode,
        root
    )
    drawAVLTree(
        node.right,
        x + horizontalSpacing,
        y + verticalSpacing,
        depth + 1,
        x,
        y,
        highlightedNode,
        root
    )
}


//Heap...

@Composable
fun HeapVisualizer(navController: NavController) {
    var heap by remember { mutableStateOf(Heap()) }
    var inputValue by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Welcome! Enter values to insert into the heap.") }
    var deletedNode by remember { mutableStateOf<HeapNode?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current

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
                .pointerInput(Unit) {
                    detectTapGestures { keyboardController?.hide() }
                },
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Heap Visualization",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawHeapTree(
                        heap,
                        size.width / 2,
                        50f,
                        0,
                        size.width / 2,
                        50f,
                        deletedNode,
                        heap.nodes
                    )
                }
            }

            Text(
                text = message,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        heap = Heap(isMinHeap = true)
                        message = "MinHeap selected. Start inserting nodes."
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("MinHeap")
                }

                Button(
                    onClick = {
                        heap = Heap(isMinHeap = false)
                        message = "MaxHeap selected. Start inserting nodes."
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("MaxHeap")
                }
            }

            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("Enter Node Value") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = {
                        val value = inputValue.toIntOrNull()
                        if (value == null) {
                            message = "Please enter a valid integer value."
                            return@Button
                        }
                        heap.insert(value, onMessage = { msg -> message = msg })
                        inputValue = ""
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Insert Node")
                }

                Button(
                    onClick = {
                        val deletedValue = heap.deleteRoot(onMessage = { msg -> message = msg })
                        message = deletedValue?.let { "Root $it deleted. Heapified." }
                            ?: "No root to delete."
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Delete Root")
                }
            }


            LaunchedEffect(deletedNode) {
                // Reset the deleted node state after a short delay or animation duration
                delay(500)  // Example delay
                deletedNode = null
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        heap = Heap()
                        message = "Heap has been reset. Choose a heap type to start again."
                        deletedNode = null
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Reset Heap")
                }
            }
        }
    }
}
fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeapTree(
    heap: Heap,
    x: Float,
    y: Float,
    depth: Int,
    parentX: Float,
    parentY: Float,
    highlightedNode: HeapNode?,
    nodes: List<HeapNode>
) {
    if (nodes.isEmpty()) return

    val nodeRadius = 40f
    val verticalSpacing = 150f
    val horizontalSpacing = 200f / (depth + 1)

    if (depth > 0) {
        drawLine(
            color = Color.Black,
            start = Offset(parentX, parentY + nodeRadius),
            end = Offset(x, y - nodeRadius),
            strokeWidth = 4f
        )
    }

    val node = nodes[depth]
    drawCircle(
        color = if (node == highlightedNode) Color.Green else Color.Cyan,
        radius = nodeRadius,
        center = Offset(x, y)
    )

    drawContext.canvas.nativeCanvas.drawText(
        node.value.toString(),
        x,
        y + 10f,
        android.graphics.Paint().apply {
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 40f
            color = android.graphics.Color.BLACK
        }
    )

    if (2 * depth + 1 < nodes.size) {
        drawHeapTree(heap, x - horizontalSpacing, y + verticalSpacing, 2 * depth + 1, x, y, highlightedNode, nodes)
    }

    if (2 * depth + 2 < nodes.size) {
        drawHeapTree(heap, x + horizontalSpacing, y + verticalSpacing, 2 * depth + 2, x, y, highlightedNode, nodes)
    }
}

class HeapNode(var value: Int)

class Heap(private val isMinHeap: Boolean = true) {
    var nodes = mutableListOf<HeapNode>()

    fun insert(value: Int, onMessage: (String) -> Unit) {
        nodes.add(HeapNode(value))
        onMessage("Node $value added. Now heapifying upwards...")
        heapifyUp(nodes.size - 1, onMessage)
    }

    fun deleteRoot(onMessage: (String) -> Unit): Int? {
        if (nodes.isEmpty()) {
            onMessage("Heap is empty. Nothing to delete.")
            return null
        }

        // If there's only one node, delete it without needing to heapify
        if (nodes.size == 1) {
            val rootValue = nodes[0].value
            nodes.removeAt(0)
            onMessage("Root $rootValue deleted. Heap is now empty.")
            return rootValue
        }

        val rootValue = nodes[0].value
        nodes[0] = nodes[nodes.size - 1]
        nodes.removeAt(nodes.size - 1)
        onMessage("Root $rootValue deleted. Now heapifying downwards...")
        heapifyDown(0, onMessage)
        return rootValue
    }


    private fun heapifyUp(index: Int, onMessage: (String) -> Unit) {
        var i = index
        val value = nodes[i].value
        while (i > 0) {
            val parentIndex = (i - 1) / 2
            val parentValue = nodes[parentIndex].value
            if ((isMinHeap && value < parentValue) || (!isMinHeap && value > parentValue)) {
                nodes[i].value = parentValue
                nodes[parentIndex].value = value
                onMessage("Swapping $value with $parentValue")
                i = parentIndex
            } else {
                break
            }
        }
    }

    private fun heapifyDown(index: Int, onMessage: (String) -> Unit) {
        var i = index
        val size = nodes.size
        val value = nodes[i].value
        while (i < size) {
            val leftChildIndex = 2 * i + 1
            val rightChildIndex = 2 * i + 2
            var swapIndex = i

            if (leftChildIndex < size && ((isMinHeap && nodes[leftChildIndex].value < nodes[swapIndex].value) || (!isMinHeap && nodes[leftChildIndex].value > nodes[swapIndex].value))) {
                swapIndex = leftChildIndex
            }
            if (rightChildIndex < size && ((isMinHeap && nodes[rightChildIndex].value < nodes[swapIndex].value) || (!isMinHeap && nodes[rightChildIndex].value > nodes[swapIndex].value))) {
                swapIndex = rightChildIndex
            }

            if (swapIndex != i) {
                nodes[i].value = nodes[swapIndex].value
                nodes[swapIndex].value = value
                onMessage("Swapping $value with ${nodes[swapIndex].value}")
                i = swapIndex
            } else {
                break
            }
        }
    }
}

