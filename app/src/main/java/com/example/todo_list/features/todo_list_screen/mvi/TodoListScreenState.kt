package com.example.todo_list.features.todo_list_screen.mvi

import com.example.todo_list.features.todo_list_screen.model.TodoTask

data class TodoListScreenState(
  val taskListName: String = "",
  val taskList: List<TodoTask> = emptyList(),
  val showNewTaskBottomSheet: Boolean = false,
  val taskToEdit: TodoTask? = null,
  val displayMenu: Boolean = false,
  val isDeleteCompletedChecked: Boolean = false,
  val contentMode: TodoListScreenMode = TodoListScreenMode.ViewList
)

sealed class TodoListScreenMode {
  data object EmptyList: TodoListScreenMode()
  data object ViewList: TodoListScreenMode()
  data object Loading: TodoListScreenMode()
  data class Reordering(
    val reorderingModeTaskList: List<TodoTask> = emptyList()
  ): TodoListScreenMode()
}
