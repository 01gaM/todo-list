package com.example.todo_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.todo_list.features.todo_list_screen.compose_views.TodoListScreenContent
import com.example.todo_list.common.ui.theme.ToDoListTheme
import com.example.todo_list.data.data_store.DataStoreManager
import com.example.todo_list.features.todo_list_screen.TodoListScreenViewModel
import com.example.todo_list.features.todo_list_screen.TodoListScreenViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: TodoListScreenViewModel by viewModels {
        TodoListScreenViewModelFactory(
          (application as TodoListApplication).repository,
          DataStoreManager(applicationContext)
        )
      }
      val state by viewModel.uiState.collectAsState()

      ToDoListTheme {
        TodoListScreenContent(
          state = state,
          onEvent = viewModel::handleEvent
        )
      }
    }
  }
}
