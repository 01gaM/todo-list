package com.example.todo_list.ui.main_screen.compose

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.R
import com.example.todo_list.ui.composables.SwipeToDeleteContainer
import com.example.todo_list.ui.main_screen.model.TodoTask
import com.example.todo_list.ui.theme.ToDoListTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
  modifier: Modifier = Modifier,
  taskList: List<TodoTask> = emptyList(),
  onItemClick: (Int) -> Unit = {},
  onItemDelete: (TodoTask) -> Unit = {},
  onDeleteAllClick: () -> Unit = {},
  onItemMoved: (Int, Int) -> Unit = { _, _ -> }
) {
  val isTaskListEmpty by remember(taskList) { derivedStateOf { taskList.isEmpty() } }
  var displayMenu by remember { mutableStateOf(false) }
  val lazyListState = rememberLazyListState()
  val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
    onItemMoved(from.index, to.index)
  }
  var isReorderingMode by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background),
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
            visible = !isReorderingMode,
            enter = expandIn(),
            exit = shrinkOut()
          ) {
            IconButton(
              onClick = { displayMenu = !displayMenu },
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
            isVisible = displayMenu,
            onDismiss = { displayMenu = false },
            onDeleteAllClick = {
              displayMenu = false
              onDeleteAllClick()
            },
            onReorderTasksClick = {
              displayMenu = false
              isReorderingMode = true
            }
          )
        }
      )
    },
    bottomBar = {
      AnimatedVisibility(
        visible = isReorderingMode,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Button(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
          onClick = { isReorderingMode = false }) {
          Text(text = stringResource(R.string.main_screen_button_save).uppercase())
        }
      }
    }
  ) { innerPadding ->
    AnimatedVisibility(
      visible = isTaskListEmpty,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Column(
        modifier = Modifier
          .padding(paddingValues = innerPadding)
          .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = stringResource(R.string.main_screen_empty_list_title),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
        )
        Text(
          modifier = Modifier.padding(top = 8.dp),
          text = stringResource(R.string.main_screen_empty_list_description),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.secondary
        )
      }
    }

    if (!isTaskListEmpty) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = innerPadding
      ) {
        itemsIndexed(
          items = taskList,
          key = { _, task -> task.id }
        ) { index, item ->
          if (isReorderingMode) {
            ReorderableItem(
              state = reorderableLazyListState,
              key = item.id
            ) { _ ->
              TodoListItem(
                modifier = Modifier
                  .draggableHandle()
                  .animateItem(),
                taskNumber = index + 1,
                taskName = item.name,
                isCompleted = item.isCompleted,
                isReorderingMode = isReorderingMode,
                onClick = { onItemClick(index) }
              )
            }
          } else {
            SwipeToDeleteContainer(
              item = item,
              onDelete = onItemDelete
            ) {
              TodoListItem(
                modifier = Modifier.animateItem(),
                taskNumber = index + 1,
                taskName = item.name,
                isCompleted = item.isCompleted,
                isReorderingMode = isReorderingMode,
                onClick = { onItemClick(index) }
              )
            }
          }
        }
      }
    }
  }
}

// region preview

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainScreenContentPreview() {
  ToDoListTheme {
    MainScreenContent(
      taskList = remember {
        mutableStateListOf(
          TodoTask(name = "task1", id = 1),
          TodoTask(name = "task2", id = 2, isCompleted = true),
          TodoTask(name = "task3", id = 3)
        )
      }
    )
  }
}

// endregion
