package com.example.todo_list.navigation

sealed class Screen(val route: String) {
  data object Main : Screen("main")
  data object TodoList : Screen("todoList")
}
