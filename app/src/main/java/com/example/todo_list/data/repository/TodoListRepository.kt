package com.example.todo_list.data.repository

import androidx.annotation.WorkerThread
import com.example.todo_list.data.dao.TodoListDao
import com.example.todo_list.data.entities.TodoTaskEntity
import com.example.todo_list.features.main_screen.model.TodoTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class TodoListRepository(private val todoListDao: TodoListDao) {

  val allTasks: Flow<List<TodoTaskEntity>> = todoListDao.getAll()

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun insert(todoTask: TodoTaskEntity) {
    todoListDao.insert(todoTask)
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun deleteTaskById(taskId: Int) {
    todoListDao.findById(id = taskId).also {
      todoListDao.delete(it)
    }
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun deleteAll() = todoListDao.deleteAll()

  @WorkerThread
  suspend fun shuffleIndexes() {
    val tasks = todoListDao.getAll().first()
    val shuffledTasksIndexes = tasks.shuffled().map { it.taskIndex }
    for (index in tasks.indices) {
      todoListDao.updateTaskIndex(
        taskId = tasks[index].uid,
        index = shuffledTasksIndexes[index]
      )
    }
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun getTaskAtIndex(index: Int): TodoTaskEntity = todoListDao.findByIndex(index = index)

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun updateTaskCompleted(taskId: Int, isCompleted: Boolean) {
    todoListDao.updateTaskCompleted(taskId, isCompleted)
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun updateTaskIndex(fromIndex: Int, toIndex: Int) {
    todoListDao.updateTaskIndex(fromIndex, toIndex)
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun updateTask(newTask: TodoTask) {
    with(newTask) {
      todoListDao.updateTask(
        taskId = id,
        name = name,
        isCompleted = isCompleted
      )
    }
  }
}
