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
            BubbleSort(navController)
        }
        composable("InsertionSort"){
            InsertionSort(navController)
        }
        composable("SelectionSort"){
            SelectionSort(navController)
        }
        composable("LinearSearch"){
            LinearSearchVisualizer(navController)
        }
        composable("BinarySearch"){
            BinarySearchVisualizer(navController)
        }
        composable("Stack"){
            StackVisualizer(navController)
        }
        composable("Queue"){
            QueueVisualizer(navController)
        }
        composable("Linked_list"){
            LinkedListVisualizer(navController)
        }
        composable("Hash_table"){
            HashingVisualizer(navController)
        }
        composable("Array"){
            ArrayVisualizer(navController)
        }
    })
}