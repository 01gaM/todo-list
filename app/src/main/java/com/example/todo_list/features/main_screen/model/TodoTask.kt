package com.example.todo_list.features.main_screen.model

data class TodoTask(
  val id: Int,
  val name: String,
  val isCompleted: Boolean = false
)
