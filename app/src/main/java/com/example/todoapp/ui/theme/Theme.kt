package com.example.todoapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    primaryContainer = Color(0xFF1B5E20),
    onPrimary = Color(0xFF003300),
    onPrimaryContainer = Color(0xFFE8F5E9),
    secondaryContainer = Color(0xFF1E241E),
    onSecondaryContainer = Color(0xFFE8F5E9),
    secondary = Color(0xFFA1887F),
    onSecondary = Color(0xFF3E2723),
    background = Color(0xFF121612),
    surface = Color(0xFF1E241E),
    tertiary = Color(0xFF8D6E63)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF388E3C),
    primaryContainer = Color(0xFFE8F5E9),
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color.White,
    background = Color(0xFFF1F8F1),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF795548),
    onSecondary = Color.White,
    tertiary = Color(0xFF5D4037)
)

@Composable
fun TodoAppTheme (
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}