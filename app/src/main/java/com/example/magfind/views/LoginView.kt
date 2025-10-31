package com.example.magfind.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.magfind.R
import com.example.magfind.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import com.example.magfind.RetrofitClient
import com.example.magfind.apis.FCuentaApi

@Composable
fun LoginView(navController: NavHostController,themeViewModel: ThemeViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado para controlar la visibilidad del diálogo de registro
    var showDialog by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }

    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val backgroundColor = if (isDark) Color(0xFF121212) else Color.White
    val textColor = if (isDark) Color.White else Color.DarkGray
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    //Fondo dinamico
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
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
                )
                // Campo de usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario", color = textColor) },
                    textStyle = LocalTextStyle.current.copy(color = textColor),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.DarkGray,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.DarkGray,
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo de contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = textColor) },
                    textStyle = LocalTextStyle.current.copy(color = textColor),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.DarkGray,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.DarkGray,
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Gray
                    ),
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
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                val repo = com.example.magfind.apis.AuthRepository()

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val token = repo.login(username, password)

                                if (token != null) {
                                    com.example.magfind.SessionManager.token = token
                                    com.example.magfind.SessionManager.username = username

                                    Toast.makeText(
                                        context,
                                        "Inicio de sesión exitoso. Bienvenido, $username",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.navigate("Home")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Credenciales incorrectas. Verifica usuario y contraseña.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error al conectar con el servidor: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                e.printStackTrace()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Iniciar sesión", color = if (isDark) Color.Black else Color.White)
                }

                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, Color.Black)),
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
                    onClick = {showDialog = true},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse", color = textColor)
                }
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
                            val context = LocalContext.current
                            val scope = rememberCoroutineScope()
                            val repo = com.example.magfind.apis.AuthRepository()

                            val palabrasProhibidas = ArrayDeque(listOf("admin", "root", "soporte", "test", "usuario"))

                            Button(
                                onClick = {
                                    scope.launch {
                                        val nombreUsuario = email.substringBefore('@').trim().lowercase()

                                        if (nombreUsuario.isEmpty()) {
                                            Toast.makeText(context, "El nombre de usuario no puede estar vacío.", Toast.LENGTH_LONG).show()
                                            return@launch
                                        }

                                        if (palabrasProhibidas.any { it.equals(nombreUsuario, ignoreCase = true) }) {
                                            Toast.makeText(context, "La palabra '$nombreUsuario' está prohibida como nombre de usuario.", Toast.LENGTH_LONG).show()
                                            return@launch
                                        }

                                        try {
                                            val api = RetrofitClient.retrofit.create(FCuentaApi::class.java)
                                            val response = api.verificarEmail(email)
                                            if (response.data != null) {
                                                Toast.makeText(context, "El correo ya está registrado.", Toast.LENGTH_LONG).show()
                                                return@launch
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error al verificar duplicado: ${e.message}", Toast.LENGTH_LONG).show()
                                            e.printStackTrace()
                                            return@launch
                                        }

                                        try {
                                            val success = repo.register(nombreUsuario, email, nuevaPassword)
                                            if (success) {
                                                Toast.makeText(context, "Registro exitoso. Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                                                showDialog = false
                                                navController.navigate("home")
                                            } else {
                                                Toast.makeText(context, "Error al registrar usuario. Inténtalo nuevamente.", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error al conectar con el servidor: ${e.message}", Toast.LENGTH_LONG).show()
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            ) {
                                Text("Registrarse")
                            }
                        }
                        ,
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