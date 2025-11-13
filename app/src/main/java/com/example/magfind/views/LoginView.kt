package com.example.magfind.views

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.magfind.apis.AuthRepository
import com.example.magfind.SessionManager

/*@Composable
fun LoginView(navController: NavHostController, themeViewModel: ThemeViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estados Registro
    var showRegisterDialog by remember { mutableStateOf(false) }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regLoading by remember { mutableStateOf(false) }

    // Estados Olvidé Contraseña
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotLoading by remember { mutableStateOf(false) }

    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val backgroundColor = if (isDark) Color(0xFF121212) else Color.White
    val textColor = if (isDark) Color.White else Color.DarkGray
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { AuthRepository() }

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Box(modifier = Modifier.fillMaxSize().padding(top = 50.dp), contentAlignment = Alignment.TopCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth(0.8f)) {

                Image(painter = painterResource(R.drawable.magfind), contentDescription = "logo")
                val gradient = Brush.linearGradient(colors = listOf(Color(0xFF2196F3), Color(0xFF00BCD4)))
                Text(text = "MagFind", fontSize = 40.sp, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, brush = gradient), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                // Login Inputs
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Email", color = textColor) }, textStyle = LocalTextStyle.current.copy(color = textColor), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña", color = textColor) }, textStyle = LocalTextStyle.current.copy(color = textColor), visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

                TextButton(onClick = { showForgotDialog = true }, modifier = Modifier.fillMaxWidth().align(Alignment.End)) {
                    Text("Olvidé mi Contraseña", color = textColor, textAlign = TextAlign.Right)
                }

                Button(onClick = {
                    scope.launch {
                        try {
                            val token = repo.login(username, password)
                            if (token != null) {
                                SessionManager.token = token
                                SessionManager.username = username
                                Toast.makeText(context, "Bienvenido, $username", Toast.LENGTH_SHORT).show()
                                navController.navigate("Home") { popUpTo(0) }
                            } else {
                                Toast.makeText(context, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = accentColor)) {
                    Text("Iniciar sesión", color = if (isDark) Color.Black else Color.White)
                }
                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = { showRegisterDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrarse", color = textColor)
                }

                // --- DIÁLOGO DE REGISTRO (LÓGICA ACTUALIZADA) ---
                if (showRegisterDialog) {
                    AlertDialog(
                        onDismissRequest = { showRegisterDialog = false },
                        title = { Text(text = "Registro Rápido") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = regEmail, onValueChange = { regEmail = it }, label = { Text("Correo Electrónico") })
                                OutlinedTextField(value = regPassword, onValueChange = { regPassword = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation())
                                if (regLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (regEmail.isBlank() || regPassword.isBlank()) {
                                    Toast.makeText(context, "Campos requeridos.", Toast.LENGTH_SHORT).show()
                                } else {
                                    regLoading = true
                                    scope.launch {
                                        try {
                                            val nombreUsuario = regEmail.substringBefore('@').trim().lowercase()

                                            // 1. Llamamos a register. El backend crea el usuario Y envía el correo.
                                            val success = repo.register(nombreUsuario, regEmail, regPassword)

                                            if (success) {
                                                Toast.makeText(context, "Registro exitoso. Verifica tu correo.", Toast.LENGTH_LONG).show()
                                                showRegisterDialog = false
                                                // 2. Navegamos directo a verificar
                                                navController.navigate("VerifyCode/$regEmail")
                                            } else {
                                                Toast.makeText(context, "Error: El email ya existe o falló la conexión.", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            regLoading = false
                                        }
                                    }
                                }
                            }) { Text("Registrarse") }
                        },
                        dismissButton = { TextButton(onClick = { showRegisterDialog = false }) { Text("Cancelar") } }
                    )
                }

                // --- DIÁLOGO DE OLVIDÉ CONTRASEÑA ---
                if (showForgotDialog) {
                    AlertDialog(
                        onDismissRequest = { showForgotDialog = false },
                        title = { Text(text = "Recuperar Contraseña") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = forgotEmail, onValueChange = { forgotEmail = it }, label = { Text("Correo Electrónico") })
                                if (forgotLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (forgotEmail.isBlank()) {
                                    Toast.makeText(context, "El correo es requerido.", Toast.LENGTH_SHORT).show()
                                } else {
                                    forgotLoading = true
                                    scope.launch {
                                        try {
                                            val emailSent = repo.requestPasswordReset(forgotEmail)
                                            if (emailSent) {
                                                Toast.makeText(context, "Correo enviado.", Toast.LENGTH_SHORT).show()
                                                showForgotDialog = false
                                                navController.navigate("VerifyCode/$forgotEmail?isReset=true")
                                            } else {
                                                Toast.makeText(context, "Error: Correo no encontrado.", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            forgotLoading = false
                                        }
                                    }
                                }
                            }) { Text("Enviar") }
                        },
                        dismissButton = { TextButton(onClick = { showForgotDialog = false }) { Text("Cancelar") } }
                    )
                }
            }
        }
    }
}*/

