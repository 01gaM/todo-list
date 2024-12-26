package com.example.todo_list.features.main_screen.mvi

import com.example.todo_list.features.main_screen.model.TodoList

data class MainScreenState(
  val todoLists: List<TodoList> = emptyList(),
  val isLoading: Boolean = false,
  val showNewListBottomSheet: Boolean = false,
  val listToEdit: TodoList? = null,
  val displayMenu: Boolean = false,
  val isDeleteMode: Boolean = false
)
