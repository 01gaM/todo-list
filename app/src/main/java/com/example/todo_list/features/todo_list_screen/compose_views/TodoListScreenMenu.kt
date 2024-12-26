package com.example.todo_list.features.todo_list_screen.compose_views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todo_list.R

@Composable
fun TodoListScreenMenu(
  isVisible: Boolean,
  isDeleteCompletedChecked: Boolean,
  onDismiss: () -> Unit,
  onDeleteAllClick: () -> Unit,
  onReorderTasksClick: () -> Unit,
  onShuffleListClick: () -> Unit,
  onDeleteCompletedChanged: () -> Unit
) {
  val context = LocalContext.current
  DropdownMenu(
    expanded = isVisible,
    onDismissRequest = onDismiss,
    content = {
      DropdownMenuItem(
        text = {
          Text(
            text = context.getString(R.string.todo_list_screen_menu_reorder),
            color = MaterialTheme.colorScheme.primary
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Rounded.DragHandle,
            contentDescription = "Reorder icon",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        onClick = onReorderTasksClick
      )

      DropdownMenuItem(
        text = {
          Text(
            text = context.getString(R.string.todo_list_screen_menu_shuffle),
            color = MaterialTheme.colorScheme.primary
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Rounded.AutoFixHigh,
            contentDescription = "Shuffle icon",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        onClick = onShuffleListClick
      )

      DropdownMenuItem(
        text = {
          Text(
            text = context.getString(R.string.todo_list_screen_menu_delete_completed),
            color = MaterialTheme.colorScheme.primary
          )
        },
        leadingIcon = {
          CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Checkbox(
              checked = isDeleteCompletedChecked,
              onCheckedChange = null
            )
          }
        },
        onClick = onDeleteCompletedChanged
      )

      DropdownMenuItem(
        text = {
          Text(
            text = context.getString(R.string.todo_list_screen_menu_delete_all),
            color = MaterialTheme.colorScheme.primary
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete all icon",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        onClick = onDeleteAllClick
      )
    }
  )
}
