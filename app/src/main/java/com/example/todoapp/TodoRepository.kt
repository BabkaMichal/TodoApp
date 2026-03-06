package com.example.todoapp

import kotlinx.coroutines.flow.Flow

class TodoRepository (private val todoDao: TodoDao){

    //get all items
    val allItems: Flow<List<TodoItem>> = todoDao.getAllItems()

    //adding item
    suspend fun insertItem(item: TodoItem){
        todoDao.insertItem(item)
    }

    //delete item
    suspend fun deleteItem(item: TodoItem){
        todoDao.deleteItem(item)
    }

    //update item
    suspend fun updateItem(item: TodoItem){
        todoDao.updateItem(item)
    }
}