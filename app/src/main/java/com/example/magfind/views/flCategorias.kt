package com.example.magfind.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.magfind.components.fPlantilla
import com.example.magfind.ui.theme.ThemeViewModel
import com.example.magfind.viewmodels.CategoriasViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magfind.models.Regla
import com.example.magfind.viewmodels.ReglaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasView(navController: NavController, themeViewModel: ThemeViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ViewModel y estado
    val vm: CategoriasViewModel = viewModel()
    val categorias by vm.categorias.collectAsState()
    // Estado para controlar la visibilidad del diálogo de registro
    var showDialog by remember { mutableStateOf(false)}
    val viewModel: ReglaViewModel = viewModel()
    val reglas by viewModel.reglas.collectAsState()
    
    var condicion by remember { mutableStateOf("") }
    var accion by remember { mutableStateOf("") }
    var reglaEditando by remember { mutableStateOf<Regla?>(null) }

    // Cargar al entrar
    LaunchedEffect(Unit) {
        vm.cargarCategorias(com.example.magfind.SessionManager.token!!)
    }

    fPlantilla(
        title = "Categorias",
        navController, themeViewModel = themeViewModel,
        drawerItems = listOf(
            "Home" to { navController.navigate("Home") },
            "Ajustes" to { navController.navigate("Ajustes") },
            "Categorías" to { navController.navigate("Categorias") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Mi Cuenta" to { navController.navigate("MiCuenta") },
            "Suscripcion" to { navController.navigate("Suscripcion") }
        )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("MagFind", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF1976D2)
                    )
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row {
                        FloatingActionButton(onClick = { /* editar */ }, containerColor = Color(0xFF1976D2)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                        }
                    }
                    Row {
                        FloatingActionButton(onClick = { /* agregar */ }, containerColor = Color(0xFF1976D2)) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        FloatingActionButton(onClick = {showDialog = true}, containerColor = Color(0xFF1976D2)) {
                            Icon(Icons.Default.Password, contentDescription = "Agregar", tint = Color.White)
                        }
                    }
                }
            }
        ) {innerPadding ->
            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (reglaEditando == null) "Nueva regla" else "Editar regla",
                                style = MaterialTheme.typography.titleLarge
                            )

                            OutlinedTextField(
                                value = condicion,
                                onValueChange = { condicion = it },
                                label = { Text("Condición (Ej. contiene 'factura')") }
                            )

                            OutlinedTextField(
                                value = accion,
                                onValueChange = { accion = it },
                                label = { Text("Acción (Ej. mover a 'Finanzas')") }
                            )

                            Button(
                                onClick = {
                                    if (reglaEditando == null) {
                                        viewModel.agregarRegla(
                                            Regla(condicion = condicion, accion = accion, usuario_id = 1)
                                        )
                                    } else {
                                        viewModel.editarRegla(
                                            reglaEditando!!.id!!,
                                            Regla(condicion = condicion, accion = accion, usuario_id = 1)
                                        )
                                        reglaEditando = null
                                    }
                                    condicion = ""
                                    accion = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (reglaEditando == null) "Agregar" else "Guardar cambios")
                            }

                            // --- NUEVO: mostrar/ocultar reglas con un botón ---
                            var mostrarReglas by remember { mutableStateOf(false) }

                            TextButton(
                                onClick = { mostrarReglas = !mostrarReglas },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(if (mostrarReglas) "Ocultar reglas" else "Mostrar reglas")
                            }

                            AnimatedVisibility(visible = mostrarReglas) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (reglas.isEmpty()) {
                                        Text("No hay reglas creadas aún.", color = Color.Gray)
                                    } else {
                                        LazyColumn {
                                            items(reglas) { regla ->
                                                Row(
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text("• ${regla.condicion} → ${regla.accion}")
                                                    Row {
                                                        IconButton(onClick = {
                                                            reglaEditando = regla
                                                            condicion = regla.condicion
                                                            accion = regla.accion
                                                        }) {
                                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                                        }
                                                        IconButton(onClick = {
                                                            regla.id?.let { viewModel.eliminarRegla(it) }
                                                        }) {
                                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Categorías",
                    fontSize = 30.sp,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                // Hint de carga/ vacío
                if (categorias.isEmpty()) {
                    Text(
                        "Cargando categorías o no hay registros.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(categorias) { categoria ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Text(
                                text = categoria.nombre,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
    
