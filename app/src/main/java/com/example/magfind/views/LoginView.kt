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

    // Estados para el diálogo de Registro
    var showRegisterDialog by remember { mutableStateOf(false) }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regLoading by remember { mutableStateOf(false) }

    // Estados para el diálogo de Olvidé Contraseña
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotLoading by remember { mutableStateOf(false) }

    // Estado para controlar la visibilidad del diálogo de registro
    var showDialog by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }

    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val backgroundColor = if (isDark) Color(0xFF121212) else Color.White
    val textColor = if (isDark) Color.White else Color.DarkGray
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = com.example.magfind.apis.AuthRepository()

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
                // ... (tu columna existente) ...
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
                    modifier = Modifier.fillMaxWidth()
                )

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

                // --- MODIFICADO: Botón Olvidé Contraseña ---
                TextButton(
                    onClick = { showForgotDialog = true }, // Abre el nuevo diálogo
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End)
                ) {
                    Text("Olvidé mi Contraseña", color = textColor, textAlign = TextAlign.Right)
                }

                // --- Botón de login (sin cambios en la lógica de click) ---
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
                                        "Credenciales incorrectas o cuenta no verificada.",
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
                    onClick = { /* Lógica de Gmail */ },
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
                    }
                }

                // --- MODIFICADO: Botón Registrarse ---
                TextButton(
                    onClick = { showRegisterDialog = true }, // Abre el diálogo de registro
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse", color = textColor)
                }


                // --- DIÁLOGO DE REGISTRO (MODIFICADO) ---
                if (showRegisterDialog) {
                    AlertDialog(
                        onDismissRequest = { showRegisterDialog = false },
                        title = { Text(text = "Registro Rápido") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = regEmail,
                                    onValueChange = { regEmail = it },
                                    label = { Text("Correo Electrónico") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = regPassword,
                                    onValueChange = { regPassword = it },
                                    label = { Text("Contraseña") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true
                                )
                                if (regLoading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (regEmail.isBlank() || regPassword.isBlank()) {
                                        Toast.makeText(context, "Ambos campos son requeridos.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    regLoading = true

                                    // (Aquí iría tu lógica de palabras prohibidas, etc.)
                                    // ...

                                    scope.launch {
                                        try {
                                            // Paso 1: Intentar registrar en el backend
                                            val nombreUsuario = regEmail.substringBefore('@').trim().lowercase()
                                            val success = repo.register(nombreUsuario, regEmail, regPassword)

                                            if (success) {
                                                // Paso 2: Si el registro es exitoso, solicitar el correo de verificación
                                                val emailSent = repo.requestVerificationCode(regEmail)

                                                if (emailSent) {
                                                    Toast.makeText(context, "Registro exitoso. Revisa tu correo para verificar.", Toast.LENGTH_LONG).show()
                                                    showRegisterDialog = false
                                                    // NAVEGAMOS A LA VISTA DE VERIFICACIÓN
                                                    navController.navigate("VerifyCode/$regEmail")
                                                } else {
                                                    Toast.makeText(context, "Registro exitoso, pero falló el envío del correo.", Toast.LENGTH_LONG).show()
                                                }

                                            } else {
                                                Toast.makeText(context, "Error al registrar usuario.", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            regLoading = false
                                        }
                                    }
                                }
                            ) {
                                Text("Registrarse")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRegisterDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                // --- NUEVO: DIÁLOGO DE OLVIDÉ CONTRASEÑA ---
                if (showForgotDialog) {
                    AlertDialog(
                        onDismissRequest = { showForgotDialog = false },
                        title = { Text(text = "Recuperar Contraseña") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Ingresa tu correo electrónico y te enviaremos un código de recuperación.")
                                OutlinedTextField(
                                    value = forgotEmail,
                                    onValueChange = { forgotEmail = it },
                                    label = { Text("Correo Electrónico") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true
                                )
                                if (forgotLoading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (forgotEmail.isBlank()) {
                                        Toast.makeText(context, "El correo es requerido.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    forgotLoading = true
                                    scope.launch {
                                        try {
                                            // Paso 1: Solicitar al backend el correo de reseteo
                                            val emailSent = repo.requestPasswordReset(forgotEmail)

                                            if (emailSent) {
                                                Toast.makeText(context, "Correo de recuperación enviado (revisa SPAM).", Toast.LENGTH_SHORT).show()
                                                showForgotDialog = false
                                                // NAVEGAMOS A LA VISTA DE VERIFICACIÓN (en modo reseteo)
                                                navController.navigate("VerifyCode/$forgotEmail?isReset=true")
                                            } else {
                                                Toast.makeText(context, "Error al enviar el correo. ¿El usuario existe?", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            forgotLoading = false
                                        }
                                    }
                                }
                            ) {
                                Text("Enviar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showForgotDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

            }
        }
    }
}
