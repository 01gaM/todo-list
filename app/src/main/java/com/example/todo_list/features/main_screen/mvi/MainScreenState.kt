package com.example.todo_list.features.main_screen.mvi

import com.example.todo_list.features.main_screen.model.TodoTask

data class MainScreenState(
  val taskList: List<TodoTask> = emptyList(),
  val reorderingModeTaskList: List<TodoTask> = emptyList(),
  val isReorderingMode: Boolean = false,
  val showNewTaskBottomSheet: Boolean = false,
  val taskToEdit: TodoTask? = null,
  val displayMenu: Boolean = false,
  val isLoading: Boolean = false
)
