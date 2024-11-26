package com.example.todo_list.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.ui.theme.ToDoListTheme
import kotlinx.coroutines.delay

@Composable
fun <T> SwipeActionContainer(
  modifier: Modifier = Modifier,
  item: T,
  onDelete: (T) -> Unit,
  onEdit: () -> Unit,
  animationDuration: Int = 500,
  content: @Composable (T) -> Unit
) {
  var isRemoved by remember { mutableStateOf(false) }
  val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = { value ->
      when (value) {
        SwipeToDismissBoxValue.EndToStart -> {
          isRemoved = true
          true
        }

        SwipeToDismissBoxValue.StartToEnd -> {
          onEdit()
          false
        }

        else -> false
      }
    }
  )

  LaunchedEffect(key1 = isRemoved) {
    if (isRemoved) {
      delay(animationDuration.toLong())
      onDelete(item)
    }
  }

  AnimatedVisibility(
    modifier = modifier,
    visible = !isRemoved,
    exit = shrinkVertically(
      tween(durationMillis = animationDuration),
      shrinkTowards = Alignment.Top
    )
  ) {
    SwipeToDismissBox(
      state = dismissState,
      backgroundContent = { ItemBackground(swipeToDismissState = dismissState) },
      content = { content(item) }
    )
  }
}

@Composable
private fun ItemBackground(swipeToDismissState: SwipeToDismissBoxState) {
  val (color, icon, alignment) = when (swipeToDismissState.dismissDirection) {
    SwipeToDismissBoxValue.EndToStart -> Triple(
      MaterialTheme.colorScheme.error,
      Icons.Default.Delete,
      Alignment.CenterEnd
    )

    SwipeToDismissBoxValue.StartToEnd -> Triple(
      MaterialTheme.colorScheme.tertiary,
      Icons.Default.Edit,
      Alignment.CenterStart
    )

    else -> Triple(Color.Transparent, null, Alignment.Center)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color)
      .padding(all = 16.dp),
    contentAlignment = alignment
  ) {
    if (icon != null) {
      Icon(
        tint = Color.White,
        imageVector = icon,
        contentDescription = "Item action icon"
      )
    }
  }
}

// region preview

@Composable
@Preview(showBackground = true)
private fun SwipeToDeleteContainerPreview() {
  ToDoListTheme {
    SwipeActionContainer(
      item = "test",
      onDelete = {},
      onEdit = {},
      content = {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(all = 16.dp)
        ) {
          Text("Test")
        }
      }
    )
  }
}

// endregion
