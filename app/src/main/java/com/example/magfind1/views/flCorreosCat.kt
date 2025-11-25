package com.example.magfind1.views


import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import com.example.magfind1.RetrofitClient
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.models.cCorreo
import com.example.magfind1.models.CategoriasResponse
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fCorreosCategorizadosView(navController: NavController, themeViewModel: ThemeViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val correosApi = RetrofitClient.instance

    var categorias by remember { mutableStateOf<CategoriasResponse?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    // 🔐 SessionManager para token
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken() ?: ""

    // 🔍 Variables del buscador estilo Gmail
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // 📡 Llamada a API
    LaunchedEffect(Unit) {
        try {
            if (token.isNotEmpty()) {
                categorias = correosApi.obtenerCorreos(token)
            } else {
                errorMsg = "Token inválido. Inicia sesión nuevamente."
            }
        } catch (e: Exception) {
            errorMsg = "Error al cargar correos: ${e.message}"
            e.printStackTrace()
        }
    }

    fPlantilla(
        title = "Correos",
        navController = navController,
        themeViewModel = themeViewModel,

        // Habilita el buscador estilo Gmail
        searchEnabled = true,

        //  Recibe el texto de búsqueda
        onSearchQueryChange = { query ->
            searchQuery = query
        },

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
                    title = {
                        // 🔍 CAMPO DE BÚSQUEDA EXPANDIBLE
                        if (!isSearching) {
                            Text("MagFind", color = Color.White)
                        } else {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Buscar correos...", color = Color.White) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White,
                                    focusedLabelColor = Color.White
                                ),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        isSearching = false
                                    }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Cerrar búsqueda",
                                            tint = Color.White
                                        )
                                    }
                                }
                            )
                        }
                    },
                    navigationIcon = {
                        if (!isSearching) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        }
                    },
                    actions = {
                        if (!isSearching) {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                            }
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

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {

                        categorias!!.forEach { (categoria, correosOriginales) ->

                            // 🔍 FILTRO DE BÚSQUEDA
                            val correosFiltrados = correosOriginales.filter { correo ->
                                val q = searchQuery.lowercase()

                                q.isEmpty() ||
                                        categoria.lowercase().contains(q) ||
                                        correo.remitente.lowercase().contains(q) ||
                                        correo.asunto.lowercase().contains(q) ||
                                        correo.descripcion.lowercase().contains(q)
                            }

                            if (correosFiltrados.isEmpty()) return@forEach

                            // 🟦 CABECERA DE CATEGORÍA
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedStates[categoria] =
                                                !(expandedStates[categoria] ?: false)
                                        }
                                        .padding(vertical = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (expandedStates[categoria] == true)
                                            Icons.Filled.KeyboardArrowDown
                                        else Icons.Filled.KeyboardArrowRight,
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
                                        correosFiltrados.forEach { correo ->
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
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fCorreosCategorizadosView(navController: NavController, themeViewModel: ThemeViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cliente API
    val correosApi = RetrofitClient.instance

    // Estado de la respuesta cruda (El mapa)
    var categoriasMap by remember { mutableStateOf<CategoriasResponse?>(null) }

    // NUEVO: Estado derivado que contiene la LISTA PLANA de todos los correos
    val todosLosCorreos = remember(categoriasMap) {
        // Toma los valores del mapa (las listas) y las aplana en una sola lista
        // Opcional: .sortedByDescending { it.fecha } si quisieras reordenar por fecha
        categoriasMap?.values?.flatten() ?: emptyList()
    }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken() ?: ""

    LaunchedEffect(Unit) {
        try {
            if (token.isNotEmpty()) {
                categoriasMap = correosApi.obtenerCorreos(token)
            } else {
                errorMsg = "Token inválido. Inicia sesión nuevamente."
            }
        } catch (e: Exception) {
            errorMsg = "Error al cargar correos: ${e.message}"
            e.printStackTrace()
        }
    }

    fPlantilla(
        title = "Todos los Correos", // Cambié el título para reflejar que es general
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
                    title = { Text("Bandeja de Entrada", color = Color.White) },
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

                categoriasMap == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }

                // Si la lista está vacía (pero cargó bien)
                todosLosCorreos.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay correos para mostrar", color = Color.Gray)
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    // Aquí iteramos directamente sobre la lista plana, sin categorías
                    items(todosLosCorreos) { correo ->
                        fCorreoCard(correo, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun fCorreoCard(correo: cCorreo, navController: NavController) { // Asegúrate que 'cCorreo' sea el nombre correcto de tu modelo
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { navController.navigate("DetalleCorreo/${correo.id}") },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = correo.remitente,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0D47A1),
                    modifier = Modifier.weight(1f)
                )
                // Si tu modelo tiene fecha, es buen lugar para ponerla
                // Text(text = correo.fecha, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = correo.asunto,
                fontWeight = FontWeight.SemiBold, // Un poco más de peso al asunto
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