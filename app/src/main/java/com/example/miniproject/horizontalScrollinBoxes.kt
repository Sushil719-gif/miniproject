package com.example.miniproject

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextForegroundStyle.Unspecified.brush
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun HorizontalScrollingBoxes(navController: NavController,authViewModel: AuthViewModel) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .height(600.dp) // Set a fixed height for the boxes
                .horizontalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row for horizontal scrolling boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(650.dp)
                        .padding(end=5.dp,start=5.dp)
                        .padding(bottom=20.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF101322), Color(0xFF3B3157), Color(0xFF2A2C44)),
                                start = Offset(0f, 0f),
                                end = Offset(300f, 300f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Text with Typewriter Effect
                    val text = "Visualize Data structures and algorithms, master concepts, and elevate your coding expertise!"
                    var displayedText by remember { mutableStateOf("") }

                    // Launching the typewriter effect animation continuously
                    LaunchedEffect(Unit) {
                        while (true) {
                            for (i in text.indices) {
                                displayedText += text[i]
                                delay(100) // Adjust speed of typing here
                            }
                            delay(3000) // Wait for a brief moment before restarting the typing effect
                            displayedText = "" // Reset the text to start typing again
                        }
                    }

                    // Displaying the typed text
                    Text(
                        text = displayedText,
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 28.sp,
                            // Slightly smaller font for elegant design
                            fontFamily = FontFamily.Monospace, // Monospace for a typewriter-like feel
                            letterSpacing = 1.8.sp,
                            lineHeight = 35.sp,
                            // Improved line spacing for readability
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 21.dp, vertical = 18.dp)
                    )

                    // Button for navigation
                    Button(
                        onClick = { navController.navigate("visualize") },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .width(180.dp)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B3157)) // Button color matches the gradient theme
                    ) {
                        Text("Visualize", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }





                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(650.dp)
                        .padding(end=5.dp,start=5.dp)
                        .padding(bottom=20.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF0D1117), Color(0xFF253149), Color(0xFF313C50)),
                                start = Offset(0f, 0f),
                                end = Offset(300f, 300f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Text with Typewriter Effect
                    val text = "Paste your code, uncover complexity, and gain insights for performance and optimization!"
                    var displayedText by remember { mutableStateOf("") }

                    // Launching the typewriter effect animation continuously
                    LaunchedEffect(Unit) {
                        while (true) {
                            for (i in text.indices) {
                                displayedText += text[i]
                                delay(100) // Adjust speed of typing here
                            }
                            delay(3000) // Wait for a brief moment before restarting the typing effect
                            displayedText = "" // Reset the text to start typing again
                        }
                    }

                    // Displaying the typed text
                    Text(
                        text = displayedText,
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 28.sp,
                            // Slightly smaller font for elegant design
                            fontFamily = FontFamily.Monospace, // Monospace for a typewriter-like feel
                            letterSpacing = 1.8.sp,
                            lineHeight = 35.sp,
                            // Improved line spacing for readability
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 21.dp, vertical = 30.dp)
                    )

                    // Button for navigation
                    Button(
                        onClick = {
                            navController.navigate("model1")
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .width(180.dp)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF253149)) // Matches the gradient theme
                    ) {
                        Text("Paste", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }


                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(650.dp)
                        .padding(end=5.dp,start=5.dp)
                        .padding(bottom=20.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF0D1117), Color(0xFF253149), Color(0xFF313C50)),
                                start = Offset(0f, 0f),
                                end = Offset(300f, 300f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Text with Typewriter Effect
                    val text = "Paste your problem statement to get the best algorithm recommendations for efficient solutions!"
                    var displayedText by remember { mutableStateOf("") }

                    // Launching the typewriter effect animation continuously
                    LaunchedEffect(Unit) {
                        while (true) {
                            for (i in text.indices) {
                                displayedText += text[i]
                                delay(100) // Adjust speed of typing here
                            }
                            delay(3000) // Wait for a brief moment before restarting the typing effect
                            displayedText = "" // Reset the text to start typing again
                        }
                    }

                    // Displaying the typed text
                    Text(
                        text = displayedText,
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 28.sp,
                            // Slightly smaller font for elegant design
                            fontFamily = FontFamily.Monospace, // Monospace for a typewriter-like feel
                            letterSpacing = 1.8.sp,
                            lineHeight = 35.sp,
                            // Improved line spacing for readability
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 21.dp, vertical = 30.dp)
                    )

                    // Button for navigation
                    Button(
                        onClick = {
                            navController.navigate("model2")
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .width(180.dp)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF253149)) // Matches the gradient theme
                    ) {
                        Text("Paste", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

            }
        }

        // Scroll Indicators at the bottom
        ScrollIndicators(scrollState, boxCount = 3, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
    }
}


@Composable
fun ScrollIndicators(scrollState: ScrollState, boxCount: Int, modifier: Modifier = Modifier) {
    val indicatorWidth = 16.dp
    val boxWidth = 300.dp.toPx() // Width of each box

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until boxCount) {
            // Determine the active box based on the scroll state
            val isActive = (scrollState.value + (boxWidth / 2) >= (i * boxWidth)) && (scrollState.value + (boxWidth / 2) < ((i + 1) * boxWidth))
            Box(
                modifier = Modifier
                    .size(indicatorWidth)
                    .padding(4.dp)
                    .background(if (isActive) Color.Blue else Color.LightGray)
            )
        }
    }
}

@Composable
private fun Dp.toPx(): Float {
    return with(LocalDensity.current) { toPx() }
}


