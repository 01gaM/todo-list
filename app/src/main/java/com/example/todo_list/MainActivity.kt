package com.example.todo_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.todo_list.ui.main_screen.compose.MainScreenContent
import com.example.todo_list.ui.main_screen.model.TodoTask
import com.example.todo_list.ui.theme.ToDoListTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val taskList = remember {
        mutableStateListOf(
          TodoTask(name = "task1", id = 1),
          TodoTask(name = "task2", id = 2),
          TodoTask(name = "task3", id = 3)
        )
      }

      val onItemClick: (Int) -> Unit = { index ->
        with(taskList[index]) {
          taskList[index] = copy(isCompleted = !isCompleted)
        }
      }

      val onItemDelete: (TodoTask) -> Unit = { deletedTask ->
        taskList.remove(deletedTask)
      }

      val onItemMoved: (Int, Int) -> Unit = { fromIndex, toIndex ->
        val item = taskList.removeAt(fromIndex)
        taskList.add(toIndex, item)
      }

      val onItemAdd: (String) -> Unit = { newItemName ->
        taskList.add(
          TodoTask(
            id = Random.Default.nextInt(), // TODO: set item database id
            name = newItemName
          )
        )
      }

      ToDoListTheme {
        MainScreenContent(
          taskList = taskList,
          onItemClick = onItemClick,
          onItemDelete = onItemDelete,
          onDeleteAllClick = { taskList.clear() },
          onItemMoved = onItemMoved,
          onItemAdd = onItemAdd,
          onShuffleListClick = { taskList.shuffle() }
        )
      }
    }
  }
}
