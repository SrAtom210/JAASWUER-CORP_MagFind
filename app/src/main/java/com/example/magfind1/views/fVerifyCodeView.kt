package com.example.magfind.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
/*import androidx.compose.ui.tooling.preview.Preview*/
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.SessionManager
import com.example.magfind1.apis.AuthRepository
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla para que el usuario ingrese el código de 6 dígitos
 * recibido por correo.
 *
 * @param email El email al que se envió el código.
 * @param isReset Flow: 'false' para verificación de registro, 'true' para reseteo de contraseña.
 */
@Composable
fun VerifyCodeView(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    email: String,
    isReset: Boolean = false
) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { AuthRepository() }

    // --- NUEVA LÓGICA DE COOLDOWN ---
    val totalCooldownSeconds = 60
    var isTimerRunning by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(totalCooldownSeconds) }

    // Este "LaunchedEffect" es el motor del temporizador.
    // Se dispara CADA VEZ que 'isTimerRunning' cambia a 'true'.
    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            // Mientras el contador sea mayor que 0...
            while (countdown > 0) {
                delay(1000L) // ...espera 1 segundo
                countdown--  // ...resta 1 al contador
            }
            // Cuando el bucle termina, reseteamos todo
            isTimerRunning = false
            countdown = totalCooldownSeconds
        }
    }
    // --- FIN DE LÓGICA DE COOLDOWN ---


    // Colores dinámicos
    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val backgroundColor = if (isDark) Color(0xFF121212) else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Verificar Correo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Enviamos un código de 6 dígitos a:",
                fontSize = 16.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = email,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { if (it.length <= 6) code = it },
                label = { Text("Código de 6 dígitos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = accentColor)
            } else {
                Button(
                    onClick = {
                        if (code.length == 6) {
                            isLoading = true
                            scope.launch {
                                try {
                                    if (isReset) {
                                        // Flujo 1: Reseteo de Contraseña
                                        // Navegamos a la siguiente pantalla
                                        isLoading = false
                                        // El código se valida en la siguiente pantalla
                                        navController.navigate("SubmitNewPassword/$email/$code")
                                    } else {
                                        // Flujo 2: Verificación de Registro
                                        val result = repo.verifyCode(email, code)
                                        if (result != null) {

                                            val sessionManager = SessionManager(context)
                                            sessionManager.saveSession(
                                                userId = result.id_usuario,
                                                token = result.token
                                            )

                                            Toast.makeText(context, "¡Verificación exitosa!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("Home") { popUpTo(0) }
                                        } else {
                                            Toast.makeText(context, "Código incorrecto.", Toast.LENGTH_SHORT).show()
                                        }

                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    if (!isReset) isLoading = false
                                }
                            }
                        } else {
                            Toast.makeText(context, "El código debe tener 6 dígitos.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(
                        text = if (isReset) "Siguiente" else "Verificar y Entrar",
                        color = if (isDark) Color.Black else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTÓN DE REENVIAR MODIFICADO ---
            TextButton(
                // 1. El botón se deshabilita si el temporizador está corriendo
                enabled = !isTimerRunning,
                onClick = {
                    // 2. Iniciamos el temporizador
                    isTimerRunning = true

                    // 3. Ejecutamos la lógica de red
                    scope.launch {
                        val success = if (isReset) {
                            repo.requestPasswordReset(email)
                        } else {
                            repo.requestVerificationCode(email)
                        }

                        if (success) {
                            Toast.makeText(context, "Nuevo código enviado.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al reenviar el código.", Toast.LENGTH_SHORT).show()
                            // Si falla la API, reseteamos el timer para que pueda reintentar
                            isTimerRunning = false
                            countdown = totalCooldownSeconds
                        }
                    }
                }
            ) {
                // 4. El texto cambia según el estado del temporizador
                Text(
                    text = if (isTimerRunning) {
                        "Reenviar en ${countdown}s"
                    } else {
                        "Reenviar código"
                    },
                    // El color se atenuará automáticamente por el 'enabled = false'
                    color = accentColor
                )
            }
            // --- FIN DEL BOTÓN MODIFICADO ---
        }
    }
}

