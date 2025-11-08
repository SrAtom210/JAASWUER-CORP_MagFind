package com.example.magfind.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.magfind.SessionManager
import com.example.magfind.apis.CategoriaRepository
import com.example.magfind.models.CategoriaDto
import com.example.magfind.ui.theme.ThemeViewModel
import com.example.magfind.components.fPlantilla
import com.example.magfind.viewmodels.CategoriasViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasView(navController: NavController, themeViewModel: ThemeViewModel) {
    val vm = remember { CategoriasViewModel() }
    val categorias by vm.categorias.collectAsState()
    val token = SessionManager.token ?: ""
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var editingCategoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // 🔹 Estados para confirmación de eliminación
    var categoriaAEliminar by remember { mutableStateOf<CategoriaDto?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Cargar categorías solo si hay token válido
    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            isLoading = true
            vm.cargarCategorias(token)
            isLoading = false
        }
    }

    fPlantilla(
        title = "Categorías",
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

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        editingCategoria = null
                        showDialog = true
                    },
                    containerColor = Color(0xFF1976D2)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar categoría", tint = Color.White)
                }
            }
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Categorías",
                        fontSize = 26.sp,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    when {
                        isLoading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }

                        categorias.isEmpty() -> {
                            Text(
                                text = "No hay categorías disponibles.",
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 90.dp)
                            ) {
                                items(categorias) { cat ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(cat.nombre, fontWeight = FontWeight.Bold, color = Color.Black)
                                                if (!cat.regla.isNullOrBlank())
                                                    Text("Regla: ${cat.regla}", style = MaterialTheme.typography.bodySmall,
                                                        color = Color.DarkGray)
                                            }

                                            Row {
                                                // Botón editar
                                                IconButton(onClick = {
                                                    editingCategoria = cat
                                                    showDialog = true
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF1976D2))
                                                }

                                                // Botón eliminar con confirmación
                                                IconButton(onClick = {
                                                    categoriaAEliminar = cat
                                                    showDeleteDialog = true
                                                }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Diálogo de confirmación de eliminación
                if (showDeleteDialog && categoriaAEliminar != null) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    try {
                                        val repo = CategoriaRepository()
                                        repo.eliminarCategoria(token, categoriaAEliminar!!.id_categoria)
                                        vm.cargarCategorias(token)
                                        Toast.makeText(context, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                showDeleteDialog = false
                            }) {
                                Text("Eliminar", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar")
                            }
                        },
                        title = { Text("Confirmar eliminación") },
                        text = {
                            Text("¿Estás seguro de que deseas eliminar la categoría \"${categoriaAEliminar!!.nombre}\"?")
                        }
                    )
                }

                // ✏️ Diálogo agregar/editar
                if (showDialog) {
                    Dialog(onDismissRequest = { showDialog = false }) {
                        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp) {
                            var nombre by remember { mutableStateOf(editingCategoria?.nombre ?: "") }
                            var regla by remember { mutableStateOf(editingCategoria?.regla ?: "") }

                            Column(
                                Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    if (editingCategoria == null) "Agregar categoría" else "Editar categoría",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                OutlinedTextField(
                                    value = nombre,
                                    onValueChange = { nombre = it },
                                    label = { Text("Nombre de la categoría") },
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = regla,
                                    onValueChange = { regla = it },
                                    label = { Text("Regla o palabra clave") },
                                    singleLine = true
                                )

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("Cancelar")
                                    }
                                    Button(onClick = {
                                        if (nombre.isBlank()) {
                                            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }

                                        scope.launch {
                                            try {
                                                val repo = CategoriaRepository()
                                                if (editingCategoria == null) {
                                                    repo.agregarCategoria(token, nombre, regla)
                                                    Toast.makeText(context, "Categoría creada", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    repo.editarCategoria(
                                                        token,
                                                        editingCategoria!!.id_categoria,
                                                        nombre,
                                                        regla
                                                    )
                                                    Toast.makeText(context, "Categoría actualizada", Toast.LENGTH_SHORT).show()
                                                }
                                                vm.cargarCategorias(token)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                                            }
                                            showDialog = false
                                        }
                                    }) {
                                        Text(if (editingCategoria == null) "Guardar" else "Actualizar")
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
