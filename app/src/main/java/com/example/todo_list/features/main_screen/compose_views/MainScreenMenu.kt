package com.example.todo_list.features.main_screen.compose_views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.todo_list.R

@Composable
fun MainScreenMenu(
  isVisible: Boolean,
  onDismiss: () -> Unit,
  onDeleteAllClick: () -> Unit,
  onReorderTasksClick: () -> Unit,
  onShuffleListClick: () -> Unit
) {
  val context = LocalContext.current
  DropdownMenu(
    expanded = isVisible,
    onDismissRequest = onDismiss,
    content = {
      DropdownMenuItem(
        text = {
          Text(
            text = context.getString(R.string.main_screen_menu_delete_all),
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

      DropdownMenuItem(
        text = {
          Text(
            text = context.getString(R.string.main_screen_menu_reorder),
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
            text = context.getString(R.string.main_screen_menu_shuffle),
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
    }
  )
}
