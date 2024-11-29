package com.example.todo_list.features.main_screen.mvi

import com.example.todo_list.features.main_screen.model.TodoTask

sealed interface MainScreenEvent {
  data class TaskClicked(val index: Int) : MainScreenEvent
  data class TaskDeleted(val task: TodoTask) : MainScreenEvent
  data class TaskMoved(val fromIndex: Int, val toIndex: Int) : MainScreenEvent

  data object AddNewTaskFabClicked: MainScreenEvent
  data class NewTaskAdded(val newTaskName: String) : MainScreenEvent
  data object AddNewTaskBottomSheetDismissed : MainScreenEvent

  data class EditTaskSelected(val task: TodoTask) : MainScreenEvent
  data class TaskEdited(val updatedTask: TodoTask) : MainScreenEvent
  data object EditTaskBottomSheetDismissed : MainScreenEvent

  data object MenuClicked : MainScreenEvent
  data object MenuDismissed : MainScreenEvent
  data object AllTasksDeleted : MainScreenEvent
  data object ReorderTasksClicked : MainScreenEvent
  data object ReorderTasksCompleted : MainScreenEvent
  data object TasksShuffled : MainScreenEvent
}
