package com.example.todo_list.common.ui.compose_views

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseModalBottomSheet(
  modifier: Modifier = Modifier,
  visible: Boolean,
  onDismiss: () -> Unit,
  content: @Composable () -> Unit
) {
  val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  if (visible) {
    ModalBottomSheet(
      modifier = modifier,
      onDismissRequest = onDismiss,
      sheetState = bottomSheetState,
    ) {
      content()
    }
  }
}
