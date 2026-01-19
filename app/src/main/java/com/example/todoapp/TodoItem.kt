package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

// Defining the types of items
enum class ItemType {
    HABIT, //doesn't disappear
    TASK, //has deadline
    LIST_ITEM //list
}

//defining the difficulty
enum class Difficulty {
    EASY, //green
    MEDIUM, //orange
    HARD //red
}

//one item in database
@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, //unique id
    val title: String,
    val description: String = "", //can be null
    val type: ItemType,
    val deadline: Long? = null, //time in milliseconds, can be null
    val difficulty: Difficulty = Difficulty.EASY,
    val isCompleted: Boolean = false
)