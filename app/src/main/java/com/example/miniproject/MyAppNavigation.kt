package com.example.miniproject

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
        composable("MergeSort"){
            MergeSortVisualization(navController)
        }
        composable("QuickSort"){
           QuickSortVisualization(navController)
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
        composable("BST"){
            BinarySearchTreeVisualizer(navController)
        }
        composable("IPS"){
            InterpolationSearchVisualizer(navController)
        }
        composable("DFS"){
            DFSGraphTraversal(navController)
        }
        composable("BFS"){
            BFSGraphTraversal(navController)
        }
        composable("TopoSort"){
            TopologicalSortVisualization(navController)
        }
        composable("Prims"){
            PrimsAlgorithmVisualization(navController)
        }
        composable("Kruskal"){
            KruskalsAlgorithmVisualization(navController)
        }
        composable("Dijkstra"){
            DijkstraAlgorithmVisualization(navController)
        }
        composable("Gbfs"){
            GBFSGraphTraversal(navController)
        }
        composable("Ucs"){
            UCSGraphTraversal(navController)
        }
        composable("Astar"){
            AStarGraphTraversal(navController)
        }
        composable("Idastar"){
            IDAStarGraphTraversal(navController)
        }
        composable("avl"){
            AVLTreeBuilder(navController)
        }
        composable("Heap"){
            HeapVisualizer(navController)
        }

        composable("Chat") {
            ChatScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel() // Use viewModel() if not using Hilt
            )
        }

        composable("Problem") {
            AlgorithmRecommendationScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel() // Use viewModel() if not using Hilt
            )
        }

    })
}