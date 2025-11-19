package com.example.magfind1.views

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.magfind1.RetrofitClient
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.viewmodels.GmailViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fAjustesView(navController: NavController, themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val activity = context as? Activity ?: return
    val scope = rememberCoroutineScope()

    // GMAIL VIEWMODEL
    val gmailViewModel: GmailViewModel = viewModel()
    val gmailConnected by gmailViewModel.connected.collectAsState()
    // val gmailEmail by gmailViewModel.connectedEmail.collectAsState()

    // PREFERENCIAS LOCALES
    val session = remember { SessionManager(context) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoSyncEnabled by remember { mutableStateOf(session.getAutoSyncEnabled()) }

    val darkModeEnabled by themeViewModel.isDarkMode.collectAsState()

    val backgroundColor = if (darkModeEnabled) Color(0xFF121212) else Color.White
    val accentColor = if (darkModeEnabled) Color(0xFF90CAF9) else Color(0xFF1976D2)
    val textColor = if (darkModeEnabled) Color.White else Color.DarkGray

    val api = RetrofitClient.instance
    var gmailEmail by remember { mutableStateOf<String?>(null) }
    var loadingGmail by remember { mutableStateOf(true) }

    // TOKEN DEL USUARIO
    val token = session.getToken() ?: ""

    // CONSULTA EL ESTADO DE GMAIL SOLO UNA VEZ
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
                "Ajustes" to { navController.navigate("Ajustes") },
                "Categorías" to { navController.navigate("Categorias") },
                "Correos" to { navController.navigate("CorreosCat") },
                "Mi Cuenta" to { navController.navigate("MiCuenta") },
                "Suscripción" to { navController.navigate("Suscripcion") }
            )
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                // ------------------------
                // SECCIÓN PREFERENCIAS
                // ------------------------
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

                fSettingToggle(
                    "Sincronización automática",
                    autoSyncEnabled,
                    textColor,
                    accentColor
                ) { state ->
                    autoSyncEnabled = state
                    session.saveAutoSyncEnabled(state)

                    if (state && gmailConnected) {
                        gmailViewModel.sync(token)
                        Toast.makeText(
                            context,
                            "Sincronización automática activada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                Divider(color = accentColor.copy(alpha = 0.6f))

                // ------------------------
                // PRIVACIDAD
                // ------------------------
                Text(
                    "Privacidad",
                    fontSize = 22.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )

                fSettingAction("Cambiar contraseña", accentColor) {}
                fSettingAction("Política de privacidad", accentColor) {}
                fSettingAction("Eliminar cuenta", accentColor) {}

                // ------------------------
                // GMAIL
                // ------------------------
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Cuenta de Gmail",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                )

                // Cargando estado
                if (loadingGmail) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(28.dp),
                        color = Color(0xFF1976D2)
                    )
                } else {

                    if (gmailEmail == null) {

                        //  NO conectado
                        Button(
                            onClick = {
                                val jwt = session.getToken() ?: ""
                                if (jwt.isNotEmpty()) {
                                    openGoogleOAuth(context, jwt)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                        ) {
                            Text("Conectar con Gmail", color = Color.White)
                        }

                    } else {

                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Conectado como:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = gmailEmail!!,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
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
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Desconectar Gmail")
                            }

                        }
                    }
                }

            }
        }
    }
}

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




