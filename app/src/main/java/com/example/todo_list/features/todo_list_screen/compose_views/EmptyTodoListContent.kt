package com.example.todo_list.features.todo_list_screen.compose_views

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.R
import com.example.todo_list.common.ui.theme.ToDoListTheme

@Composable
fun EmptyTodoListContent(
  modifier: Modifier = Modifier,
  title: String,
  description: String
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.primary,
      fontWeight = FontWeight.Bold,
    )
    Text(
      modifier = Modifier.padding(top = 8.dp),
      text = description,
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.secondary
    )
  }
}

// region preview

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun EmptyTodoListContentPreview() {
  ToDoListTheme {
    EmptyTodoListContent(
      modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background),
      title = stringResource(R.string.todo_list_screen_empty_list_title),
      description = stringResource(R.string.todo_list_screen_empty_list_description)
    )
  }
}

// endregion
