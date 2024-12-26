package com.example.todo_list.data.dao

import com.example.todo_list.data.entities.TodoListEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoListDao {
  @Query("SELECT * FROM todo_list")
  fun getAll(): Flow<List<TodoListEntity>>

  @Query("SELECT * FROM todo_list WHERE uid LIKE :id LIMIT 1")
  fun findById(id: Int): TodoListEntity?

  @Insert
  fun insert(task: TodoListEntity)

  @Delete
  fun delete(task: TodoListEntity)

  @Query("DELETE FROM todo_list")
  fun deleteAll()

  @Query("DELETE FROM todo_list WHERE uid IN (:idList)")
  fun deleteAllById(idList: List<Int>)

  @Query("UPDATE todo_list SET list_name = :name WHERE uid = :listId")
  fun updateListName(listId: Int, name: String)
}
