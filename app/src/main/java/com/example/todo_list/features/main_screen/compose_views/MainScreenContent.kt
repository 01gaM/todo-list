package com.example.todo_list.features.main_screen.compose_views

import com.example.todo_list.features.todo_list_screen.compose_views.EmptyTodoListContent
import com.example.todo_list.common.ui.compose_views.NewItemBottomSheet
import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todo_list.R
import com.example.todo_list.common.ui.theme.ToDoListTheme
import com.example.todo_list.features.main_screen.model.TodoList
import com.example.todo_list.features.main_screen.mvi.MainScreenEvent
import com.example.todo_list.features.main_screen.mvi.MainScreenState
import com.example.todo_list.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
  modifier: Modifier = Modifier,
  navController: NavController?,
  state: MainScreenState,
  onEvent: (MainScreenEvent) -> Unit = {}
) {
  val isNoListsSaved = remember(state.todoLists) { state.todoLists.isEmpty() }
  val lazyGridState = rememberLazyGridState()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background)
      .navigationBarsPadding(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.app_name),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
          )
        },
        colors = TopAppBarDefaults.topAppBarColors()
          .copy(containerColor = MaterialTheme.colorScheme.primary),
//        actions = {
//          IconButton(
//            onClick = {}, // TODO: add event
//            content = {
//              Icon(
//                imageVector = Icons.Default.MoreVert,
//                contentDescription = "More icon",
//                tint = MaterialTheme.colorScheme.onPrimary
//              )
//            }
//          )
//          // TODO: add menu
//        }
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        shape = CircleShape,
        content = {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add task icon"
          )
        },
        onClick = { onEvent(MainScreenEvent.AddNewListFabClicked) }
      )
    }
  ) { innerPadding ->
    if (state.isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.Center
      ) { CircularProgressIndicator() }
    } else {
      Crossfade(
        targetState = isNoListsSaved,
        label = "cross_fade_empty_list"
      ) { isListEmpty ->
        if (isListEmpty) {
          EmptyTodoListContent(
            modifier = Modifier
              .padding(paddingValues = innerPadding)
              .fillMaxSize(),
            title = stringResource(R.string.no_todo_lists_saved_title),
            description = stringResource(R.string.no_todo_lists_saved_title_description)
          )
        } else {
          LazyVerticalGrid(
            modifier = Modifier
              .padding(all = 16.dp)
              .fillMaxSize(),
            state = lazyGridState,
            columns = GridCells.Fixed(2),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            items(items = state.todoLists) { item ->
              TodoListView(
                name = item.name,
                onItemClick = {
                  navController?.navigate(Screen.TodoList.route) // TODO: pass list id
                }
              )
            }
          }
        }
      }
    }
  }

  NewItemBottomSheet(
    visible = state.showNewListBottomSheet,
    title = stringResource(R.string.new_todo_list_bottom_sheet_title),
    onDismiss = { onEvent(MainScreenEvent.AddNewListBottomSheetDismissed) },
    onSaveItem = { newItemName ->
      onEvent(MainScreenEvent.NewTodoListAdded(newItemName))
      onEvent(MainScreenEvent.AddNewListBottomSheetDismissed)
    }
  )
}

@Composable
fun TodoListView(name: String, onItemClick: () -> Unit) {
  Card(
    modifier = Modifier.clickable(onClick = onItemClick),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Text(
      modifier = Modifier
        .fillMaxWidth()
        .padding(all = 16.dp),
      text = name,
      textAlign = TextAlign.Center,
      fontWeight = FontWeight.Bold,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1
    )
  }
}

// region preview

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoListScreenContentPreview() {
  ToDoListTheme {
    MainScreenContent(
      navController = null,
      state = MainScreenState(
        todoLists = listOf(
          TodoList(id = 1, name = "Test1Test1Test1Test1Test1Test1Test1"),
          TodoList(id = 2, name = "Test2"),
          TodoList(id = 3, name = "Test3"),
          TodoList(id = 4, name = "Test4")
        )
      )
    )
  }
}

// endregion
