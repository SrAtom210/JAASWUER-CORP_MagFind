package com.example.magfind1.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.viewmodels.FHomeViewModel

// Helper para convertir el Hex String (#FF0000) de la DB a Color de Compose
fun parseHexColor(hex: String?): Color {
    return try {
        if (hex.isNullOrEmpty()) Color.Gray
        else Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}

@Composable
fun flHomeView(navController: NavController, themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    val homeViewModel: FHomeViewModel = viewModel {
        FHomeViewModel(sessionManager = session)
    }

    val state by homeViewModel.uiState.collectAsState()
    val nombreUsuario = session.getDisplayName() ?: "Usuario"

    val lightBlueBg = Color(0xFFF5F9FF)
    val primaryBlue = Color(0xFF1976D2)

    fPlantilla(
        title = "Inicio",
        navController = navController,
        themeViewModel = themeViewModel,
        drawerItems = listOf(
            "Home" to { navController.navigate("Home") },
            "Categorías" to { navController.navigate("Categorias") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Ajustes" to { navController.navigate("Ajustes") },
            "Mi Cuenta" to { navController.navigate("MiCuenta") },
            "Suscripción" to { navController.navigate("Suscripcion") }
        )
    ) { padding ->

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(lightBlueBg)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. BIENVENIDA
                Column {
                    Text(
                        text = "Hola, $nombreUsuario 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF202124)
                    )
                    Text(
                        text = "Aquí tienes el resumen de hoy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                // 2. CARD PRINCIPAL
                StatusCard(
                    count = state.unorganizedCount,
                    label = "Correos sin organizar",
                    subLabel = if (state.unorganizedCount > 0) "Toca para limpiar" else "¡Todo limpio!",
                    icon = Icons.Outlined.MarkEmailUnread,
                    colorStart = Color(0xFF2196F3),
                    colorEnd = Color(0xFF64B5F6),
                    onClick = { navController.navigate("CorreosCat") }
                )

                // 3. FAVORITOS
                if (state.favorites.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SectionHeader("Accesos Rápidos", Icons.Rounded.Star, primaryBlue)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.favorites) { cat ->
                                FavoriteCategoryCard(
                                    name = cat.nombre,
                                    color = parseHexColor(cat.colorHex),
                                    onClick = { navController.navigate("CorreosCategoria/${cat.id_categoria}/${cat.nombre}") }
                                )
                            }
                        }
                    }
                }

                // 4. USO DE IA
                UsageCard(
                    used = state.aiUsed,
                    limit = state.aiLimit,
                    primaryColor = primaryBlue,
                    onClick = { navController.navigate("Suscripcion") }
                )

                // 5. ACTIVIDAD RECIENTE (CORREGIDO: SIN DUPLICADOS)
                if (state.recentActivityList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp)) // Ajuste visual

                    // Cabecera
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader("Actividad Reciente", Icons.Default.History, Color(0xFF546E7A))

                        TextButton(onClick = { navController.navigate("CorreosCat") }) {
                            Text("Ver todo", fontSize = 13.sp, color = primaryBlue)
                        }
                    }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                            // Iteramos DIRECTAMENTE aquí, sin otro IF ni otra CARD
                            state.recentActivityList.forEachIndexed { index, correo ->

                                RecentEmailItem(
                                    remitente = correo.remitente,
                                    asunto = correo.asunto,
                                    categoria = correo.categoria,
                                    colorCategoria = parseHexColor(correo.colorHex),
                                    fecha = correo.fecha,
                                    onClick = {
                                        navController.navigate("DetalleCorreo/${correo.id}")
                                    }
                                )

                                // Divisor
                                if (index < state.recentActivityList.size - 1) {
                                    HorizontalDivider(
                                        color = Color(0xFFEEEEEE),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. BANNER
                val plan = session.getPlan()?.lowercase()?.trim()
                // Lógica corregida: Si NO es admin Y NO es business
                if (plan != "admin" && plan != "business") {
                    PromoBanner(
                        title = "¿Necesitas más potencia?",
                        description = "Pásate a Business y obtén IA ilimitada.",
                        onClick = { navController.navigate("Suscripcion") }
                    )
                }

                // Espacio final para que no se corte el scroll
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// --- COMPONENTES VISUALES ---

@Composable
fun SectionHeader(title: String, icon: ImageVector, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF37474F)
        )
    }
}

@Composable
fun StatusCard(
    count: Int,
    label: String,
    subLabel: String, // <--- Ahora sí lo vamos a usar
    icon: ImageVector,
    colorStart: Color,
    colorEnd: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp) // Altura aumentada
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(colorStart, colorEnd)))
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(6.dp) // Espacio equilibrado
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 1. Contador Gigante
                Text(
                    text = if (count > 0) "$count Nuevos" else "Todo limpio",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 38.sp
                )

                // 2. Título Principal
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.95f)
                )

                // 3. Subtítulo (El que faltaba)
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f) // Un poco más transparente
                )
            }

            // Flechita
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
            )
        }
    }
}

@Composable
fun FavoriteCategoryCard(name: String, color: Color, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.size(width = 110.dp, height = 100.dp).clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = color, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun UsageCard(used: Int, limit: Int, primaryColor: Color, onClick: () -> Unit) {
    val percentage = (used.toFloat() / limit.toFloat()).coerceIn(0f, 1f)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                progress = { percentage },
                modifier = Modifier.size(50.dp),
                color = if (percentage > 0.9f) Color.Red else primaryColor,
                trackColor = Color(0xFFE0E0E0),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Uso diario de IA", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("$used de $limit clasificaciones usadas", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PromoBanner(title: String, description: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFFC107))
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            }
            Icon(Icons.Default.WorkspacePremium, null, tint = Color(0xFFFFC107), modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun RecentEmailItem(
    remitente: String,
    asunto: String,
    categoria: String,
    colorCategoria: Color,
    fecha: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp), // Un poco más de aire para el dedo
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Avatar con inicial
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = remitente.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF757575),
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. Textos Centrales
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = remitente,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = asunto,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3. Badge de Categoría y Hora
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                color = colorCategoria.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = categoria,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorCategoria,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTime(fecha),
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray,
                fontSize = 11.sp
            )
        }
    }
}

fun formatTime(dateString: String): String {
    return try {
        if (dateString.contains(" ")) {
            dateString.split(" ")[1].substring(0, 5)
        } else {
            "Reciente"
        }
    } catch (e: Exception) {
        "Hoy"
    }
}

//push inofencivo