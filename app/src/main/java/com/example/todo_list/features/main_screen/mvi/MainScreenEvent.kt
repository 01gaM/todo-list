package com.example.todo_list.features.main_screen.mvi

import com.example.todo_list.features.main_screen.model.TodoList

sealed interface MainScreenEvent {
  data object AddNewListFabClicked : MainScreenEvent
  data class TodoListDeleted(val todoList: TodoList) : MainScreenEvent
  data class NewTodoListAdded(val newTodoListName: String) : MainScreenEvent
  data object AddNewListBottomSheetDismissed : MainScreenEvent
}
