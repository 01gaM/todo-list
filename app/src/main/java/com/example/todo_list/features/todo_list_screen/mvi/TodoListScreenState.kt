package com.example.todo_list.features.todo_list_screen.mvi

import com.example.todo_list.features.todo_list_screen.model.TodoTask

data class TodoListScreenState(
  val taskList: List<TodoTask> = emptyList(),
  val reorderingModeTaskList: List<TodoTask> = emptyList(),
  val isReorderingMode: Boolean = false,
  val showNewTaskBottomSheet: Boolean = false,
  val taskToEdit: TodoTask? = null,
  val displayMenu: Boolean = false,
  val isDeleteCompletedChecked: Boolean = false,
  val isLoading: Boolean = false
)