@Composable
fun LoginView(navController: NavHostController, themeViewModel: ThemeViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    // Modo oscuro
    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val backgroundColor = if (isDark) Color(0xFF121212) else Color.White
    val textColor = if (isDark) Color.White else Color.DarkGray
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    // Nuevo estado para pestañas
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Iniciar Sesión", "Registrarse")

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { AuthRepository() }

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        // Centramos todo verticalmente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.magfind),
                contentDescription = "logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "MagFind",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pestañas
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = accentColor,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .fillMaxWidth(tabs.size.toFloat() / tabs.size) // se extiende todo
                            .height(3.dp),
                        color = accentColor
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) accentColor else textColor,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animación de swipe entre Login / Registro
            AnimatedContent(targetState = selectedTab, label = "") { target ->
                if (target == 0) {
                    // --- LOGIN ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Email", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(color = textColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contraseña", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(color = textColor),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        TextButton(
                            onClick = { showForgotDialog = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                "Olvidé mi contraseña",
                                color = accentColor,
                                textAlign = TextAlign.Right
                            )
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val token = repo.login(username, password)
                                        if (token != null) {
                                            SessionManager.token = token
                                            SessionManager.username = username
                                            Toast.makeText(context, "Bienvenido, $username", Toast.LENGTH_SHORT).show()
                                            navController.navigate("Home") { popUpTo(0) }
                                        } else {
                                            Toast.makeText(context, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("Iniciar sesión", color = Color.White)
                        }
                    }
                } else {
                    // --- REGISTRO ---
                    var regEmail by remember { mutableStateOf("") }
                    var regPassword by remember { mutableStateOf("") }
                    var regLoading by remember { mutableStateOf(false) }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Correo electrónico", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(color = textColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Contraseña", color = textColor) },
                            textStyle = LocalTextStyle.current.copy(color = textColor),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (regEmail.isBlank() || regPassword.isBlank()) {
                                    Toast.makeText(context, "Campos requeridos.", Toast.LENGTH_SHORT).show()
                                } else {
                                    regLoading = true
                                    scope.launch {
                                        try {
                                            val nombreUsuario = regEmail.substringBefore('@').trim().lowercase()
                                            val success = repo.register(nombreUsuario, regEmail, regPassword)
                                            if (success) {
                                                Toast.makeText(context, "Registro exitoso. Verifica tu correo.", Toast.LENGTH_LONG).show()
                                                navController.navigate("VerifyCode/$regEmail")
                                            } else {
                                                Toast.makeText(context, "Error: El email ya existe o falló la conexión.", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            regLoading = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            if (regLoading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Registrarse", color = Color.White)
                            }
                        }
                    }
                }
            }
            if (showForgotDialog) {
                var forgotEmail by remember { mutableStateOf("") }
                var forgotLoading by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = { showForgotDialog = false },
                    title = { Text(text = "Recuperar Contraseña") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = forgotEmail,
                                onValueChange = { forgotEmail = it },
                                label = { Text("Correo Electrónico") }
                            )
                            if (forgotLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (forgotEmail.isBlank()) {
                                Toast.makeText(context, "El correo es requerido.", Toast.LENGTH_SHORT).show()
                            } else {
                                forgotLoading = true
                                scope.launch {
                                    try {
                                        val emailSent = repo.requestPasswordReset(forgotEmail)
                                        if (emailSent) {
                                            Toast.makeText(context, "Correo enviado.", Toast.LENGTH_SHORT).show()
                                            showForgotDialog = false
                                            navController.navigate("VerifyCode/$forgotEmail?isReset=true")
                                        } else {
                                            Toast.makeText(context, "Error: Correo no encontrado.", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    } finally {
                                        forgotLoading = false
                                    }
                                }
                            }
                        }) {
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

//Comentario unicamente para hacer comit and push :)


