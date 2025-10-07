package com.example.magfind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.magfind.ui.theme.MagFindTheme
import com.example.magfind.views.HomeView
import com.example.magfind.views.LoginView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MagFindTheme {
                // Estado para almacenar el token
                var token by remember { mutableStateOf<String?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    if (token == null) {
                        // Mostrar LoginView
                        LoginView(
                            onLoginSuccess = { t -> token = t },
                            onRegisterClick = { /* Aquí podrías abrir pantalla de registro */ }
                        )
                    } else {
                        // Mostrar HomeView
                        HomeView()
                    }
                }
            }
        }
    }
}
