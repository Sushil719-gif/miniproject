package com.example.miniproject

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AimlModel1(navController: NavController) {
   Box(
      modifier = Modifier.fillMaxSize()
   ) {

      IconButton(
         onClick = { navController.popBackStack() },
         modifier = Modifier
            .padding(16.dp)
            .align(Alignment.TopStart)
      ) {
         Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
         )
      }


      Column(
         modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         Text("Working on it...........")
      }
   }
}
