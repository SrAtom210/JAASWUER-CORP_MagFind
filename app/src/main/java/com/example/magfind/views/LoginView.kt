package com.example.magfind.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.magfind.apis.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun LoginView(
    onLoginSuccess: (token: String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val repo = remember { AuthRepository() }

    // Fondo blanco
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                // Campo de usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario", color = Color.DarkGray) },
                    textStyle = LocalTextStyle.current.copy(color = Color.DarkGray),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.DarkGray,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.DarkGray,
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )

                // Campo de contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.DarkGray) },
                    textStyle = LocalTextStyle.current.copy(color = Color.DarkGray),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.DarkGray,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.DarkGray,
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )

                // Mensaje de error (en rojo)
                if (errorMsg != null) {
                    Text(
                        errorMsg!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Botón de login
                Button(
                    onClick = {
                        errorMsg = null
                        if (username.isBlank() || password.isBlank()) {
                            errorMsg = "Usuario y contraseña son obligatorios"
                            return@Button
                        }
                        loading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            val token = repo.login(username, password)
                            loading = false
                            if (token != null) {
                                onLoginSuccess(token)
                            } else {
                                errorMsg = "Usuario o contraseña incorrectos"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        if (loading) "Cargando..." else "Login",
                        color = Color.White
                    )
                }

                // Enlace de registro
                TextButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrarse", color = Color.DarkGray)
                }
            }
        }
    }
}

