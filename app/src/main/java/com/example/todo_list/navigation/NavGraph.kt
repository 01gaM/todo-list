package com.example.todo_list.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todo_list.features.main_screen.MainScreenViewModel
import com.example.todo_list.features.main_screen.compose_views.MainScreenContent
import com.example.todo_list.features.todo_list_screen.TodoListScreenViewModel
import com.example.todo_list.features.todo_list_screen.compose_views.TodoListScreenContent

@Composable
fun NavGraph(navController: NavHostController) {
  val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
  val mainScreenState by mainScreenViewModel.uiState.collectAsState()

  val todoListViewModel: TodoListScreenViewModel = hiltViewModel()
  val todoListScreenState by todoListViewModel.uiState.collectAsState()

  NavHost(
    navController = navController,
    startDestination = Screen.Main.route
  ) {
    composable(route = Screen.Main.route) {
      MainScreenContent(
        navController = navController,
        state = mainScreenState,
        onEvent = mainScreenViewModel::handleEvent
      )
    }
    composable(route = Screen.TodoList.route) {
      TodoListScreenContent(
        state = todoListScreenState,
        onEvent = todoListViewModel::handleEvent
      )
    }
  }
}
