package com.example.magfind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.magfind.pnavigation.NavManager
import com.example.magfind.ui.theme.MagFindTheme
import com.example.magfind.ui.theme.ThemeRepository
import com.example.magfind.ui.theme.ThemeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {

    private val themeViewModel by viewModels<ThemeViewModel> {
        viewModelFactory {
            initializer {
                ThemeViewModel(ThemeRepository(applicationContext))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        SessionManager.loadSession(this)

        setContent {
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            MagFindTheme(darkTheme = isDarkMode) {
                NavManager(themeViewModel)
            }
        }
    }
}
