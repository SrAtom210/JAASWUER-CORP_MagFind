package com.example.magfind1.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.ui.theme.ThemeViewModel

@Composable
fun fAyudaView(navController: NavController, themeViewModel: ThemeViewModel) {

    fPlantilla(
        title = "Ayuda",
        navController = navController,
        themeViewModel = themeViewModel,
        drawerItems = listOf(
            "Home" to { navController.navigate("Home") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Categorías" to { navController.navigate("Categorias") },
            "Mi Cuenta" to { navController.navigate("MiCuenta") },
            "Suscripción" to { navController.navigate("Suscripcion") },
            "Ajustes" to { navController.navigate("Ajustes") },
            "Ayuda" to { }
        )
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // --- INTRO ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Help, contentDescription = null, tint = Color(0xFF1976D2))
                Spacer(Modifier.width(8.dp))
                Text("Centro de Ayuda", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            HelpSection(
                "¿Qué es MagFind?",
                "MagFind te ayuda a clasificar tus correos automáticamente usando Inteligencia Artificial, " +
                        "creando categorías inteligentes y organizando tu bandeja."
            )

            HelpSection(
                "¿Qué significa cada plan?",
                "• Essential: Gratis, IA básica, 5 categorías\n" +
                        "• Plus: Clasificaciones extendidas y sin anuncios\n" +
                        "• Platinum: Más cuentas, IA fuerte\n" +
                        "• Business: Ilimitado y herramientas avanzadas"
            )

            HelpSection(
                "¿Cómo funciona la IA diaria?",
                "Tu plan define cuántas clasificaciones puedes realizar por día. Una vez llegas al límite, " +
                        "la IA se recarga automáticamente después del tiempo indicado."
            )

            HelpSection(
                "¿Cómo cambio mi foto y nombre?",
                "Ve a 'Mi Cuenta' → Editar perfil. Puedes subir una imagen desde tu galería y cambiar tu nombre."
            )

            HelpSection(
                "¿Cómo funcionan las categorías?",
                "La app clasifica tus correos automáticamente, pero también puedes crear categorías personalizadas en la sección 'Categorías'."
            )

            HelpSection(
                "¿Qué hago si no carga la información?",
                "Asegúrate de tener conexión a internet. Si el problema continúa, cierra sesión y vuelve a iniciar. " +
                        "En caso extremo, reinstala la aplicación."
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun HelpSection(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F6FF)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1976D2))
            Spacer(Modifier.height(8.dp))
            Text(text, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}
