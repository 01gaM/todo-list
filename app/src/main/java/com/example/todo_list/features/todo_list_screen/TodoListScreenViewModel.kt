package com.example.todo_list.features.todo_list_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo_list.data.data_store.DataStoreManager
import com.example.todo_list.data.entities.TodoTaskEntity
import com.example.todo_list.data.repository.TodoTaskRepository
import com.example.todo_list.features.todo_list_screen.model.TodoTask
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenEvent
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenState
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
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class TodoListScreenViewModel(
  private val todoTaskRepository: TodoTaskRepository,
  private val dataStoreManager: DataStoreManager
) : ViewModel() {
  private val _uiState = MutableStateFlow(TodoListScreenState())
  val uiState: StateFlow<TodoListScreenState> = _uiState.asStateFlow()

  init {
    _uiState.update { it.copy(isLoading = true) }
    viewModelScope.launch {
      // fake delay to show loading animation for demo purposes
      delay(timeMillis = TimeUnit.SECONDS.toMillis(2))
      bindUiStateToData()
    }
  }

  fun handleEvent(event: TodoListScreenEvent) {
    when (event) {
      is TodoListScreenEvent.NewTaskAdded -> handleNewTaskAdded(event.newTaskName)

      is TodoListScreenEvent.AddNewTaskBottomSheetDismissed -> {
        _uiState.update { it.copy(showNewTaskBottomSheet = false) }
      }

      is TodoListScreenEvent.AddNewTaskFabClicked -> {
        _uiState.update { it.copy(showNewTaskBottomSheet = true) }
      }

      is TodoListScreenEvent.MenuClicked -> {
        _uiState.update { it.copy(displayMenu = !it.displayMenu) }
      }

      is TodoListScreenEvent.TaskClicked -> handleTaskClicked(event.index)

      is TodoListScreenEvent.AllTasksDeleted -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoTaskRepository.deleteAll()
        }
      }

      is TodoListScreenEvent.TaskDeleted -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoTaskRepository.deleteTaskById(event.task.id)
        }
      }

      is TodoListScreenEvent.MenuDismissed -> {
        _uiState.update { it.copy(displayMenu = false) }
      }

      is TodoListScreenEvent.TaskEdited -> handleTaskEdited(event.updatedTask)

      is TodoListScreenEvent.EditTaskBottomSheetDismissed -> {
        _uiState.update { it.copy(taskToEdit = null) }
      }

      is TodoListScreenEvent.EditTaskSelected -> {
        _uiState.update { it.copy(taskToEdit = event.task) }
      }

      is TodoListScreenEvent.TaskMoved -> handleTaskMoved(event.fromIndex, event.toIndex)

      is TodoListScreenEvent.ReorderTasksClicked -> {
        _uiState.update {
          it.copy(
            reorderingModeTaskList = it.taskList,
            isReorderingMode = true
          )
        }
      }

      is TodoListScreenEvent.ReorderTasksCompleted -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoTaskRepository.updateTasksIndexes(updatedList = uiState.value.reorderingModeTaskList)
          withContext(context = Dispatchers.Main) {
            _uiState.update { it.copy(isReorderingMode = false) }
          }
        }
      }

      is TodoListScreenEvent.TasksShuffled -> {
        CoroutineScope(Dispatchers.IO).launch {
          todoTaskRepository.shuffleIndexes()
        }
      }

      is TodoListScreenEvent.DeleteCompletedCheckedChanged -> {
        viewModelScope.launch {
          dataStoreManager.updateIsDeleteCompletedChecked(!uiState.value.isDeleteCompletedChecked)
        }
      }
    }
  }

  // region private

  private fun handleNewTaskAdded(taskName: String) {
    CoroutineScope(Dispatchers.IO).launch {
      todoTaskRepository.insert(
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
      todoTaskRepository.getTaskAtIndex(index)?.let {
        todoTaskRepository.updateTaskCompleted(taskId = it.uid, isCompleted = !it.isCompleted)
        if (uiState.value.isDeleteCompletedChecked && !it.isCompleted) {
          todoTaskRepository.deleteTaskById(it.uid)
        }
      }
    }
  }

  private fun handleTaskEdited(task: TodoTask) {
    CoroutineScope(Dispatchers.IO).launch {
      todoTaskRepository.updateTask(newTask = task)
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
    todoTaskRepository.allTasks.combine(
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

class TodoListScreenViewModelFactory(
  private val repository: TodoTaskRepository,
  private val dataStoreManager: DataStoreManager
) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TodoListScreenViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return TodoListScreenViewModel(repository, dataStoreManager) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}