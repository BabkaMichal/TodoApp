package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.ui.theme.TodoAppTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        val app = application as TodoApplication
        val sharedPrefs = getSharedPreferences("todo_prefs", android.content.Context.MODE_PRIVATE)
        val viewModelFactory = TodoViewModelFactory(app.repository, sharedPrefs)

        setContent {
            TodoAppTheme {
                val activity = LocalActivity.current
                val colorScheme = MaterialTheme.colorScheme
                val darkTheme = isSystemInDarkTheme()

                SideEffect {
                    activity?.window?.let { window ->
                        window.statusBarColor = colorScheme.secondaryContainer.toArgb()

                        WindowCompat.getInsetsController(window, window.decorView).apply {
                            isAppearanceLightStatusBars = !darkTheme
                        }
                    }
                }

                val viewModel: TodoViewModel = viewModel(factory = viewModelFactory)
                TodoListScreen(viewModel = viewModel)
            }
        }
    }
}