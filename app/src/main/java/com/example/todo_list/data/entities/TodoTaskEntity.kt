package com.example.todo_list.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_task")
data class TodoTaskEntity(
  @PrimaryKey(autoGenerate = true) val uid: Int = 0,
  @ColumnInfo(name = "task_index") val taskIndex: Int,
  @ColumnInfo(name = "task_name") val taskName: String,
  @ColumnInfo(name = "is_completed") val isCompleted: Boolean
)
