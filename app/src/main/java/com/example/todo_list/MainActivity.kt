package com.example.todo_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.todo_list.features.main_screen.compose_views.MainScreenContent
import com.example.todo_list.common.ui.theme.ToDoListTheme
import com.example.todo_list.features.main_screen.MainScreenViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel by viewModels<MainScreenViewModel>()
      val state by viewModel.uiState.collectAsState()

      ToDoListTheme {
        MainScreenContent(
          state = state,
          onEvent = viewModel::handleEvent
        )
      }
    }
  }
}
