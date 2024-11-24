package com.example.todo_list.ui.main_screen.model

data class TodoTask(
  val id: Int,
  val name: String,
  val isCompleted: Boolean = false
)
