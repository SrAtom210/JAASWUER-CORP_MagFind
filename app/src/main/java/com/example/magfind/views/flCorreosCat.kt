package com.example.magfind.views

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind.RetrofitClient
import com.example.magfind.SessionManager
import com.example.magfind.apis.CorreosResponse
import com.example.magfind.apis.FCorreosApi
import com.example.magfind.components.fPlantilla
import com.example.magfind.models.cCorreo
import com.example.magfind.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fCorreosCategorizadosView(navController: NavController, themeViewModel: ThemeViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val retrofit = RetrofitClient.retrofit
    val correosApi = retrofit.create(FCorreosApi::class.java)

    var categorias by remember { mutableStateOf<CorreosResponse?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    val token = SessionManager.token ?: ""

    // Cargar datos
    LaunchedEffect(Unit) {
        try {
            if (token.isNotEmpty()) {
                categorias = correosApi.obtenerCorreos(token)
            } else {
                errorMsg = "Token inválido. Inicia sesión nuevamente."
            }
        } catch (e: Exception) {
            errorMsg = "Error al cargar correos: ${e.message}"
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    errorMsg != null -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = errorMsg!!, color = Color.Red)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = {
                            scope.launch {
                                try {
                                    categorias = correosApi.obtenerCorreos(token)
                                    errorMsg = null
                                } catch (e: Exception) {
                                    errorMsg = "Error al recargar: ${e.message}"
                                }
                            }
                        }) {
                            Text("Reintentar")
                        }
                    }

                    categorias == null -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    }

                    else -> {
                        val mapaCategorias = categorias!!.categorias

                        if (mapaCategorias.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay correos clasificados aún", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                mapaCategorias.forEach { (categoria, correos) ->
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
                                                    Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
                                                contentDescription = null,
                                                tint = Color(0xFF1976D2)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = categoria,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1976D2)
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = "${correos.size}",
                                                fontSize = 14.sp,
                                                color = Color.DarkGray,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }

                                        AnimatedVisibility(visible = expandedStates[categoria] == true) {
                                            Column {
                                                if (correos.isEmpty()) {
                                                    Text(
                                                        text = "No hay correos en esta categoría",
                                                        modifier = Modifier.padding(start = 32.dp, bottom = 8.dp),
                                                        color = Color.Gray
                                                    )
                                                } else {
                                                    correos.forEach { correo ->
                                                        fCorreoCard(correo) {
                                                            navController.navigate("CorreoDetail/${correo.id}")
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
            }
        }
    }
}

@Composable
fun fCorreoCard(correo: cCorreo, onClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick?.invoke() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = correo.remitente,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0D47A1)
                )
                Spacer(modifier = Modifier.weight(1f))
                correo.fecha.let {
                    Text(
                        text = formatFecha(it),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = correo.asunto,
                fontSize = 15.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

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

private fun formatFecha(fechaStr: String): String {
    return try {
        val parseFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        )
        var date: Date? = null
        for (fmt in parseFormats) {
            try {
                date = fmt.parse(fechaStr)
                if (date != null) break
            } catch (_: Exception) { }
        }
        if (date == null) return fechaStr
        val out = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        out.format(date)
    } catch (e: Exception) {
        fechaStr
    }
}
