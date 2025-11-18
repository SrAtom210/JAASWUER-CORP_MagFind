package com.example.magfind.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberDismissState
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CategoriasView(navController: NavController, themeViewModel: ThemeViewModel) {

    val vm = remember { CategoriasViewModel() }
    val categorias by vm.categorias.collectAsState()
    val token = SessionManager.token ?: ""
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingCategoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var deletingCategoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                }
            }
        ) { innerPadding ->

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    "Categorías",
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
                            "No hay categorías disponibles.",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(categorias, key = { it.id_categoria }) { cat ->

                                val dismissState = rememberDismissState { value ->
                                    when (value) {
                                        DismissValue.DismissedToEnd -> { // Editar
                                            editingCategoria = cat
                                            showDialog = true
                                            false
                                        }
                                        DismissValue.DismissedToStart -> {  //Eliminar
                                            deletingCategoria = cat
                                            showDeleteDialog = true
                                            false
                                        }
                                        else -> false
                                    }
                                }
                                SwipeToDismiss(
                                    state = dismissState,
                                    background = {
                                        val direction = dismissState.dismissDirection
                                        val progress = dismissState.progress.fraction
                                        val isSwiping = dismissState.targetValue != DismissValue.Default

                                        // Si no se está deslizando, no mostramos nada de color
                                        if (!isSwiping || direction == null || progress <= 0.05f) {
                                            Box(
                                                Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Transparent)
                                            )
                                        } else {
                                            // Mostrar color solo mientras se arrastra
                                            val color = when (direction) {
                                                DismissDirection.StartToEnd -> Color(0xFF4CAF50) // verde editar
                                                DismissDirection.EndToStart -> Color(0xFFF44336) // rojo eliminar
                                            }

                                            val icon = when (direction) {
                                                DismissDirection.StartToEnd -> Icons.Default.Edit
                                                DismissDirection.EndToStart -> Icons.Default.Delete
                                            }

                                            val alignment = when (direction) {
                                                DismissDirection.StartToEnd -> Alignment.CenterStart
                                                DismissDirection.EndToStart -> Alignment.CenterEnd
                                            }

                                            Box(
                                                Modifier
                                                    .fillMaxSize()
                                                    .background(color.copy(alpha = progress.coerceIn(0.3f, 1f)))
                                                    .padding(horizontal = 20.dp),
                                                contentAlignment = alignment
                                            ) {
                                                Icon(
                                                    icon,
                                                    contentDescription = null,
                                                    tint = Color.White.copy(alpha = progress.coerceIn(0.5f, 1f))
                                                )
                                            }
                                        }
                                    },
                                    dismissContent = {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            colors = CardDefaults.cardColors(Color(0xFFE3F2FD)),
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Column(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp)
                                            ) {
                                                Text(cat.nombre, fontWeight = FontWeight.Bold)
                                                if (!cat.regla.isNullOrBlank())
                                                    Text("Regla: ${cat.regla}", color = Color.DarkGray, fontSize = 12.sp)
                                            }
                                        }
                                    },
                                    directions = setOf(
                                        DismissDirection.StartToEnd, //  editar
                                        DismissDirection.EndToStart  //  eliminar
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Diálogo de Agregar / Editar
            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp) {
                        var nombre by remember { mutableStateOf(editingCategoria?.nombre ?: "") }
                        var regla by remember { mutableStateOf(editingCategoria?.regla ?: "") }

                        Column(
                            Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                if (editingCategoria == null) "Agregar Categoría" else "Editar Categoría",
                                style = MaterialTheme.typography.titleLarge
                            )

                            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                            OutlinedTextField(value = regla, onValueChange = { regla = it }, label = { Text("Regla") })

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
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
                                                repo.editarCategoria(token, editingCategoria!!.id_categoria, nombre, regla)
                                                Toast.makeText(context, "Categoría actualizada", Toast.LENGTH_SHORT).show()
                                            }
                                            vm.cargarCategorias(token)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                                        }
                                        showDialog = false
                                    }
                                }) { Text("Guardar") }
                            }
                        }
                    }
                }
            }

            //  Diálogo de Confirmar Eliminación
            if (showDeleteDialog && deletingCategoria != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                try {
                                    val repo = CategoriaRepository()
                                    repo.eliminarCategoria(token, deletingCategoria!!.id_categoria)
                                    vm.cargarCategorias(token)
                                    Toast.makeText(context, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                }
                                showDeleteDialog = false
                            }
                        }) { Text("Eliminar", color = Color.Red) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                    },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Eliminar \"${deletingCategoria!!.nombre}\"?") }
                )
            }
        }
    }
}

