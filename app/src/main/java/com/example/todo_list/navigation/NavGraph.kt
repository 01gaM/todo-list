package com.example.todo_list.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todo_list.features.main_screen.MainScreenViewModel
import com.example.todo_list.features.main_screen.compose_views.MainScreenContent
import com.example.todo_list.features.todo_list_screen.TodoListScreenViewModel
import com.example.todo_list.features.todo_list_screen.compose_views.TodoListScreenContent

@Composable
fun NavGraph(navController: NavHostController) {
  NavHost(
    navController = navController,
    startDestination = Screen.Main.route
  ) {
    composable(route = Screen.Main.route) {
      val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
      val mainScreenState by mainScreenViewModel.uiState.collectAsState()

      MainScreenContent(
        navController = navController,
        state = mainScreenState,
        onEvent = mainScreenViewModel::handleEvent
      )
    }
    composable(
      route = "${Screen.TodoList.route}/{listId}",
      arguments = listOf(
        navArgument("listId") {
          defaultValue = 0
          type = NavType.IntType
        }
      )
    ) { backStackEntry ->
      backStackEntry.arguments?.getInt("listId")?.let { listId ->
        val todoListViewModel: TodoListScreenViewModel = hiltViewModel(
          creationCallback = { factory: TodoListScreenViewModel.TodoListScreenViewModelFactory ->
            factory.create(listId)
          }
        )
        val todoListScreenState by todoListViewModel.uiState.collectAsState()
        TodoListScreenContent(
          navController = navController,
          state = todoListScreenState,
          onEvent = todoListViewModel::handleEvent
        )
      }
    }
  }
}
