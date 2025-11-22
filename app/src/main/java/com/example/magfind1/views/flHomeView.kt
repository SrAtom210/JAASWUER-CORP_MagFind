package com.example.magfind1.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun flHomeView(navController: NavController, themeViewModel: ThemeViewModel) {

    // ⭐ Obtiene el nombre real del usuario que está logueado
    val nombreUsuario = SessionManager.username ?: "Usuario"

    fPlantilla(
        title = "Inicio",
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F8FF))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 👋 Bienvenida
            Column {
                Text(
                    text = "¡Bienvenido de nuevo!",
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = nombreUsuario,   // ⭐ Aquí ya se muestra el nombre real
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color(0xFF0D47A1)
                )
                Text(
                    text = "Administra tus categorías, correos y progreso fácilmente.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // 🔵 Correos pendientes
            FullGradientCard(
                title = "Correos pendientes",
                subtitle = "2 sin clasificar",
                colorStart = Color(0xFF1B7ACE),
                colorEnd = Color(0xFF3594E8),
                icon = Icons.Default.Email
            ) { navController.navigate("CorreosCat") }

            // 🟣 Categorías Favoritas
            FullGradientCard(
                title = "Categorías Favoritas",
                subtitle = "Accede a tus Categorías Favoritas",
                colorStart = Color(0xFF931bce),
                colorEnd = Color(0xFFac35e8),
                icon = Icons.Default.Folder
            ) { navController.navigate("Categorias") }

            // 🟡 Notificaciones
            FullGradientCard(
                title = "Notificaciones",
                subtitle = "Tienes 3 alertas nuevas",
                colorStart = Color(0xFFadb718),
                colorEnd = Color(0xFFdce835),
                icon = Icons.Default.Notifications
            ) { navController.navigate("Categorias") }
        }
    }
}


// 🔷 Card coloreada
@Composable
fun FullGradientCard(
    title: String,
    subtitle: String,
    colorStart: Color,
    colorEnd: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(8.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(colorStart, colorEnd)))
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.White.copy(alpha = 0.3f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
