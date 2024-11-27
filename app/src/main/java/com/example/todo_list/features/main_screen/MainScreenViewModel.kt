package com.example.todo_list.features.main_screen

import androidx.lifecycle.ViewModel
import com.example.todo_list.features.main_screen.model.TodoTask
import com.example.todo_list.features.main_screen.mvi.MainScreenEvent
import com.example.todo_list.features.main_screen.mvi.MainScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class MainScreenViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(MainScreenState())
  val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

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
        _uiState.update {
          it.copy(taskList = emptyList())
        }
      }

      is MainScreenEvent.TaskDeleted -> {
        _uiState.update { currState ->
          currState.copy(taskList = currState.taskList.filterNot { it == event.task })
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
        _uiState.update { it.copy(taskList = it.taskList.shuffled()) }
      }
    }
  }

  // region private

  private fun handleNewTaskAdded(taskName: String) {
    _uiState.update {
      it.copy(
        taskList = it.taskList.toMutableList().apply {
          add(
            TodoTask(
              id = Random.nextInt(), // TODO: set item database id
              name = taskName
            )
          )
        }
      )
    }
  }

  private fun handleTaskClicked(index: Int) {
    _uiState.update {
      val updatedTask = with(it.taskList[index]) {
        copy(isCompleted = !isCompleted)
      }

      val newList = it.taskList.toMutableList()
      newList[index] = updatedTask

      it.copy(taskList = newList)
    }
  }

  private fun handleTaskEdited(task: TodoTask) {
    _uiState.update { currState ->
      val index = currState.taskList.indexOfFirst { it.id == task.id }
      currState.copy(
        taskList = currState.taskList.toMutableList().apply {
          this[index] = task
        }
      )
    }
  }

  private fun handleTaskMoved(fromIndex: Int, toIndex: Int) {
    _uiState.update {
      val newList = it.taskList.toMutableList()
      val item = newList.removeAt(fromIndex)
      newList.add(toIndex, item)
      it.copy(taskList = newList)
    }
  }

  // endregion
}
