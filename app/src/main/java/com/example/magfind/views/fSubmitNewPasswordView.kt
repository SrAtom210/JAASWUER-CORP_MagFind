package com.example.magfind.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind.apis.AuthRepository
import com.example.magfind.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla final para que el usuario ingrese su NUEVA contraseña.
 */
@Composable
fun SubmitNewPasswordView(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    email: String,
    code: String
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { AuthRepository() }

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
                text = "Crear Nueva Contraseña",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Nueva Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = accentColor)
            } else {
                Button(
                    onClick = {
                        if (password.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(context, "Ambos campos son requeridos.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (password != confirmPassword) {
                            Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            try {
                                val success = repo.submitPasswordReset(email, code, password)
                                if (success) {
                                    Toast.makeText(context, "Contraseña actualizada. Inicia sesión.", Toast.LENGTH_LONG).show()
                                    navController.navigate("Login") { popUpTo(0) }
                                } else {
                                    Toast.makeText(context, "Error al actualizar. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(
                        text = "Guardar Contraseña",
                        color = if (isDark) Color.Black else Color.White
                    )
                }
            }
        }
    }
}
/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SubmitNewPasswordViewPreviewStandalone() {
    // --- valores estáticos para el preview ---
    val isDark = false
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
                text = "Crear Nueva Contraseña",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = "••••••••",
                onValueChange = {},
                label = { Text("Nueva Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "••••••••",
                onValueChange = {},
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* sin acción en preview */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text(
                    text = "Guardar Contraseña",
                    color = if (isDark) Color.Black else Color.White
                )
            }
        }
    }
}*/