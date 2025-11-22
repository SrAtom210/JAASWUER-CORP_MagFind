package com.example.magfind1.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.magfind1.SessionManager
import com.example.magfind1.apis.CategoriaRepository
import com.example.magfind1.components.AdMobBanner
import com.example.magfind1.models.CategoriaDto
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.viewmodels.CategoriasViewModel
// --- IMPORTS NUEVOS PARA COLOR ---
import com.example.magfind1.components.ColorPicker
import com.example.magfind1.components.parseColor
// --------------------------------
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CategoriasView(navController: NavController, themeViewModel: ThemeViewModel) {

    val vm = remember { CategoriasViewModel() }
    val categorias by vm.categorias.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingCategoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var deletingCategoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val sessionManager = SessionManager(context)
    val token = sessionManager.getToken() ?: ""

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
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
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
                                if (true) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)        // << ESTE ES EL "MOUNSTRO"
                                            .padding(8.dp)
                                            .background(Color(0xFFE3F2FD), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AdMobBanner(
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                // Convertimos el hex string a Color real
                                val catColor = parseColor(cat.colorHex)

                                if (cat.nombre == "Sin organizar") {
                                    // Tarjeta Fija (No swipeable)
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(Color(0xFFE3F2FD)),
                                        elevation = CardDefaults.cardElevation(3.dp)
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Indicador de color
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(catColor, CircleShape)
                                                    .border(1.dp, Color.Gray, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    cat.nombre,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.DarkGray
                                                )
                                                if (!cat.regla.isNullOrBlank())
                                                    Text("Regla: ${cat.regla}", color = Color.Gray, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                } else {
                                    // Tarjeta Swipeable
                                    val dismissState = rememberDismissState { value ->
                                        when (value) {
                                            DismissValue.DismissedToEnd -> { // Editar
                                                editingCategoria = cat
                                                showDialog = true
                                                false
                                            }
                                            DismissValue.DismissedToStart -> { // Eliminar
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

                                            if (!isSwiping || direction == null || progress <= 0.05f) {
                                                Box(Modifier.fillMaxSize().background(Color.Transparent))
                                            } else {
                                                val color = when (direction) {
                                                    DismissDirection.StartToEnd -> Color(0xFF4CAF50)
                                                    DismissDirection.EndToStart -> Color(0xFFF44336)
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
                                                        .background(color.copy(alpha = progress.coerceIn(0.3f, 1f)), RoundedCornerShape(12.dp))
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
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(Color(0xFFE3F2FD)),
                                                elevation = CardDefaults.cardElevation(2.dp)
                                            ) {
                                                Row(
                                                    Modifier.fillMaxWidth().padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        // Indicador de color
                                                        Box(
                                                            modifier = Modifier
                                                                .size(20.dp)
                                                                .background(catColor, CircleShape)
                                                                .border(1.dp, Color.Gray, CircleShape)
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))

                                                        Column {
                                                            Text(cat.nombre, fontWeight = FontWeight.Bold)
                                                            if (!cat.regla.isNullOrBlank())
                                                                Text("Regla: ${cat.regla}", color = Color.DarkGray, fontSize = 12.sp)
                                                        }
                                                    }

                                                    // --- ESTRELLA FAVORITO (REINTEGRADA) ---
                                                    // Nota: Esta variable es local. Para persistencia,
                                                    // deberías llamar a un endpoint en onClick.
                                                    var isFavorite by remember { mutableStateOf(false) } // O usa cat.esFavorita si existe en tu DTO

                                                    IconButton(onClick = {
                                                        isFavorite = !isFavorite
                                                        // TODO: Llamar a vm.marcarFavorita(cat.id_categoria, isFavorite) si es necesario
                                                    }) {
                                                        Icon(
                                                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                                            contentDescription = "Favorito",
                                                            tint = if (isFavorite) Color(0xFFFFD700) else Color.Gray
                                                        )
                                                    }
                                                    // ---------------------------------------
                                                }
                                            }
                                        },
                                        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // POPUP DE AGREGAR / EDITAR (ESTILO MODERNO CON COLOR)
            // ---------------------------------------------------------
            if (showDialog) {
                Dialog(
                    onDismissRequest = { showDialog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false) // Permite usar más ancho
                ) {
                    // Surface estilo "Tarjeta Flotante"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.92f) // 92% del ancho de pantalla
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        var nombre by remember { mutableStateOf(editingCategoria?.nombre ?: "") }
                        var regla by remember { mutableStateOf(editingCategoria?.regla ?: "") }
                        // Color por defecto o el que tenía la categoría
                        var selectedColor by remember { mutableStateOf(editingCategoria?.colorHex ?: "#1976D2") }

                        Column(
                            Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Encabezado
                            Text(
                                text = if (editingCategoria == null) "Nueva Categoría" else "Editar Categoría",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )

                            // Inputs
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = regla,
                                onValueChange = { regla = it },
                                label = { Text("Palabra clave (Regla)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Selector de Color (NUEVO)
                            ColorPicker(
                                selectedColorHex = selectedColor,
                                onColorSelected = { selectedColor = it }
                            )

                            // Botones
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Cancelar", color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (nombre.isBlank()) {
                                            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        scope.launch {
                                            try {
                                                val repo = CategoriaRepository()
                                                if (editingCategoria == null) {
                                                    // Agregamos pasando el color
                                                    repo.agregarCategoria(token, nombre, regla, selectedColor)
                                                    Toast.makeText(context, "Categoría creada", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    // Editamos pasando el color
                                                    repo.editarCategoria(
                                                        token,
                                                        editingCategoria!!.id_categoria,
                                                        nombre,
                                                        regla,
                                                        selectedColor
                                                    )
                                                    Toast.makeText(context, "Categoría actualizada", Toast.LENGTH_SHORT).show()
                                                }
                                                vm.cargarCategorias(token)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                                            }
                                            showDialog = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }
                    }
                }
            }

            // Diálogo de Confirmar Eliminación (Sin cambios mayores)
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