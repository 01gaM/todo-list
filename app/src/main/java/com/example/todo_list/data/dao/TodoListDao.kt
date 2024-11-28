package com.example.todo_list.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.todo_list.data.entities.TodoTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoListDao {
  @Query("SELECT * FROM todo_task")
  fun getAll(): Flow<List<TodoTaskEntity>>

  @Query("SELECT * FROM todo_task WHERE uid IN (:taskIds)")
  fun loadAllByIds(taskIds: IntArray): Flow<List<TodoTaskEntity>>

  @Query(
    "SELECT * FROM todo_task WHERE task_name LIKE :first AND " +
        "task_name LIKE :last LIMIT 1"
  )
  fun findByName(first: String, last: String): TodoTaskEntity

  @Insert
  fun insert(task: TodoTaskEntity)

  @Insert
  fun insertAll(vararg tasks: TodoTaskEntity)

  @Delete
  fun delete(task: TodoTaskEntity)

  @Query("DELETE FROM todo_task")
  fun deleteAll()

  @Query("UPDATE todo_task SET is_completed = :isCompleted WHERE uid = :taskId")
  fun updateTaskCompleted(taskId: Int, isCompleted: Boolean)

  @Query("UPDATE todo_task SET task_index = :index WHERE uid = :taskId")
  fun updateTaskIndex(taskId: Int, index: Int)

  @Query("UPDATE todo_task SET task_name = :name WHERE uid = :taskId")
  fun updateTaskName(taskId: Int, name: String)
}
