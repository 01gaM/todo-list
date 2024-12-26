package com.example.todo_list.features.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_list.data.entities.TodoListEntity
import com.example.todo_list.data.repository.TodoListRepository
import com.example.todo_list.features.main_screen.model.TodoList
import com.example.todo_list.features.main_screen.mvi.MainScreenEvent
import com.example.todo_list.features.main_screen.mvi.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
  private val todoListRepository: TodoListRepository
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
      MainScreenEvent.AddNewListBottomSheetDismissed -> {
        _uiState.update { it.copy(showNewListBottomSheet = false) }
      }

      MainScreenEvent.AddNewListFabClicked -> {
        _uiState.update { it.copy(showNewListBottomSheet = true) }
      }

      is MainScreenEvent.NewTodoListAdded -> {
        viewModelScope.launch {
          withContext(Dispatchers.IO) {
            todoListRepository.insert(
              todoList = TodoListEntity(
                listName = event.newTodoListName
              )
            )
          }
        }
      }

      is MainScreenEvent.TodoListsDeleted -> {
        viewModelScope.launch {
          withContext(Dispatchers.IO) {
            val idList = uiState.value.todoLists.filter { it.isSelectedToDelete }.map { it.id }
            todoListRepository.deleteListsById(idList)
          }
          withContext(Dispatchers.Main) {
            _uiState.update { it.copy(isDeleteMode = false) }
          }
        }
      }

      is MainScreenEvent.DeleteModeEnabled -> {
        if (!event.isEnabled) {
          _uiState.update {
            it.copy(
              isDeleteMode = false,
              todoLists = it.todoLists.map { list -> list.copy(isSelectedToDelete = false) }
            )
          }
        } else {
          _uiState.update { it.copy(isDeleteMode = true) }
        }
      }

      is MainScreenEvent.TodoListSelectedToDelete -> {
        _uiState.update {
          it.copy(
            todoLists = it.todoLists.map { list ->
              if (list.id == event.todoList.id) {
                list.copy(isSelectedToDelete = !list.isSelectedToDelete)
              } else {
                list
              }
            }
          )
        }
      }
    }
  }

  // region private

  private suspend fun bindUiStateToData() {
    todoListRepository.allLists.collectLatest { todoList ->
      _uiState.update { state ->
        state.copy(
          isLoading = false,
          todoLists = todoList.map {
            TodoList(
              id = it.uid,
              name = it.listName,
            )
          }
        )
      }
    }
  }

  // endregion
}
