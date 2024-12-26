package com.example.todo_list.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.todo_list.data.dao.TodoListDao
import com.example.todo_list.data.entities.TodoListEntity
import kotlinx.coroutines.flow.Flow


class TodoListRepository(private val todoListDao: TodoListDao) {

  val allLists: Flow<List<TodoListEntity>> = todoListDao.getAll()

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun insert(todoList: TodoListEntity) {
    todoListDao.insert(todoList)
  }

  @WorkerThread
  fun deleteListsById(idList: List<Int>) = todoListDao.deleteAllById(idList)

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun deleteAll() = todoListDao.deleteAll()

  @Suppress("RedundantSuspendModifier")
  @WorkerThread
  suspend fun getListById(id: Int): TodoListEntity? = todoListDao.findById(id = id)

  @WorkerThread
  fun updateListName(listId: Int, newName: String) {
    val list = todoListDao.findById(id = listId)
    if (list != null) {
      todoListDao.updateListName(listId = listId, name = newName)
    } else {
      Log.e(javaClass.simpleName, "Task with id \"$listId\" not found")
    }
  }
}
