package com.example.magfind.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.magfind.R
@Composable
fun LoginView(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado para controlar la visibilidad del diálogo de registro
    var showDialog by remember { mutableStateOf(false) }

    // Estados para los campos de texto del diálogo
    var email by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }

    // Fondo blanco
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            contentAlignment = Alignment.TopCenter
        )
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Image(painter = painterResource(R.drawable.magfind), contentDescription = "logo")
                val gradient = Brush.linearGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFF00BCD4))
                )

                Text(
                    text = "MagFind",
                    fontSize = 40.sp,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        brush = gradient
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                )
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
                TextButton(
                    onClick = { navController.navigate("OContraseña") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End)
                ) {
                    Text("Olvidé mi Contraseña", color = Color.Black, textAlign = TextAlign.Right)
                }
                // Botón de login
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        "Iniciar Sesión",
                        color = Color.White
                    )
                }
                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, Color.Black)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                )
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.gmail),
                            contentDescription = "Gmail",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar con Gmail", color = Color.Black)
                        /*navController.navigate("Categoria")*/
                    }
                }
                // Enlace de registro
                TextButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Aún no tienes una cuenta? Regístrate", color = Color.Black)
                }
                // Si el diálogo debe mostrarse, lo componemos
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            // Oculta el diálogo si el usuario toca fuera de él
                            showDialog = false
                        },
                        title = {
                            Text(text = "Registro Rápido")
                        },
                        text = {
                            // Columna para organizar los campos de texto
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Correo Electrónico") }
                                )
                                OutlinedTextField(
                                    value = nuevaPassword,
                                    onValueChange = { nuevaPassword = it },
                                    label = { Text("Contraseña") },
                                    visualTransformation = PasswordVisualTransformation()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    // Aquí puedes añadir la lógica de registro
                                    // Por ejemplo, validar los campos y luego navegar

                                    // Oculta el diálogo
                                    showDialog = false
                                    // Navega a la pantalla de home o a donde necesites
                                    navController.navigate("home")
                                }
                            ) {
                                Text("Registrarse")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    // Oculta el diálogo
                                    showDialog = false
                                }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

