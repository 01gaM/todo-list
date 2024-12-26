package com.example.todo_list.data.data_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
private val IS_DELETE_COMPLETED_CHECKED = booleanPreferencesKey("is_delete_completed_checked")

class DataStoreManager(private val context: Context) {
  val isDeleteCompletedCheckedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
    preferences[IS_DELETE_COMPLETED_CHECKED] ?: false
  }

  suspend fun updateIsDeleteCompletedChecked(value: Boolean) {
    context.dataStore.edit { preferences ->
      preferences[IS_DELETE_COMPLETED_CHECKED] = value
    }
  }
}
