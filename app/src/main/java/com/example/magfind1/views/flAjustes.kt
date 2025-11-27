package com.example.magfind1.views

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind.components.fResetPasswordDialog
import com.example.magfind1.RetrofitClient
import com.example.magfind1.SessionManager
import com.example.magfind1.apis.AuthRepository
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fAjustesView(navController: NavController, themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val activity = context as? Activity ?: return
    val scope = rememberCoroutineScope()
    val session = remember { SessionManager(context) }

    // ESTADOS
    var notificationsEnabled by remember { mutableStateOf(true) }
    val darkModeEnabled by themeViewModel.isDarkMode.collectAsState()

    val backgroundColor = if (darkModeEnabled) Color(0xFF121212) else Color.White
    val accentColor = if (darkModeEnabled) Color(0xFF90CAF9) else Color(0xFF1976D2)
    val textColor = if (darkModeEnabled) Color.White else Color.DarkGray

    val api = RetrofitClient.instance
    var gmailEmail by remember { mutableStateOf<String?>(null) }
    var loadingGmail by remember { mutableStateOf(true) }

    var showConfirmDialog by remember { mutableStateOf(false) }

    val token = session.getToken() ?: ""
    val uriHandler = LocalUriHandler.current

    // OLVIDÉ CONTRASEÑA
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotLoading by remember { mutableStateOf(false) }
    val repo = remember { AuthRepository() }

    // ------------------------------
    // CARGAR ESTADO DE GMAIL
    // ------------------------------
    LaunchedEffect(Unit) {
        try {
            if (token.isNotEmpty()) {
                val res = api.getGmailStatus(token)
                gmailEmail = res["email"] as? String
            }
        } catch (e: Exception) {
            gmailEmail = null
        } finally {
            loadingGmail = false
        }
    }

    // ------------------------------
    // UI PRINCIPAL
    // ------------------------------
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {

        fPlantilla(
            title = "Ajustes",
            navController = navController,
            themeViewModel = themeViewModel,
            drawerItems = listOf(
                "Home" to { navController.navigate("Home") },
                "Correos" to { navController.navigate("CorreosCat") },
                "Categorías" to { navController.navigate("Categorias") },
                "Mi Cuenta" to { navController.navigate("MiCuenta") },
                "Suscripción" to { navController.navigate("Suscripcion") },
                "Ajustes" to { navController.navigate("Ajustes") },
            )
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                //----------------------
                // PREFERENCIAS
                //----------------------
                Text(
                    "Preferencias",
                    fontSize = 22.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )

                fSettingToggle("Notificaciones", notificationsEnabled, textColor, accentColor) {
                    notificationsEnabled = it
                }

                fSettingToggle("Modo oscuro", darkModeEnabled, textColor, accentColor) {
                    themeViewModel.toggleDarkMode(it)
                }

                Divider(color = accentColor.copy(alpha = 0.6f))

                //----------------------
                // PRIVACIDAD
                //----------------------
                Text(
                    "Privacidad",
                    fontSize = 22.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )

                fSettingAction("Cambiar contraseña", accentColor) {
                    showForgotDialog = true
                }

                fSettingAction("Política de privacidad", accentColor) {
                    uriHandler.openUri("https://magfind.xyz/politica")
                }

                fSettingAction("Términos y condiciones", accentColor) {
                    uriHandler.openUri("https://magfind.xyz/terminos")
                }

                fSettingAction("Eliminar cuenta", accentColor) {}

                //----------------------
                // CUENTA GMAIL
                //----------------------
                Text(
                    "Cuenta de Gmail",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                if (loadingGmail) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(28.dp),
                        color = accentColor
                    )
                } else if (gmailEmail == null) {

                    Button(
                        onClick = {
                            val jwt = session.getToken() ?: ""
                            if (jwt.isNotEmpty()) openGoogleOAuth(context, jwt)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                    ) {
                        Text("Conectar con Gmail", color = Color.White)
                    }

                } else {
                    Text("Conectado como:", fontSize = 16.sp)
                    Text(
                        gmailEmail!!,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )

                    OutlinedButton(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Desconectar Gmail")
                    }
                }
            }
        }
    }

    // ======================================================
    // POPUP CONFIRMACIÓN DESVINCULAR GMAIL
    // ======================================================
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Desvincular Gmail") },
            text = { Text("¿Seguro que deseas desconectar tu cuenta de Gmail?\nLos correos dejarán de sincronizarse.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    scope.launch {
                        try {
                            val jwt = session.getToken() ?: return@launch
                            api.disconnectGmail(jwt)
                            gmailEmail = null
                            Toast.makeText(context, "Cuenta de Gmail desconectada", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al desconectar Gmail", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Desvincular", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ======================================================
    // POPUP CAMBIAR CONTRASEÑA
    // ======================================================
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

// ======================================================
// COMPONENTES REUTILIZABLES
// ======================================================

@Composable
fun fSettingToggle(
    title: String,
    checked: Boolean,
    textColor: Color,
    accentColor: Color,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = textColor, fontSize = 18.sp)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.4f)
            )
        )
    }
}

@Composable
fun fSettingAction(title: String, accentColor: Color, onClick: () -> Unit) {
    Text(
        title,
        fontSize = 18.sp,
        color = accentColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    )
}

// ======================================================
// GOOGLE OAUTH
// ======================================================
fun openGoogleOAuth(context: Context, token: String) {
    val encodedState = URLEncoder.encode(token, "UTF-8")
    val clientId = "76794028126-h85vt3eva11286jjob5leq038mr61q6c.apps.googleusercontent.com"

    val url = "https://accounts.google.com/o/oauth2/v2/auth" +
            "?client_id=$clientId" +
            "&redirect_uri=https://api.magfind.xyz/gmail/callback" +
            "&response_type=code" +
            "&scope=email%20profile%20https://www.googleapis.com/auth/gmail.readonly" +
            "&access_type=offline" +
            "&prompt=consent" +
            "&state=$encodedState"

    val intent = CustomTabsIntent.Builder().build()
    intent.launchUrl(context, Uri.parse(url))
}
