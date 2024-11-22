package com.example.todo_list.ui.main_screen.compose

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.ui.main_screen.model.TodoTask
import com.example.todo_list.ui.theme.ToDoListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(modifier: Modifier = Modifier) {
  val taskList = remember {
    mutableStateListOf(
      TodoTask(name = "task1"),
      TodoTask(name = "task2"),
      TodoTask(name = "task3")
    )
  }

  val onItemClick: (Int) -> Unit = remember {
    { index ->
      with(taskList[index]) {
        taskList[index] = copy(isCompleted = !isCompleted)
      }
    }
  }

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
    LazyColumn(contentPadding = innerPadding) {
      itemsIndexed(taskList) { index, item ->
        TodoListItem(
          taskNumber = index + 1,
          taskName = item.name,
          isCompleted = item.isCompleted,
          onClick = { onItemClick(index) }
        )
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

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenContentPreview() {
  ToDoListTheme {
    MainScreenContent()
  }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0x000000)
@Composable
fun TodoListItemPreview() {
  ToDoListTheme {
    TodoListItem(taskNumber = 1, taskName = "Some task", isCompleted = false, onClick = {})
  }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0x000000)
@Composable
fun TodoListItemCompletedPreview() {
  ToDoListTheme {
    TodoListItem(
      taskNumber = 1,
      taskName = "Some task",
      isCompleted = true,
      onClick = {}
    )
  }
}
