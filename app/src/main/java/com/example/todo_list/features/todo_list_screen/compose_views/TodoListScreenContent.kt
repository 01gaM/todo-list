package com.example.todo_list.features.todo_list_screen.compose_views

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todo_list.R
import com.example.todo_list.common.ui.compose_views.NewItemBottomSheet
import com.example.todo_list.common.ui.compose_views.SwipeActionContainer
import com.example.todo_list.features.todo_list_screen.model.TodoTask
import com.example.todo_list.common.ui.theme.ToDoListTheme
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenEvent
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenMode
import com.example.todo_list.features.todo_list_screen.mvi.TodoListScreenState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreenContent(
  modifier: Modifier = Modifier,
  navController: NavController?,
  state: TodoListScreenState,
  onEvent: (TodoListScreenEvent) -> Unit = {}
) {
  val isTaskListEmpty = remember(state.taskList) { state.taskList.isEmpty() }
  val lazyListState = rememberLazyListState()
  val reorderedTaskList by remember(state.contentMode) {
    mutableStateOf(
      if (state.contentMode is TodoListScreenMode.Reordering)
        state.contentMode.reorderingModeTaskList
      else
        emptyList()
    )
  }
  val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
    onEvent(
      TodoListScreenEvent.TaskMoved(
        from.index,
        to.index
      )
    )
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background)
      .navigationBarsPadding(),
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text = state.taskListName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
          )
        },
        colors = TopAppBarDefaults.topAppBarColors()
          .copy(containerColor = MaterialTheme.colorScheme.primary),
        navigationIcon = {
          val animationOffset = { size: IntSize -> IntOffset(-size.width, 0) }
          AnimatedVisibility(
            visible = state.contentMode !is TodoListScreenMode.Reordering,
            enter = slideIn(initialOffset = animationOffset),
            exit = slideOut(targetOffset = animationOffset)
          ) {
            IconButton(
              modifier = Modifier.padding(all = 16.dp),
              onClick = { navController?.navigateUp() },
              content = {
                Icon(
                  imageVector = Icons.AutoMirrored.Default.ArrowBack,
                  contentDescription = "Back icon",
                  tint = MaterialTheme.colorScheme.onPrimary,
                )
              }
            )
          }
        },
        actions = {
          val animationOffset = { size: IntSize -> IntOffset(size.width, 0) }
          AnimatedVisibility(
            visible = state.contentMode !is TodoListScreenMode.Reordering,
            enter = slideIn(initialOffset = animationOffset),
            exit = slideOut(targetOffset = animationOffset)
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
    floatingActionButton = {
      val animationOffset = { size: IntSize -> IntOffset(0, -size.height) }
      AnimatedVisibility(
        visible = state.contentMode !is TodoListScreenMode.Reordering,
        enter = slideIn(initialOffset = animationOffset) + scaleIn(),
        exit = slideOut(targetOffset = animationOffset) + scaleOut()
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
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.Center
    ) {
      when (state.contentMode) { // TODO: add animated content
        TodoListScreenMode.Loading -> {
          CircularProgressIndicator()
        }

        is TodoListScreenMode.Reordering -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
          ) {
            itemsIndexed(
              items = state.contentMode.reorderingModeTaskList,
              key = { _, task -> task.id }
            ) { index, item ->
              ReorderableItem(
                state = reorderableLazyListState,
                key = item.id
              ) { _ ->
                TodoListItem(
                  modifier = Modifier.draggableHandle(),
                  taskIndex = index + 1,
                  taskName = item.name,
                  isCompleted = item.isCompleted,
                  isReorderingMode = true
                )
              }
            }
          }
        }

        TodoListScreenMode.ViewList -> {
          if (isTaskListEmpty) {
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
              state = lazyListState
            ) {
              itemsIndexed(
                items = state.taskList,
                key = { _, task -> task.id }
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
                    onClick = { onEvent(TodoListScreenEvent.TaskClicked(item.id)) }
                  )
                }
              }
            }
          }
        }
      }

      SaveOrderButton(
        modifier = Modifier.align(alignment = Alignment.BottomCenter),
        isVisible = state.contentMode is TodoListScreenMode.Reordering,
        onClick = { onEvent(TodoListScreenEvent.ReorderTasksCompleted(reorderedTaskList)) }
      )
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

// region private

@Composable
private fun SaveOrderButton(
  modifier: Modifier = Modifier,
  isVisible: Boolean,
  onClick: () -> Unit
) {
  val animationOffset = { size: IntSize -> IntOffset(0, size.height) }
  AnimatedVisibility(
    modifier = modifier,
    visible = isVisible,
    enter = slideIn(initialOffset = animationOffset),
    exit = slideOut(targetOffset = animationOffset)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colorScheme.primary)
        .clickable(onClick = onClick)
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(all = 16.dp),
        text = stringResource(R.string.save).uppercase(),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center
      )
    }
  }
}

// endregion

// region preview

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoListScreenContentPreview() {
  ToDoListTheme {
    TodoListScreenContent(
      navController = null,
      state = TodoListScreenState(
        taskList = remember {
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
