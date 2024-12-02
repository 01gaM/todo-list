package com.example.todo_list.features.todo_list_screen.mvi

import com.example.todo_list.features.todo_list_screen.model.TodoTask

sealed interface TodoListScreenEvent {
  data class TaskClicked(val index: Int) : TodoListScreenEvent
  data class TaskDeleted(val task: TodoTask) : TodoListScreenEvent
  data class TaskMoved(val fromIndex: Int, val toIndex: Int) : TodoListScreenEvent

  data object AddNewTaskFabClicked: TodoListScreenEvent
  data class NewTaskAdded(val newTaskName: String) : TodoListScreenEvent
  data object AddNewTaskBottomSheetDismissed : TodoListScreenEvent

  data class EditTaskSelected(val task: TodoTask) : TodoListScreenEvent
  data class TaskEdited(val updatedTask: TodoTask) : TodoListScreenEvent
  data object EditTaskBottomSheetDismissed : TodoListScreenEvent

  data object MenuClicked : TodoListScreenEvent
  data object MenuDismissed : TodoListScreenEvent
  data object AllTasksDeleted : TodoListScreenEvent
  data object ReorderTasksClicked : TodoListScreenEvent
  data object ReorderTasksCompleted : TodoListScreenEvent
  data object TasksShuffled : TodoListScreenEvent
  data object DeleteCompletedCheckedChanged : TodoListScreenEvent
}
