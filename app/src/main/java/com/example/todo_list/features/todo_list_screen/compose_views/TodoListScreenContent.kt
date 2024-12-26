package com.example.todo_list.features.todo_list_screen.compose_views

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.R
import com.example.todo_list.common.ui.compose_views.NewItemBottomSheet
import com.example.todo_list.common.ui.compose_views.SwipeActionContainer
import com.example.todo_list.features.todo_list_screen.model.TodoTask
import com.example.todo_list.common.ui.theme.ToDoListTheme
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenEvent
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreenContent(
  modifier: Modifier = Modifier,
  state: TodoListScreenState,
  onEvent: (TodoListScreenEvent) -> Unit = {}
) {
  val isTaskListEmpty = remember(state.taskList) { state.taskList.isEmpty() }
  val lazyListState = rememberLazyListState()
  val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
    onEvent(TodoListScreenEvent.TaskMoved(from.index, to.index))
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background)
      .navigationBarsPadding(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.app_name),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
          )
        },
        colors = TopAppBarDefaults.topAppBarColors()
          .copy(containerColor = MaterialTheme.colorScheme.primary),
        actions = {
          AnimatedVisibility(
            visible = !state.isReorderingMode,
            enter = expandIn(),
            exit = shrinkOut()
          ) {
            IconButton(
              onClick = { onEvent(TodoListScreenEvent.MenuClicked) },
              content = {
                Icon(
                  imageVector = Icons.Default.MoreVert,
                  contentDescription = "More icon",
                  tint = MaterialTheme.colorScheme.onPrimary
                )
              }
            )
          }

          TodoListScreenMenu(
            isVisible = state.displayMenu,
            isDeleteCompletedChecked = state.isDeleteCompletedChecked,
            onDismiss = { onEvent(TodoListScreenEvent.MenuDismissed) },
            onDeleteAllClick = {
              onEvent(TodoListScreenEvent.MenuDismissed)
              onEvent(TodoListScreenEvent.AllTasksDeleted)
            },
            onReorderTasksClick = {
              onEvent(TodoListScreenEvent.MenuDismissed)
              onEvent(TodoListScreenEvent.ReorderTasksClicked)
            },
            onShuffleListClick = {
              onEvent(TodoListScreenEvent.MenuDismissed)
              onEvent(TodoListScreenEvent.TasksShuffled)
            },
            onDeleteCompletedChanged = {
              onEvent(TodoListScreenEvent.DeleteCompletedCheckedChanged)
            }
          )
        }
      )
    },
    bottomBar = {
      AnimatedVisibility(
        visible = state.isReorderingMode,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        BottomAppBar {
          Button(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            onClick = { onEvent(TodoListScreenEvent.ReorderTasksCompleted) },
            content = {
              Text(text = stringResource(R.string.save).uppercase())
            }
          )
        }
      }
    },
    floatingActionButton = {
      AnimatedVisibility(
        visible = !state.isReorderingMode,
        enter = fadeIn(animationSpec = tween(durationMillis = 2000)),
        exit = fadeOut()
      ) {
        FloatingActionButton(
          shape = CircleShape,
          content = {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add task icon"
            )
          },
          onClick = { onEvent(TodoListScreenEvent.AddNewTaskFabClicked) }
        )
      }
    }
  ) { innerPadding ->
    if (state.isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.Center
      ) { CircularProgressIndicator() }
    } else {
      Crossfade(
        targetState = isTaskListEmpty,
        label = "cross_fade_empty_list"
      ) { isListEmpty ->
        if (isListEmpty) {
          EmptyTodoListContent(
            modifier = Modifier
              .padding(paddingValues = innerPadding)
              .fillMaxSize(),
            title = stringResource(R.string.todo_list_screen_empty_list_title),
            description = stringResource(R.string.todo_list_screen_empty_list_description)
          )
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = innerPadding
          ) {
            if (state.isReorderingMode) {
              itemsIndexed(
                items = state.reorderingModeTaskList,
                key = { _, task -> task.id.toString() + task.name }
              ) { index, item ->
                ReorderableItem(
                  state = reorderableLazyListState,
                  key = item.id.toString() + item.name
                ) { _ ->
                  TodoListItem(
                    modifier = Modifier.draggableHandle(),
                    taskIndex = index + 1,
                    taskName = item.name,
                    isCompleted = item.isCompleted,
                    isReorderingMode = true,
                    onClick = { onEvent(TodoListScreenEvent.TaskClicked(index)) }
                  )
                }
              }
            } else {
              itemsIndexed(
                items = state.taskList,
                key = { _, task -> task.id.toString() + task.name }
              ) { index, item ->
                SwipeActionContainer(
                  modifier = Modifier.animateItem(),
                  item = item,
                  onDelete = { onEvent(TodoListScreenEvent.TaskDeleted(item)) },
                  onEdit = { onEvent(TodoListScreenEvent.EditTaskSelected(item)) }
                ) {
                  TodoListItem(
                    taskIndex = index + 1,
                    taskName = item.name,
                    isCompleted = item.isCompleted,
                    isReorderingMode = false,
                    onClick = { onEvent(TodoListScreenEvent.TaskClicked(index)) }
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  NewItemBottomSheet(
    visible = state.showNewTaskBottomSheet,
    title = stringResource(R.string.new_task_bottom_sheet_title),
    onDismiss = { onEvent(TodoListScreenEvent.AddNewTaskBottomSheetDismissed) },
    onSaveItem = { newItemName ->
      onEvent(TodoListScreenEvent.NewTaskAdded(newItemName))
      onEvent(TodoListScreenEvent.AddNewTaskBottomSheetDismissed)
    }
  )

  state.taskToEdit?.let {
    EditTaskBottomSheet(
      visible = true,
      task = it,
      onDismiss = { onEvent(TodoListScreenEvent.EditTaskBottomSheetDismissed) },
      onSaveItem = { updatedItem ->
        onEvent(TodoListScreenEvent.TaskEdited(updatedItem))
        onEvent(TodoListScreenEvent.EditTaskBottomSheetDismissed)
      }
    )
  }
}

// region preview

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoListScreenContentPreview() {
  ToDoListTheme {
    TodoListScreenContent(
      state = TodoListScreenState(taskList = remember {
        mutableStateListOf(
          TodoTask(name = "task1", id = 1),
          TodoTask(name = "task2", id = 2, isCompleted = true),
          TodoTask(name = "task3", id = 3)
        )
      }
      )
    )
  }
}

// endregion
