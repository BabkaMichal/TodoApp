package com.example.todoapp

import androidx.compose.ui.graphics.Color
import java.util.concurrent.TimeUnit
import java.util.Calendar
import java.util.Locale

//difficulty colors
fun getDifficultyColor(difficulty: Difficulty): Color {
    return when (difficulty) {
        Difficulty.EASY -> Color(0xFF4CAF50)
        Difficulty.MEDIUM -> Color(0xFFFF9800)
        Difficulty.HARD -> Color(0xFFF44336)
    }
}
//formating deadline > 1day -> days, < 1day -> hours
fun formatDeadline(deadline: Long): String {
    val now = System.currentTimeMillis()
    val diff = deadline - now

    return when {
        diff < 0 -> "Expired!"
        diff < TimeUnit.HOURS.toMillis(24) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "$hours h"
        }
        else -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff) + 1
            "$days d"
        }
    }
}

//change first letter to capital
fun String.toCapitalized(): String {
    return this.lowercase(Locale.ROOT).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }
}

fun getEndOfDayTimestamp(daysFromNow: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, daysFromNow)

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)

    return calendar.timeInMillis
}

