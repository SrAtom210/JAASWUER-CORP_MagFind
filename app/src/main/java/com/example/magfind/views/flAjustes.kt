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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fAjustesView(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var autoSyncEnabled by remember { mutableStateOf(true) }

    fPlantilla(
        title = "Ajustes",
        drawerItems = listOf(
            "Home" to { navController.navigate("Home")},
            "Ajustes" to { navController.navigate("Ajustes")},
            "Categorías" to { navController.navigate("Categorias") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Mi Cuenta" to { navController.navigate("MiCuenta")},
            "Suscripcion" to { navController.navigate("Suscripcion")}
        )
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text("Preferencias", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))

                fSettingToggle("Notificaciones", notificationsEnabled) { notificationsEnabled = it }
                fSettingToggle("Modo oscuro", darkModeEnabled) { darkModeEnabled = it }
                fSettingToggle("Sincronización automática", autoSyncEnabled) { autoSyncEnabled = it }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Privacidad", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))

                fSettingAction("Cambiar contraseña") { /* acción futura */ }
                fSettingAction("Política de privacidad") { /* acción futura */ }
                fSettingAction("Eliminar cuenta") { /* acción futura */ }
            }
        }
    }

@Composable
fun fSettingToggle(title: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp)
        Switch(checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF1976D2)))
    }
}

@Composable
fun fSettingAction(title: String, onClick: () -> Unit) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = Color(0xFF1976D2),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    )
}
