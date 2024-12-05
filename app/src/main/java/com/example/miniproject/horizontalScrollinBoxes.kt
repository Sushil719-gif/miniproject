package com.example.miniproject

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextForegroundStyle.Unspecified.brush
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController

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
                        .width(370.dp)
                        .height(600.dp)
                        .size(300.dp)
                        .padding(4.dp)
                        .background(Color.Gray)
                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.demo),
//                        contentDescription = "Background Image",
//                        modifier = Modifier.fillMaxSize()
//                            .height(900.dp)
//                            // Makes the image fill the entire box
//                    )

                    Button(onClick = {navController.navigate("visualize")},
                         modifier = Modifier.align(Alignment.BottomCenter)
                        .width(150.dp)
                             .padding(10.dp)

                        ) {
                        Text("Visualize",color=Color.White)
                    }
                }


                Box(
                    modifier = Modifier
                        .width(370.dp)
                        .height(600.dp)
                        .size(300.dp)
                        .padding(4.dp)
                        .background(Color.Gray)
                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.demo),
//                        contentDescription = "Background Image",
//                        modifier = Modifier.fillMaxSize()
//                            .height(600.dp)// Makes the image fill the entire box
//                    )
                    Button(onClick = {
                        navController.navigate("model1")
                    },
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .width(150.dp)
                            .padding(10.dp)

                    ) {
                        Text("Paste",color=Color.White)
                    }
                }


                Box(
                    modifier = Modifier
                        .width(370.dp)
                        .height(600.dp)
                        .size(300.dp)
                        .padding(4.dp)
                        .background(Color.Gray)
                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.demo),
//                        contentDescription = "Background Image",
//                        modifier = Modifier.fillMaxSize()
//                            .height(600.dp)// Makes the image fill the entire box
//                    )
                    Button(onClick = {
                        navController.navigate("model2")
                    },
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .width(150.dp)
                            .padding(10.dp)

                    ) {
                        Text("Paste",color=Color.White)
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


