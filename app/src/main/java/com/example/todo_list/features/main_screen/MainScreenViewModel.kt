package com.example.todo_list.features.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo_list.data.data_store.DataStoreManager
import com.example.todo_list.data.entities.TodoTaskEntity
import com.example.todo_list.data.repository.TodoListRepository
import com.example.todo_list.features.main_screen.model.TodoTask
import com.example.todo_list.features.main_screen.mvi.MainScreenEvent
import com.example.todo_list.features.main_screen.mvi.MainScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainScreenViewModel(
  private val todoListRepository: TodoListRepository,
  private val dataStoreManager: DataStoreManager
) : ViewModel() {
  private val _uiState = MutableStateFlow(MainScreenState())
  val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

  init {
    _uiState.update { it.copy(isLoading = true) }
    viewModelScope.launch {
      // fake delay to show loading animation for demo purposes
      delay(timeMillis = TimeUnit.SECONDS.toMillis(2))
      bindUiStateToData()
    }
  }

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
        _uiState.update {
          it.copy(
            reorderingModeTaskList = it.taskList,
            isReorderingMode = true
          )
        }
      }

      is MainScreenEvent.ReorderTasksCompleted -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoListRepository.updateTasksIndexes(updatedList = uiState.value.reorderingModeTaskList)
          _uiState.update { it.copy(isReorderingMode = false) }
        }
      }

      is MainScreenEvent.TasksShuffled -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoListRepository.shuffleIndexes()
        }
      }

      is MainScreenEvent.DeleteCompletedCheckedChanged -> {
        viewModelScope.launch {
          dataStoreManager.updateIsDeleteCompletedChecked(!uiState.value.isDeleteCompletedChecked)
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
      todoListRepository.getTaskAtIndex(index)?.let {
        todoListRepository.updateTaskCompleted(taskId = it.uid, isCompleted = !it.isCompleted)
        if (uiState.value.isDeleteCompletedChecked && !it.isCompleted) {
          todoListRepository.deleteTaskById(it.uid)
        }
      }
    }
  }

  private fun handleTaskEdited(task: TodoTask) {
    CoroutineScope(Dispatchers.IO).launch {
      todoListRepository.updateTask(newTask = task)
    }
  }

  private fun handleTaskMoved(fromIndex: Int, toIndex: Int) {
    _uiState.update {
      val newList = it.reorderingModeTaskList.toMutableList()
      val item = newList.removeAt(fromIndex)
      newList.add(toIndex, item)
      it.copy(reorderingModeTaskList = newList)
    }
  }

  private suspend fun bindUiStateToData() {
    todoListRepository.allTasks.combine(
      dataStoreManager.isDeleteCompletedCheckedFlow.distinctUntilChanged(),
      transform = { tasks, isDeleteCompletedChecked ->
        uiState.value.copy(
          isLoading = false,
          isDeleteCompletedChecked = isDeleteCompletedChecked,
          taskList = tasks.sortedBy { it.taskIndex }.map { task ->
            TodoTask(
              id = task.uid,
              name = task.taskName,
              isCompleted = task.isCompleted
            )
          }
        )
      }
    ).collectLatest { newState -> _uiState.update { newState } }
  }

  // endregion
}

class MainScreenViewModelFactory(
  private val repository: TodoListRepository,
  private val dataStoreManager: DataStoreManager
) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return MainScreenViewModel(repository, dataStoreManager) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
