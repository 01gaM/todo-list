package com.example.todo_list.features.main_screen.model

import com.example.todo_list.features.todo_list_screen.model.TodoTask

data class TodoList(
  val id: Int,
  val name: String,
  val tasks: List<TodoTask> = emptyList(),
  val isSelectedToDelete: Boolean = false
)
