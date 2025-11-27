package com.example.magfind1.views

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.magfind.components.fResetPasswordDialog
import com.example.magfind1.R
import com.example.magfind1.SessionManager
import com.example.magfind1.apis.AuthRepository
import com.example.magfind1.google.GoogleAuthManager
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavHostController, themeViewModel: ThemeViewModel) {

    // ------------------------- CAMPOS LOGIN -------------------------
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ------------------------- CAMPOS REGISTRO (FUERA de AnimatedContent) -------------------------
    var regEmail by remember { mutableStateOf("") }
    var regPass by remember { mutableStateOf("") }
    var regLoading by remember { mutableStateOf(false) }

    // ------------------------- FORGOT PASSWORD -------------------------
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotLoading by remember { mutableStateOf(false) }

    // ------------------------- TABS -------------------------
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Iniciar Sesión", "Registrarse")

    // ------------------------- THEME -------------------------
    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val backgroundColor = if (isDark) Color(0xFF121212) else Color.White
    val textColor = if (isDark) Color.White else Color.DarkGray
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    // ------------------------- CONTEXT -------------------------
    val context = LocalContext.current
    val activity = context as Activity
    val repo = remember { AuthRepository() }
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // ------------------------- UI -------------------------
    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp)
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(R.drawable.magfind),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "MagFind",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    color = accentColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ------------------------- TABS -------------------------
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = accentColor,
                    indicator = { pos ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .tabIndicatorOffset(pos[selectedTab])
                                .height(3.dp),
                            color = accentColor
                        )
                    }
                ) {
                    tabs.forEachIndexed { i, title ->
                        Tab(
                            selected = selectedTab == i,
                            onClick = { selectedTab = i },
                            text = {
                                Text(
                                    title,
                                    color = if (selectedTab == i) accentColor else textColor,
                                    fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ------------------------- CONTENIDO TABS -------------------------
                AnimatedContent(selectedTab) { tab ->

                    // ======================================================
                    //                     LOGIN
                    // ======================================================
                    if (tab == 0) {

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
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

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = { showForgotDialog = true }) {
                                    Text("Olvidé mi contraseña", color = accentColor)
                                }
                            }

                            // ------------------------- LOGIN NORMAL -------------------------
                            Button(
                                onClick = {
                                    scope.launch {
                                        val res = repo.login(username, password)

                                        if (res != null) {
                                            val derivedName = username.substringBefore("@")

                                            session.saveSession(
                                                res.id_usuario,
                                                res.token,
                                                derivedName,
                                                username,
                                                res.plan
                                            )

                                            Toast.makeText(context, "Bienvenido $derivedName", Toast.LENGTH_SHORT).show()
                                            navController.navigate("Home") { popUpTo(0) }
                                        } else {
                                            Toast.makeText(context, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(accentColor)
                            ) {
                                Text("Iniciar sesión", color = if (isDark) Color.Black else Color.White)
                            }

                            // ------------------------- LOGIN GOOGLE -------------------------
                            Button(
                                onClick = {
                                    scope.launch {
                                        val googleToken = GoogleAuthManager.signIn(activity)

                                        if (googleToken != null) {
                                            val googleRes = repo.loginGoogle(googleToken)
                                            if (googleRes != null) {

                                                val emailGoogle =
                                                    GoogleAuthManager.lastEmail ?: "google@unknown.com"
                                                val nameGoogle = emailGoogle.substringBefore("@")

                                                session.saveSession(
                                                    googleRes.id_usuario,
                                                    googleRes.token,
                                                    nameGoogle,
                                                    emailGoogle,
                                                    googleRes.plan
                                                )

                                                Toast.makeText(context, "Inicio con Google exitoso", Toast.LENGTH_SHORT).show()
                                                navController.navigate("Home") { popUpTo(0) }
                                            }
                                        } else {
                                            Toast.makeText(context, "Cancelado", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(Color.White)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.google_logo),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Continuar con Google", color = Color.Black)
                            }
                        }
                    }

                    // ======================================================
                    //                     REGISTRO
                    // ======================================================
                    else {

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = { regEmail = it },
                                label = { Text("Correo electrónico", color = textColor) },
                                textStyle = LocalTextStyle.current.copy(color = textColor),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = regPass,
                                onValueChange = { regPass = it },
                                label = { Text("Contraseña", color = textColor) },
                                textStyle = LocalTextStyle.current.copy(color = textColor),
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (regEmail.isBlank() || regPass.isBlank()) {
                                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    regLoading = true

                                    scope.launch {
                                        val nameDerived = regEmail.substringBefore("@")
                                        val ok = repo.register(nameDerived, regEmail, regPass)

                                        if (ok) {
                                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                                            navController.navigate("VerifyCode/$regEmail")
                                        } else {
                                            Toast.makeText(context, "El correo ya existe", Toast.LENGTH_LONG).show()
                                        }

                                        regLoading = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(accentColor)
                            ) {
                                if (regLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Registrarse", color = if (isDark) Color.Black else Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // ------------------------- DIALOG OLVIDÉ CONTRASEÑA -------------------------
            if (showForgotDialog) {
                fResetPasswordDialog(
                    themeViewModel = themeViewModel,
                    isLoading = forgotLoading,
                    onDismissRequest = { showForgotDialog = false },
                    onSendClick = { email ->
                        if (email.isBlank()) {
                            Toast.makeText(context, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                        } else {
                            forgotLoading = true
                            scope.launch {
                                val sent = repo.requestPasswordReset(email)
                                if (sent) {
                                    Toast.makeText(context, "Correo enviado", Toast.LENGTH_SHORT).show()
                                    navController.navigate("VerifyCode/$email?isReset=true")
                                    showForgotDialog = false
                                } else {
                                    Toast.makeText(context, "Correo no encontrado", Toast.LENGTH_SHORT).show()
                                }
                                forgotLoading = false
                            }
                        }
                    }
                )
            }
        }
    }
}
