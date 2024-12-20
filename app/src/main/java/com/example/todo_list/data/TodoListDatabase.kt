package com.example.todo_list.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todo_list.data.dao.TodoListDao
import com.example.todo_list.data.entities.TodoTaskEntity
import kotlinx.coroutines.CoroutineScope

@Database(entities = [TodoTaskEntity::class], version = 1)
abstract class TodoListDatabase : RoomDatabase() {
  abstract fun todoTaskDao(): TodoListDao

  companion object {
    // Singleton prevents multiple instances of database opening at the
    // same time.
    @Volatile
    private var INSTANCE: TodoListDatabase? = null

    fun getDatabase(
      context: Context,
      scope: CoroutineScope
    ): TodoListDatabase {
      // if the INSTANCE is not null, then return it,
      // if it is, then create the database
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TodoListDatabase::class.java,
          "word_database"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
