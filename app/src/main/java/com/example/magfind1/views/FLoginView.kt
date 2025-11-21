package com.example.magfind1.views

// Credential Manager + Google ID
import android.R.attr.text
import android.app.Activity
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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavHostController
import com.example.magfind.components.fResetPasswordDialog
import com.example.magfind1.SessionManager
import com.example.magfind1.apis.AuthRepository
import com.example.magfind1.ui.theme.ThemeViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch
import com.example.magfind1.R
import com.example.magfind1.google.GoogleAuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavHostController, themeViewModel: ThemeViewModel) {

    // Campos login
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Forgot password
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotLoading by remember { mutableStateOf(false) }

    // Tabs
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Iniciar Sesión", "Registrarse")

    // Tema
    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val backgroundColor = if (isDark) Color(0xFF121212) else Color.White
    val textColor = if (isDark) Color.White else Color.DarkGray
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    // General
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { AuthRepository() }
    val session = remember { SessionManager(context) }
    val activity = context as Activity

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Logo
                Image(
                    painter = painterResource(R.drawable.magfind),
                    contentDescription = "Logo",
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

                // ------------------ TABS ------------------
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
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    color = if (index == selectedTab) accentColor else textColor,
                                    fontWeight = if (index == selectedTab) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ================= LOGIN / REGISTRO =================
                AnimatedContent(selectedTab) { tab ->

                    // -------- LOGIN --------
                    if (tab == 0) {

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

                            // ---- Olvidé contraseña centrado ----
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = { showForgotDialog = true }) {
                                    Text("Olvidé mi contraseña", color = accentColor)
                                }
                            }

                            // ---- LOGIN NORMAL ----
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

                            Spacer(modifier = Modifier.height(12.dp))

                            // ---- LOGIN CON GOOGLE ----
                            Button(
                                onClick = {
                                    scope.launch {
                                        val googleToken = GoogleAuthManager.signIn(activity)

                                        if (googleToken != null) {

                                            val googleRes = repo.loginGoogle(googleToken)

                                            if (googleRes != null) {

                                                // Tu backend no regresa email/foto todavía
                                                session.saveSession(
                                                    googleRes.id_usuario,
                                                    googleRes.token
                                                )

                                                Toast.makeText(context, "Inicio con Google exitoso", Toast.LENGTH_SHORT).show()
                                                navController.navigate("Home") { popUpTo(0) }

                                            } else {
                                                Toast.makeText(context, "Error al iniciar con Google", Toast.LENGTH_SHORT).show()
                                            }

                                        } else {
                                            Toast.makeText(context, "Inicio cancelado", Toast.LENGTH_SHORT).show()
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

                    // -------- REGISTRO --------
                    else {

                        var regEmail by remember { mutableStateOf("") }
                        var regPassword by remember { mutableStateOf("") }
                        var loading by remember { mutableStateOf(false) }

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
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (regEmail.isBlank() || regPassword.isBlank()) {
                                        Toast.makeText(context, "Todos los campos son requeridos.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    loading = true

                                    scope.launch {
                                        val userName = regEmail.substringBefore("@")

                                        val ok = repo.register(
                                            userName,
                                            regEmail,
                                            regPassword
                                        )

                                        if (ok) {
                                            Toast.makeText(context, "Registro exitoso. Verifica tu correo.", Toast.LENGTH_LONG).show()
                                            navController.navigate("VerifyCode/$regEmail")
                                        } else {
                                            Toast.makeText(context, "El correo ya está registrado.", Toast.LENGTH_LONG).show()
                                        }

                                        loading = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(accentColor)
                            ) {
                                if (loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = if (isDark) Color.Black else Color.White,
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

            // ---------------------- DIÁLOGO OLVIDÉ CONTRASEÑA ----------------------
            if (showForgotDialog) {
                fResetPasswordDialog(
                    themeViewModel = themeViewModel,
                    isLoading = forgotLoading,
                    onDismissRequest = { showForgotDialog = false },
                    onSendClick = { email ->

                        if (email.isBlank()) {
                            Toast.makeText(context, "Ingresa un correo válido.", Toast.LENGTH_SHORT).show()
                            return@fResetPasswordDialog
                        }

                        forgotLoading = true
                        scope.launch {
                            val sent = repo.requestPasswordReset(email)
                            if (sent) {
                                Toast.makeText(context, "Correo enviado.", Toast.LENGTH_SHORT).show()
                                navController.navigate("VerifyCode/$email?isReset=true")
                                showForgotDialog = false
                            } else {
                                Toast.makeText(context, "Correo no encontrado.", Toast.LENGTH_LONG).show()
                            }
                            forgotLoading = false
                        }
                    }
                )
            }
        }
    }
}
