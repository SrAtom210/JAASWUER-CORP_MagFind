package com.example.magfind1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.magfind1.google.GmailAuthCallback
import com.example.magfind1.google.GmailAuthManager
import com.example.magfind1.pnavigation.NavManager
import com.example.magfind1.ui.theme.MagFindTheme
import com.example.magfind1.ui.theme.ThemeRepository
import com.example.magfind1.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel by viewModels<ThemeViewModel> {
        viewModelFactory {
            initializer {
                ThemeViewModel(ThemeRepository(applicationContext))
            }
        }
    }

    // Manejo del resultado de login con Gmail (para sincronización)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        GmailAuthManager.handleActivityResult(requestCode, data) { authCode ->
            if (authCode != null) {
                GmailAuthCallback.onCodeReceived?.invoke(authCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Cargar sesión si existe
        SessionManager.loadSession(this)

        setContent {
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            MagFindTheme(darkTheme = isDarkMode) {
                NavManager(themeViewModel)
            }
        }
    }
}
