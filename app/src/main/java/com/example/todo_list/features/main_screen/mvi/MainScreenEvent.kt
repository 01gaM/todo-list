package com.example.todo_list.features.main_screen.mvi

import com.example.todo_list.features.main_screen.model.TodoList

sealed interface MainScreenEvent {
  data object AddNewListFabClicked : MainScreenEvent
  data class NewTodoListAdded(val newTodoListName: String) : MainScreenEvent
  data object AddNewListBottomSheetDismissed : MainScreenEvent
  data object TodoListsDeleted : MainScreenEvent
  data class DeleteModeEnabled(val isEnabled: Boolean) : MainScreenEvent
  data class TodoListSelectedToDelete(val todoList: TodoList) : MainScreenEvent
}
