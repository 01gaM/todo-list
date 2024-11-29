package com.example.todo_list

import android.app.Application
import com.example.todo_list.data.TodoListDatabase
import com.example.todo_list.data.repository.TodoListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TodoListApplication: Application() {
  val applicationScope = CoroutineScope(SupervisorJob())

  val database by lazy { TodoListDatabase.getDatabase(this, applicationScope) }
  val repository by lazy { TodoListRepository(database.todoTaskDao()) }
}
