package com.example.todo_list.di

import android.content.Context
import com.example.todo_list.data.TodoListDatabase
import com.example.todo_list.data.data_store.DataStoreManager
import com.example.todo_list.data.repository.TodoListRepository
import com.example.todo_list.data.repository.TodoTaskRepository
import com.example.todo_list.features.todo_list_screen.TodoListScreenViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
  @Provides
  fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
    DataStoreManager(context)

  @Provides
  fun provideDatabase(@ApplicationContext context: Context): TodoListDatabase {
    return TodoListDatabase.getDatabase(context, CoroutineScope(SupervisorJob()))
  }

  @Provides
  fun provideTodoTaskRepository(database: TodoListDatabase): TodoTaskRepository {
    return TodoTaskRepository(todoTaskDao = database.todoTaskDao())
  }

  @Provides
  fun provideTodoListRepository(database: TodoListDatabase): TodoListRepository {
    return TodoListRepository(todoListDao = database.todoListDao())
  }

  @Provides
  fun provideTodoListScreenViewModel(
    todoTaskRepository: TodoTaskRepository,
    dataStoreManager: DataStoreManager
  ): TodoListScreenViewModel {
    return TodoListScreenViewModel(
      todoTaskRepository = todoTaskRepository,
      dataStoreManager = dataStoreManager
    )
  }
}
