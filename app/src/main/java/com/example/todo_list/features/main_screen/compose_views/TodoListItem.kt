package com.example.todo_list.features.main_screen.compose_views

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.common.ui.theme.ToDoListTheme
import java.util.Locale

@Composable
fun TodoListItem(
  modifier: Modifier = Modifier,
  taskNumber: Int,
  taskName: String,
  isCompleted: Boolean,
  isReorderingMode: Boolean = false,
  onClick: () -> Unit
) {
  val capitalizedName = remember(taskName) {
    taskName.replaceFirstChar {
      if (it.isLowerCase()) it.titlecase(Locale.getDefault())
      else it.toString()
    }
  }

  val notCompletedColor = MaterialTheme.colorScheme.onBackground
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
      text = capitalizedName,
      style = MaterialTheme.typography.bodyLarge,
      color = animatedTaskNameColor,
      textDecoration = remember(isCompleted) {
        if (isCompleted) TextDecoration.LineThrough else null
      }
    )

    Crossfade(
      targetState = isReorderingMode,
      label = "Reordering Icon/Checkbox transition"
    ) { isReordering ->
      if (isReordering) {
        Icon(
          imageVector = Icons.Default.DragHandle,
          contentDescription = "Drag handle icon",
          tint = MaterialTheme.colorScheme.primary
        )
      } else {
        Checkbox(
          checked = isCompleted,
          onCheckedChange = null
        )
      }
    }
  }
}

// region preview

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