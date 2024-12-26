package com.example.todo_list.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_list")
data class TodoListEntity(
  @PrimaryKey(autoGenerate = true) val uid: Int = 0,
  @ColumnInfo(name = "list_name") val listName: String
)
