package com.example.todo_list.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.todo_list.data.dao.TodoTaskDao
import com.example.todo_list.data.entities.TodoTaskEntity
import com.example.todo_list.features.todo_list_screen.model.TodoTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class TodoTaskRepository(private val todoTaskDao: TodoTaskDao) {
  @WorkerThread
  fun getTasksByListId(listId: Int): Flow<List<TodoTaskEntity>> = todoTaskDao.findByListId(listId)

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun insert(todoTask: TodoTaskEntity) {
    todoTaskDao.insert(todoTask)
  }

  @WorkerThread
  suspend fun deleteTaskById(taskId: Int) {
    val task = todoTaskDao.findByTaskId(id = taskId)
    if (task != null) {
      todoTaskDao.delete(task)
      updateTasksIndexesAfterDeletion(
        listId = task.listId,
        deletedIndex = task.taskIndex
      )
    } else {
      Log.e(javaClass.simpleName, "Task with id \"$taskId\" not found")
    }
  }

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun deleteAll() = todoTaskDao.deleteAll()

  @WorkerThread
  suspend fun shuffleIndexes(listId: Int) {
    val tasks = todoTaskDao.findByListId(listId).first()
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
  suspend fun getTaskById(id: Int): TodoTaskEntity? = todoTaskDao.findByTaskId(id = id)

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun updateTaskCompleted(taskId: Int, isCompleted: Boolean) {
    todoTaskDao.updateTaskCompleted(taskId, isCompleted)
  }

  @WorkerThread
  suspend fun updateTasksIndexes(listId: Int, updatedList: List<TodoTask>) {
    val oldList = todoTaskDao.findByListId(listId).first()
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
  private suspend fun updateTasksIndexesAfterDeletion(listId: Int, deletedIndex: Int) {
    // Move all tasks indexes after the deleted task on one position
    todoTaskDao.findByListId(listId).first()
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
