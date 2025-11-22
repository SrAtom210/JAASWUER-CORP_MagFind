package com.example.magfind1

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.magfind1.pnavigation.NavManager
import com.example.magfind1.ui.theme.MagFindTheme
import com.example.magfind1.ui.theme.ThemeRepository
import com.example.magfind1.ui.theme.ThemeViewModel
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

    private val themeViewModel by viewModels<ThemeViewModel> {
        viewModelFactory {
            initializer {
                ThemeViewModel(ThemeRepository(applicationContext))
            }
        }
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permiso = android.Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permiso), 100)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        MobileAds.initialize(this) {}
        // Cargar sesión si existe
        SessionManager.loadSession(this)

        //permiso para notificaciones push
        pedirPermisoNotificaciones()

        setContent {
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            MagFindTheme(darkTheme = isDarkMode) {
                NavManager(themeViewModel)
            }
        }
    }
}
