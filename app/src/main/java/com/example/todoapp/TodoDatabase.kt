package com.example.todoapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//this is a database that contains table TodoItem
@Database(entities = [TodoItem::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    //every other part of application has access to this function
    abstract fun todoDao(): TodoDao

    //things inside companion object are visible to other parts of application
    companion object {
        // volatile means that all threads see the change instantly
        @Volatile
        private var Instance: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase {
            //if exists, return it, otherwise create new one
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}