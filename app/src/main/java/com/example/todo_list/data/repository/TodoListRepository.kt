package com.example.todo_list.data.repository

import android.util.Log
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

  @WorkerThread
  suspend fun deleteTaskById(taskId: Int) {
    val task = todoListDao.findById(id = taskId)
    if (task != null) {
      todoListDao.delete(task)
      updateTasksIndexesAfterDeletion(deletedIndex = task.taskIndex)
    } else {
      Log.e(javaClass.simpleName, "Task with id \"$taskId\" not found")
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
  suspend fun getTaskAtIndex(index: Int): TodoTaskEntity? = todoListDao.findByIndex(index = index)

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun updateTaskCompleted(taskId: Int, isCompleted: Boolean) {
    todoListDao.updateTaskCompleted(taskId, isCompleted)
  }

  @WorkerThread
  suspend fun updateTasksIndexes(updatedList: List<TodoTask>) {
    val oldList = todoListDao.getAll().first()
    updatedList.forEachIndexed { index, todoTask ->
      val oldIndex = oldList.find { it.uid == todoTask.id }?.taskIndex
      if (oldIndex != index) {
        todoListDao.updateTaskIndex(taskId = todoTask.id, index = index)
      }
    }
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

  // region private

  @WorkerThread
  private suspend fun updateTasksIndexesAfterDeletion(deletedIndex: Int) {
    // Move all tasks indexes after the deleted task on one position
    todoListDao.getAll().first()
      .sortedBy { it.taskIndex }
      .drop(deletedIndex)
      .forEach {
        todoListDao.updateTaskIndex(
          taskId = it.uid,
          index = it.taskIndex - 1
        )
      }
  }

  // endregion

}
