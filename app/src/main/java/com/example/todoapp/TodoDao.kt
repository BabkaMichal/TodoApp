package com.example.todoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//Data Access Object -> like PDO
@Dao
interface TodoDao {

    //get all items ordered by deadline
    @Query("SELECT * FROM todo_items ORDER BY deadline ASC")
    fun getAllItems(): Flow<List<TodoItem>>

    //add new item
    @Insert
    suspend fun insertItem(item: TodoItem)

    //remove item
    @Delete
    suspend fun deleteItem(item: TodoItem)

    //update item
    @Update
    suspend fun updateItem(item: TodoItem)
}