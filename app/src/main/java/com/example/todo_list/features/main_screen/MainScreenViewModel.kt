package com.example.todo_list.features.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo_list.data.entities.TodoTaskEntity
import com.example.todo_list.data.repository.TodoListRepository
import com.example.todo_list.features.main_screen.model.TodoTask
import com.example.todo_list.features.main_screen.mvi.MainScreenEvent
import com.example.todo_list.features.main_screen.mvi.MainScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(private val todoListRepository: TodoListRepository) : ViewModel() {
  private val _uiState = MutableStateFlow(MainScreenState())
  val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

  init { viewModelScope.launch { bindUiStateToDatabase() } }

  fun handleEvent(event: MainScreenEvent) {
    when (event) {
      is MainScreenEvent.NewTaskAdded -> handleNewTaskAdded(event.newTaskName)

      is MainScreenEvent.AddNewTaskBottomSheetDismissed -> {
        _uiState.update { it.copy(showNewTaskBottomSheet = false) }
      }

      is MainScreenEvent.AddNewTaskFabClicked -> {
        _uiState.update { it.copy(showNewTaskBottomSheet = true) }
      }

      is MainScreenEvent.MenuClicked -> {
        _uiState.update { it.copy(displayMenu = !it.displayMenu) }
      }

      is MainScreenEvent.TaskClicked -> handleTaskClicked(event.index)

      is MainScreenEvent.AllTasksDeleted -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoListRepository.deleteAll()
        }
      }

      is MainScreenEvent.TaskDeleted -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoListRepository.deleteTaskById(event.task.id)
        }
      }

      is MainScreenEvent.MenuDismissed -> {
        _uiState.update { it.copy(displayMenu = false) }
      }

      is MainScreenEvent.TaskEdited -> handleTaskEdited(event.updatedTask)

      is MainScreenEvent.EditTaskBottomSheetDismissed -> {
        _uiState.update { it.copy(taskToEdit = null) }
      }

      is MainScreenEvent.EditTaskSelected -> {
        _uiState.update { it.copy(taskToEdit = event.task) }
      }

      is MainScreenEvent.TaskMoved -> handleTaskMoved(event.fromIndex, event.toIndex)

      is MainScreenEvent.ReorderTasksClicked -> {
        _uiState.update { it.copy(isReorderingMode = true) }
      }

      is MainScreenEvent.ReorderTasksCompleted -> {
        _uiState.update { it.copy(isReorderingMode = false) }
      }

      is MainScreenEvent.TasksShuffled -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoListRepository.shuffleIndexes()
        }
      }
    }
  }

  // region private

  private fun handleNewTaskAdded(taskName: String) {
    CoroutineScope(Dispatchers.IO).launch {
      todoListRepository.insert(
        TodoTaskEntity(
          taskIndex = uiState.value.taskList.size,
          taskName = taskName,
          isCompleted = false
        )
      )
    }
  }

  private fun handleTaskClicked(index: Int) {
    CoroutineScope(Dispatchers.IO).launch {
      with(todoListRepository.getTaskAtIndex(index)) {
        todoListRepository.updateTaskCompleted(taskId = uid, isCompleted = !isCompleted)
      }
    }
  }

  private fun handleTaskEdited(task: TodoTask) {
    CoroutineScope(Dispatchers.IO).launch {
      todoListRepository.updateTask(newTask = task)
    }
  }

  private fun handleTaskMoved(fromIndex: Int, toIndex: Int) {
    CoroutineScope(Dispatchers.IO).launch {
      todoListRepository.updateTaskIndex(fromIndex = fromIndex, toIndex = toIndex)
    }
  }

  private suspend fun bindUiStateToDatabase() {
    todoListRepository.allTasks.collectLatest { taskList ->
      _uiState.update { currState ->
        currState.copy(
          taskList = taskList.sortedBy { it.taskIndex }.map { task ->
            TodoTask(
              id = task.uid,
              name = task.taskName,
              isCompleted = task.isCompleted
            )
          }
        )
      }
    }
  }

  // endregion
}

class MainScreenViewModelFactory(private val repository: TodoListRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return MainScreenViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
