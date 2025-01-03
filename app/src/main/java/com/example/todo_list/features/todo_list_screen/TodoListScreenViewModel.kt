package com.example.todo_list.features.todo_list_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_list.data.data_store.DataStoreManager
import com.example.todo_list.data.entities.TodoTaskEntity
import com.example.todo_list.data.repository.TodoTaskRepository
import com.example.todo_list.features.todo_list_screen.model.TodoTask
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenEvent
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = TodoListScreenViewModel.TodoListScreenViewModelFactory::class)
class TodoListScreenViewModel @AssistedInject constructor(
  private val todoTaskRepository: TodoTaskRepository,
  private val dataStoreManager: DataStoreManager,
  @Assisted private val listId: Int
) : ViewModel() {
  private val _uiState = MutableStateFlow(TodoListScreenState())
  val uiState: StateFlow<TodoListScreenState> = _uiState.asStateFlow()

  @AssistedFactory
  interface TodoListScreenViewModelFactory {
    fun create(listId: Int): TodoListScreenViewModel
  }

  init {
    _uiState.update { it.copy(isLoading = true) }
    viewModelScope.launch {
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

      is TodoListScreenEvent.TaskClicked -> handleTaskClicked(event.taskId)

      is TodoListScreenEvent.AllTasksDeleted -> {
        viewModelScope.launch {
          withContext(Dispatchers.IO) {
            todoTaskRepository.deleteAll()
          }
        }
      }

      is TodoListScreenEvent.TaskDeleted -> {
        viewModelScope.launch {
          withContext(Dispatchers.IO) {
            todoTaskRepository.deleteTaskById(event.task.id)
          }
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
        viewModelScope.launch {
          withContext(Dispatchers.IO) {
            todoTaskRepository.updateTasksIndexes(
              listId = listId,
              updatedList = uiState.value.reorderingModeTaskList
            )
            withContext(context = Dispatchers.Main) {
              _uiState.update { it.copy(isReorderingMode = false) }
            }
          }
        }
      }

      is TodoListScreenEvent.TasksShuffled -> {
        viewModelScope.launch {
          withContext(Dispatchers.IO) {
            todoTaskRepository.shuffleIndexes(listId = listId)
          }
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
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        todoTaskRepository.insert(
          TodoTaskEntity(
            taskIndex = uiState.value.taskList.size,
            taskName = taskName,
            isCompleted = false,
            listId = listId
          )
        )
      }
    }
  }

  private fun handleTaskClicked(taskId: Int) {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        todoTaskRepository.getTaskById(taskId)?.let {
          todoTaskRepository.updateTaskCompleted(taskId = it.uid, isCompleted = !it.isCompleted)
          if (uiState.value.isDeleteCompletedChecked && !it.isCompleted) {
            todoTaskRepository.deleteTaskById(it.uid)
          }
        }
      }
    }
  }

  private fun handleTaskEdited(task: TodoTask) {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        todoTaskRepository.updateTask(newTask = task)
      }
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
    todoTaskRepository.getTasksByListId(listId).combine(
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
