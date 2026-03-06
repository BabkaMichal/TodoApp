package com.example.todoapp

import android.app.Application

class TodoApplication : Application() {

    //lazy means create it once someone needs it
    val database by lazy { TodoDatabase.getDatabase(this) }
    val repository by lazy { TodoRepository(database.todoDao()) }
}