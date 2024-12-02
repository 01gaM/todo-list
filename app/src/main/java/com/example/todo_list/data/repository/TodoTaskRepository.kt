package com.example.todo_list.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.todo_list.data.dao.TodoTaskDao
import com.example.todo_list.data.entities.TodoTaskEntity
import com.example.todo_list.features.todo_list_screen.model.TodoTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class TodoTaskRepository(private val todoTaskDao: TodoTaskDao) {

  val allTasks: Flow<List<TodoTaskEntity>> = todoTaskDao.getAll()

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun insert(todoTask: TodoTaskEntity) {
    todoTaskDao.insert(todoTask)
  }

  @WorkerThread
  suspend fun deleteTaskById(taskId: Int) {
    val task = todoTaskDao.findById(id = taskId)
    if (task != null) {
      todoTaskDao.delete(task)
      updateTasksIndexesAfterDeletion(deletedIndex = task.taskIndex)
    } else {
      Log.e(javaClass.simpleName, "Task with id \"$taskId\" not found")
    }
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun deleteAll() = todoTaskDao.deleteAll()

  @WorkerThread
  suspend fun shuffleIndexes() {
    val tasks = todoTaskDao.getAll().first()
    val shuffledTasksIndexes = tasks.shuffled().map { it.taskIndex }
    for (index in tasks.indices) {
      todoTaskDao.updateTaskIndex(
        taskId = tasks[index].uid,
        index = shuffledTasksIndexes[index]
      )
    }
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun getTaskAtIndex(index: Int): TodoTaskEntity? = todoTaskDao.findByIndex(index = index)

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun updateTaskCompleted(taskId: Int, isCompleted: Boolean) {
    todoTaskDao.updateTaskCompleted(taskId, isCompleted)
  }

  @WorkerThread
  suspend fun updateTasksIndexes(updatedList: List<TodoTask>) {
    val oldList = todoTaskDao.getAll().first()
    updatedList.forEachIndexed { index, todoTask ->
      val oldIndex = oldList.find { it.uid == todoTask.id }?.taskIndex
      if (oldIndex != index) {
        todoTaskDao.updateTaskIndex(taskId = todoTask.id, index = index)
      }
    }
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun updateTask(newTask: TodoTask) {
    with(newTask) {
      todoTaskDao.updateTask(
        taskId = id,
        name = name,
        isCompleted = isCompleted
      )
    }
  }

  // region private

  @WorkerThread
  private suspend fun updateTasksIndexesAfterDeletion(deletedIndex: Int) {
    // Move all tasks indexes after the deleted task on one position
    todoTaskDao.getAll().first()
      .sortedBy { it.taskIndex }
      .drop(deletedIndex)
      .forEach {
        todoTaskDao.updateTaskIndex(
          taskId = it.uid,
          index = it.taskIndex - 1
        )
      }
  }

  // endregion

}
