package com.example.todo_list.features.main_screen.compose_views

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.R
import com.example.todo_list.common.ui.compose_views.SwipeActionContainer
import com.example.todo_list.features.main_screen.model.TodoTask
import com.example.todo_list.common.ui.theme.ToDoListTheme
import com.example.todo_list.features.main_screen.mvi.MainScreenEvent
import com.example.todo_list.features.main_screen.mvi.MainScreenState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
  modifier: Modifier = Modifier,
  state: MainScreenState,
  onEvent: (MainScreenEvent) -> Unit = {}
) {
  val isTaskListEmpty = remember(state.taskList) { state.taskList.isEmpty() }
  val lazyListState = rememberLazyListState()
  val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
    onEvent(MainScreenEvent.TaskMoved(from.index, to.index))
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
              onClick = { onEvent(MainScreenEvent.MenuClicked) },
              content = {
                Icon(
                  imageVector = Icons.Default.MoreVert,
                  contentDescription = "More icon",
                  tint = MaterialTheme.colorScheme.onPrimary
                )
              }
            )
          }

          MainScreenMenu(
            isVisible = state.displayMenu,
            onDismiss = { onEvent(MainScreenEvent.MenuDismissed) },
            onDeleteAllClick = {
              onEvent(MainScreenEvent.MenuDismissed)
              onEvent(MainScreenEvent.AllTasksDeleted)
            },
            onReorderTasksClick = {
              onEvent(MainScreenEvent.MenuDismissed)
              onEvent(MainScreenEvent.ReorderTasksClicked)
            },
            onShuffleListClick = {
              onEvent(MainScreenEvent.MenuDismissed)
              onEvent(MainScreenEvent.TasksShuffled)
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
        Button(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
          onClick = { onEvent(MainScreenEvent.ReorderTasksCompleted) }) {
          Text(text = stringResource(R.string.save).uppercase())
        }
      }
    },
    floatingActionButton = {
      AnimatedVisibility(
        visible = !state.isReorderingMode,
        enter = fadeIn(),
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
          onClick = { onEvent(MainScreenEvent.AddNewTaskFabClicked) }
        )
      }
    }
  ) { innerPadding ->
    Crossfade(
      targetState = isTaskListEmpty,
      label = "cross_fade_empty_list"
    ) { isListEmpty ->
      if (isListEmpty) {
        EmptyTodoListContent(
          modifier = Modifier
            .padding(paddingValues = innerPadding)
            .fillMaxSize()
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
                  taskNumber = index + 1,
                  taskName = item.name,
                  isCompleted = item.isCompleted,
                  isReorderingMode = true,
                  onClick = { onEvent(MainScreenEvent.TaskClicked(index)) }
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
                onDelete = { onEvent(MainScreenEvent.TaskDeleted(item)) },
                onEdit = { onEvent(MainScreenEvent.EditTaskSelected(item)) }
              ) {
                TodoListItem(
                  taskNumber = index + 1,
                  taskName = item.name,
                  isCompleted = item.isCompleted,
                  isReorderingMode = false,
                  onClick = { onEvent(MainScreenEvent.TaskClicked(index)) }
                )
              }
            }
          }
        }
      }
    }
  }

  NewTaskBottomSheet(
    visible = state.showNewTaskBottomSheet,
    onDismiss = { onEvent(MainScreenEvent.AddNewTaskBottomSheetDismissed) },
    onSaveItem = { newItemName ->
      onEvent(MainScreenEvent.NewTaskAdded(newItemName))
      onEvent(MainScreenEvent.AddNewTaskBottomSheetDismissed)
    }
  )

  state.taskToEdit?.let {
    EditTaskBottomSheet(
      visible = true,
      task = it,
      onDismiss = { onEvent(MainScreenEvent.EditTaskBottomSheetDismissed) },
      onSaveItem = { updatedItem ->
        onEvent(MainScreenEvent.TaskEdited(updatedItem))
        onEvent(MainScreenEvent.EditTaskBottomSheetDismissed)
      }
    )
  }
}

// region preview

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainScreenContentPreview() {
  ToDoListTheme {
    MainScreenContent(
      state = MainScreenState(taskList = remember {
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
