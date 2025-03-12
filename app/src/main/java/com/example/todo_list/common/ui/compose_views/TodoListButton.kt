package com.example.todo_list.common.ui.compose_views

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo_list.common.ui.theme.ToDoListTheme

@Composable
fun TodoListButton(
  modifier: Modifier = Modifier,
  text: String,
  textColor: Color = MaterialTheme.colorScheme.onPrimary,
  borderColor: Color = MaterialTheme.colorScheme.primary,
  backgroundColor: Color = MaterialTheme.colorScheme.primary,
  onClick: () -> Unit
) {
  OutlinedButton(
    modifier = modifier,
    onClick = onClick,
    shape = RoundedCornerShape(corner = CornerSize(size = 4.dp)),
    border = BorderStroke(width = 1.dp, color = borderColor),
    colors = ButtonDefaults.buttonColors().copy(containerColor = backgroundColor)
  ) {
    Text(
      text = text,
      fontWeight = FontWeight.Bold,
      color = textColor
    )
  }
}

// region preview

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoListButtonPreview() {
  ToDoListTheme {
    TodoListButton(
      text = "My button",
      borderColor = MaterialTheme.colorScheme.background,
      onClick = {}
    )
  }
}

// endregion
