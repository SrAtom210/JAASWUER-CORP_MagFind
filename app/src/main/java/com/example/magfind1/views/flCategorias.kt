package com.example.magfind1.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.magfind1.SessionManager
import com.example.magfind1.apis.CategoriaRepository
import com.example.magfind1.models.CategoriaDto
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.viewmodels.CategoriasViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasView(navController: NavController, themeViewModel: ThemeViewModel) {

    val vm = remember { CategoriasViewModel() }
    val categorias by vm.categorias.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var editingCategoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val sessionManager = SessionManager(context)
    val token = sessionManager.getToken() ?: ""


    // Dejamos la lógica de eliminación comentada para usarla después con swipe
    /*
    var categoriaAEliminar by remember { mutableStateOf<CategoriaDto?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    */

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
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {

                    //  FAB → AGREGAR
                    FloatingActionButton(
                        onClick = {
                            editingCategoria = null     // modo agregar
                            showDialog = true
                        },
                        containerColor = Color(0xFF1976D2)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar categoría",
                            tint = Color.White
                        )
                    }

                    // FAB → EDITAR (abre dialog vacío y luego selecciona desde el dialog)
                    FloatingActionButton(
                        onClick = {
                            editingCategoria = null     // Abrimos el dialog igual pero con intención de editar
                            showDialog = true
                        },
                        containerColor = Color(0xFF0288D1)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar categoría",
                            tint = Color.White
                        )
                    }
                }
            }

        ) { innerPadding ->

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(padding)
                    .padding(16.dp)
            ) {

                Column {

                    Text(
                        "Categorías",
                        fontSize = 26.sp,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        categorias.isEmpty() -> {
                            Text(
                                "No hay categorías disponibles.",
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
                                        colors = CardDefaults.cardColors(Color(0xFFE3F2FD)),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Text(cat.nombre, fontWeight = FontWeight.Bold)
                                            if (!cat.regla.isNullOrBlank())
                                                Text(
                                                    "Regla: ${cat.regla}",
                                                    color = Color.DarkGray,
                                                    fontSize = 12.sp
                                                )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --------------------------------------------------------
                // ✏ DIÁLOGO PARA AGREGAR / EDITAR
                // --------------------------------------------------------
                if (showDialog) {
                    Dialog(onDismissRequest = { showDialog = false }) {

                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 8.dp
                        ) {

                            var nombre by remember { mutableStateOf(editingCategoria?.nombre ?: "") }
                            var regla by remember { mutableStateOf(editingCategoria?.regla ?: "") }

                            Column(
                                Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Text(
                                    if (editingCategoria == null)
                                        "Editar categoría"
                                    else
                                        "Editar categoría",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                OutlinedTextField(
                                    value = nombre,
                                    onValueChange = { nombre = it },
                                    label = { Text("Nombre") },
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = regla,
                                    onValueChange = { regla = it },
                                    label = { Text("Regla") },
                                    singleLine = true
                                )

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("Cancelar")
                                    }

                                    Button(onClick = {
                                        if (nombre.isBlank()) {
                                            Toast.makeText(
                                                context,
                                                "El nombre no puede estar vacío",
                                                Toast.LENGTH_SHORT
                                            ).show()
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
                                        Text("Guardar")
                                    }
                                }
                            }
                        }
                    }
                }

                // --------------------------------------------------------
                // 🗑️ DIÁLOGO DE ELIMINAR (COMENTADO PARA USARLO CON SWIPE)
                // --------------------------------------------------------
                /*
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
                            Text("¿Eliminar \"${categoriaAEliminar!!.nombre}\"?")
                        }
                    )
                }
                */

            }
        }
    }
}
