package com.example.magfind.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind.components.fDrawerItem
import com.example.magfind.components.fPlantilla
import com.example.magfind.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fAjustesView(navController: NavController, themeViewModel: ThemeViewModel) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoSyncEnabled by remember { mutableStateOf(true) }

    val darkModeEnabled by themeViewModel.isDarkMode.collectAsState()

    val backgroundColor = if (darkModeEnabled) Color(0xFF121212) else Color.White
    val textColor = if (darkModeEnabled) Color.White else Color.Black
    val accentColor = if (darkModeEnabled) Color(0xFF90CAF9) else Color(0xFF1976D2)

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
                Text(
                    "Preferencias",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )

                fSettingToggle("Notificaciones", notificationsEnabled, textColor, accentColor) {
                    notificationsEnabled = it
                }

                //  este switch controla el modo oscuro global
                fSettingToggle("Modo oscuro", darkModeEnabled, textColor, accentColor) {
                    themeViewModel.toggleDarkMode(it)
                }

                fSettingToggle("Sincronización automática", autoSyncEnabled, textColor, accentColor) {
                    autoSyncEnabled = it
                }

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = accentColor.copy(alpha = 0.5f)
                )

                Text("Privacidad", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = accentColor)

                fSettingAction("Cambiar contraseña", accentColor, textColor) { }
                fSettingAction("Política de privacidad", accentColor, textColor) { }
                fSettingAction("Eliminar cuenta", accentColor, textColor) { }

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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, color = textColor)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.4f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}

@Composable
fun fSettingAction(title: String, accentColor: Color, textColor: Color, onClick: () -> Unit) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = accentColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    )
}
