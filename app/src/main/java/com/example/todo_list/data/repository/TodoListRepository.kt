package com.example.todo_list.data.repository

import androidx.annotation.WorkerThread
import com.example.todo_list.data.dao.TodoListDao
import com.example.todo_list.data.entities.TodoTaskEntity
import kotlinx.coroutines.flow.Flow


class TodoListRepository(private val todoListDao: TodoListDao) {

  val allTasks: Flow<List<TodoTaskEntity>> = todoListDao.getAll()

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun insert(todoTask: TodoTaskEntity) {
    todoListDao.insert(todoTask)
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun delete(todoTask: TodoTaskEntity) {
    todoListDao.delete(todoTask)
  }
}
