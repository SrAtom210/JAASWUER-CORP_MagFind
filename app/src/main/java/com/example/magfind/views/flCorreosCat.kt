package com.example.magfind.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind.components.fPlantilla
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fCorreosCategorizadosView(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val categorias = mapOf(
        "Trabajo" to listOf(
            cCorreo("Recursos Humanos", "Revisión de contrato", "Favor de revisar el nuevo formato del contrato laboral."),
            cCorreo("Jefe de Proyecto", "Entrega sprint", "Adjunto los avances del sprint actual y próximos objetivos.")
        ),
        "Personal" to listOf(
            cCorreo("Amazon", "Tu pedido ha sido enviado", "El paquete #12345 se encuentra en camino."),
            cCorreo("Netflix", "Nuevas recomendaciones", "Series que podrían gustarte esta semana.")
        ),
        "Promociones" to listOf(
            cCorreo("Spotify", "Oferta Premium", "Obtén 3 meses por $9.99"),
            cCorreo("Adobe", "Descuento especial", "40% en el plan anual de Creative Cloud.")
        ),
        "Importante" to listOf(
            cCorreo("Banco XYZ", "Movimiento sospechoso", "Detectamos una transacción inusual en tu cuenta."),
            cCorreo("Seguridad", "Actualización de contraseña", "Por motivos de seguridad, actualiza tu clave de acceso.")
        )
    )

    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    fPlantilla(
        title = "Correos",
        drawerItems = listOf(
            "Home" to { navController.navigate("Home")},
            "Ajustes" to { navController.navigate("Ajustes")},
            "Categorías" to { navController.navigate("Categorias") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Mi Cuenta" to { navController.navigate("MiCuenta")},
            "Suscripcion" to { navController.navigate("Suscripcion")}
        )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("MagFind", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menú",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF1976D2)
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                categorias.forEach { (categoria, correos) ->
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedStates[categoria] = !(expandedStates[categoria] ?: false)
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (expandedStates[categoria] == true)
                                    Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF1976D2)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = categoria,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                        }
                        AnimatedVisibility(visible = expandedStates[categoria] == true) {
                            Column {
                                correos.forEach { correo ->
                                    fCorreoCard(correo)
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// Modelo de datos
data class cCorreo(
    val remitente: String,
    val asunto: String,
    val descripcion: String
)

// Tarjeta de correo
@Composable
fun fCorreoCard(correo: cCorreo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { /* acción: abrir correo */ },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = correo.remitente,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0D47A1)
            )
            Text(
                text = correo.asunto,
                fontSize = 15.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = correo.descripcion,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


