package com.example.todo_list.features.main_screen.compose_views

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.R
import com.example.todo_list.common.ui.compose_views.BaseModalBottomSheet
import com.example.todo_list.common.ui.theme.ToDoListTheme

@Composable
fun NewTaskBottomSheet(
  modifier: Modifier = Modifier,
  visible: Boolean,
  onDismiss: () -> Unit,
  onSaveItem: (String) -> Unit
) {
  BaseModalBottomSheet(
    modifier = modifier,
    visible = visible,
    onDismiss = onDismiss,
  ) {
    BottomSheetContent(onSaveItem = onSaveItem)
  }
}

@Composable
private fun BottomSheetContent(onSaveItem: (String) -> Unit) {
  var taskName by remember { mutableStateOf("") }
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = MaterialTheme.colorScheme.background)
      .padding(all = 16.dp),
    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
  ) {
    Text(
      modifier = Modifier.fillMaxWidth(),
      text = stringResource(R.string.new_task_bottom_sheet_title),
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center,
      fontWeight = FontWeight.Bold
    )

    OutlinedTextField(
      modifier = Modifier.fillMaxWidth(),
      value = taskName,
      onValueChange = { taskName = it },
      label = { Text(text = stringResource(R.string.new_task_bottom_sheet_task_name)) }
    )

    Button(
      modifier = Modifier.fillMaxWidth(),
      onClick = { onSaveItem(taskName) },
      enabled = taskName.isNotEmpty(),
      content = {
        Text(text = stringResource(R.string.save).uppercase())
      }
    )
  }
}

// region preview

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun NewTaskBottomSheetContentPreview() {
  ToDoListTheme {
    BottomSheetContent(onSaveItem = {})
  }
}

// endregion
