package com.example.miniproject

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miniproject.pager.HomePage
import com.example.miniproject.pager.LoginPage
import com.example.miniproject.pager.SignupPage

@Composable
fun MyAppNavigation(modifier: Modifier =Modifier,authViewModel: AuthViewModel){
    val navController=rememberNavController()
    NavHost(navController=navController,startDestination="login",builder= {
        composable("Login"){
            LoginPage(modifier,navController,authViewModel)
    }
        composable("Signup"){
           SignupPage(modifier,navController,authViewModel)
        }
        composable("Home"){
            HomePage(modifier,navController,authViewModel)
        }
        composable("visualize"){
            VisualHomePage(navController)
        }
        composable("model1"){
            AimlModel1(navController)
        }
        composable("model2"){
            AimlModel2(navController)
        }
        composable("BubbleSort"){
            SortingVisualizer1(navController)
        }
        composable("InsertionSort"){
            SortingVisualizer2(navController)
        }
        composable("SelectionSort"){
            SortingVisualizer3(navController)
        }
    })
}