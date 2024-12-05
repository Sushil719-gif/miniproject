package com.example.miniproject.pager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.miniproject.AuthState
import com.example.miniproject.AuthViewModel
import com.example.miniproject.HorizontalScrollingBoxes



@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel){
val authState=authViewModel.authState.observeAsState()
    LaunchedEffect (authState.value){
        when(authState.value){
            is AuthState.Unauthenticated ->navController.navigate("login")
            else->Unit

        }
    }
    Column(modifier=modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End) {
        TextButton(onClick = {
            authViewModel.signout()
        }){
            Text(text="Logout")
        }

        HorizontalScrollingBoxes(navController,authViewModel)

    }
}


