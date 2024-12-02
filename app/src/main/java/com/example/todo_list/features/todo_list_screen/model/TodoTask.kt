package com.example.todo_list.features.todo_list_screen.model

data class TodoTask(
  val id: Int,
  val name: String,
  val isCompleted: Boolean = false
)
