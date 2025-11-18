package com.example.magfind1.views

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// --- CAMBIOS DE IMPORTS ---
import com.example.magfind1.RetrofitClient // <-- Bien
import com.example.magfind1.SessionManager // <-- Bien
// import com.example.magfind.apis.FCorreosApi // <--- BORRADO (Ya no se usa)
// -------------------------
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.models.cCorreo
import com.example.magfind1.models.CategoriasResponse
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fCorreosCategorizadosView(navController: NavController, themeViewModel: ThemeViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- CAMBIO DE LÓGICA ---
    // val retrofit = RetrofitClient.retrofit // <--- BORRADO
    // val correosApi = retrofit.create(FCorreosApi::class.java) // <--- BORRADO

    // ¡ESTA ES LA LÍNEA CORRECTA!
    // Usamos la instancia única de ApiService.
    val correosApi = RetrofitClient.instance
    // ------------------------

    var categorias by remember { mutableStateOf<CategoriasResponse?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    // Token actual del usuario logueado
    // (Asegúrate de que SessionManager.token se esté guardando como JWT)
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken() ?: ""

    // Llamada a la API
    LaunchedEffect(Unit) {
        try {
            if (token.isNotEmpty()) {
                // Esta línea ahora funciona porque 'correosApi' es un 'ApiService'
                // y 'ApiService' tiene la función 'obtenerCorreos'
                categorias = correosApi.obtenerCorreos(token)
            } else {
                errorMsg = "Token inválido. Inicia sesión nuevamente."
            }
        } catch (e: Exception) {
            errorMsg = "Error al cargar correos: ${e.message}"
            e.printStackTrace() // Ayuda a ver el error real en Logcat
        }
    }

    fPlantilla(
        title = "Correos",
        navController, themeViewModel = themeViewModel,
        drawerItems = listOf(
            "Home" to { navController.navigate("Home") },
            "Ajustes" to { navController.navigate("Ajustes") },
            "Categorías" to { navController.navigate("Categorias") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Mi Cuenta" to { navController.navigate("MiCuenta") },
            "Suscripción" to { navController.navigate("Suscripcion") }
        )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("MagFind", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1976D2))
                )
            }
        ) { innerPadding ->
            when {
                errorMsg != null -> Text(
                    text = errorMsg!!,
                    color = Color.Red,
                    modifier = Modifier.padding(20.dp)
                )

                categorias == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    categorias!!.forEach { (categoria, correos) ->
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
                }
            }
        }
    }
}

@Composable
fun fCorreoCard(correo: cCorreo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { },
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


