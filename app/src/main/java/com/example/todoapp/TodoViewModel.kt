package com.example.todoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.SharedPreferences

class TodoViewModel (private val repository: TodoRepository, private val sharedPreferences: SharedPreferences) : ViewModel() {

    //on start delete expired daily tasks
    init {
        deleteExpiredDailyTasks()
        resetRoutinesIfNewDay()
    }

    //reset routines if new day
    private fun resetRoutinesIfNewDay() {
        viewModelScope.launch {
            val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)

            val lastResetDay = sharedPreferences.getLong("last_reset_day", 0L)

            if (currentDay > lastResetDay) {

                val items = repository.allItems.first()

                items.forEach { item ->
                    if (item.type == ItemType.ROUTINE && item.isCompleted) {
                        repository.updateItem(item.copy(isCompleted = false))
                    }
                }

                sharedPreferences.edit().putLong("last_reset_day", currentDay).apply()
            }
        }
    }

    //deletes expired tasks
    private fun deleteExpiredDailyTasks() {
        viewModelScope.launch {
            val items = repository.allItems.first()
            val now = System.currentTimeMillis()

            items.forEach { item ->
                // If daily && expired -> remove
                if (item.type == ItemType.DAILY && item.deadline != null && item.deadline < now) {
                    repository.deleteItem(item)
                }
            }
        }
    }

    //list of items converted to stateflow
    val todoItems: StateFlow<List<TodoItem>> = repository.allItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    //add items
    fun addItem(item: TodoItem) {
        //launching in background
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }
    //delete item
    fun deleteItem(item: TodoItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }
    //update item
    fun updateItem(item: TodoItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }
}

//when we create todoViewModel we need to pass repository
class TodoViewModelFactory(
    private val repository: TodoRepository, private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}