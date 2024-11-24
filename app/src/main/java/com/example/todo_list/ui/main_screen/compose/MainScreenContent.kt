package com.example.todo_list.ui.main_screen.compose

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.ui.composables.SwipeToDeleteContainer
import com.example.todo_list.ui.main_screen.model.TodoTask
import com.example.todo_list.ui.theme.ToDoListTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreenContent(
  modifier: Modifier = Modifier,
  taskList: List<TodoTask> = emptyList(),
  onItemClick: (Int) -> Unit = {},
  onItemDelete: (TodoTask) -> Unit = {}
) {
  val isTaskListEmpty by remember(taskList) { derivedStateOf { taskList.isEmpty() } }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background),
    topBar = {
      TopAppBar(
        title = {
          Text(
            modifier = Modifier.fillMaxWidth(),
            text = "To-Do list",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
          )
        },
        colors = TopAppBarDefaults.topAppBarColors()
          .copy(containerColor = MaterialTheme.colorScheme.primary)
      )
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
          text = "List is empty!",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
        )
        Text(
          modifier = Modifier.padding(top = 8.dp),
          text = "Tap on \"+\" to add a new item.",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.secondary
        )
      }
    }

    if (!isTaskListEmpty) {
      LazyColumn(contentPadding = innerPadding) {
        itemsIndexed(
          items = taskList,
          key = { _, task -> task.id }
        ) { index, item ->
          SwipeToDeleteContainer(item = item, onDelete = onItemDelete) {
            TodoListItem(
              modifier = Modifier.animateItemPlacement(),
              taskNumber = index + 1,
              taskName = item.name,
              isCompleted = item.isCompleted,
              onClick = { onItemClick(index) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun TodoListItem(
  modifier: Modifier = Modifier,
  taskNumber: Int,
  taskName: String,
  isCompleted: Boolean,
  onClick: () -> Unit
) {
  val notCompletedColor = MaterialTheme.colorScheme.secondary
  val completedColor = MaterialTheme.colorScheme.inversePrimary

  val taskNameColor = remember(key1 = isCompleted) {
    if (isCompleted) completedColor else notCompletedColor
  }
  val animatedTaskNameColor by animateColorAsState(
    targetValue = taskNameColor,
    label = "taskNameColor"
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .background(color = MaterialTheme.colorScheme.background)
      .padding(all = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {

    Text(
      modifier = Modifier.padding(end = 16.dp),
      text = "$taskNumber.",
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.primary
    )

    Text(
      modifier = Modifier.weight(weight = 1f),
      text = taskName,
      style = MaterialTheme.typography.bodyLarge,
      color = animatedTaskNameColor,
      textDecoration = remember(isCompleted) {
        if (isCompleted) TextDecoration.LineThrough else null
      }
    )

    Checkbox(
      checked = isCompleted,
      onCheckedChange = null
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
      taskList = remember {
        mutableStateListOf(
          TodoTask(name = "task1", id = 1),
          TodoTask(name = "task2", id = 2),
          TodoTask(name = "task3", id = 3)
        )
      }
    )
  }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0x000000)
@Composable
private fun TodoListItemPreview() {
  ToDoListTheme {
    TodoListItem(taskNumber = 1, taskName = "Some task", isCompleted = false, onClick = {})
  }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0x000000)
@Composable
private fun TodoListItemCompletedPreview() {
  ToDoListTheme {
    TodoListItem(
      taskNumber = 1,
      taskName = "Some task",
      isCompleted = true,
      onClick = {}
    )
  }
}

// endregion
