package com.example.magfind1.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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
import com.example.magfind1.components.ColorPicker
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.components.parseColor
import com.example.magfind1.models.CategoriaDto
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.viewmodels.CategoriasViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CategoriasView(navController: NavController, themeViewModel: ThemeViewModel) {
    // ViewModel y estados comunes
    val vm = remember { CategoriasViewModel() }
    val categorias by vm.categorias.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingCategoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var deletingCategoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Mantener estado local de favoritos (por simplicidad; persistir en backend si hace falta)
    // Inicializamos con false para cada categoría por id; si tu DTO tiene campo isFavorite úsalo.
    val favoriteStates = remember { mutableStateMapOf<Int, Boolean>() }

    val sessionManager = SessionManager(context)
    val token = sessionManager.getToken() ?: ""

    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            isLoading = true
            vm.cargarCategorias(token)
            isLoading = false
        }
    }

    // Plantilla principal (drawer, etc.)
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
            // Tomo lo mejor de ambas versiones: barra inferior con AdMob (si aplica)
            bottomBar = {
                Column(Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AdMobBanner()
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        editingCategoria = null
                        showDialog = true
                    },
                    containerColor = Color(0xFF1976D2),
                    // mantener padding más alto si hay bottomBar
                    modifier = Modifier.padding(bottom = 60.dp)
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
                    "Gestión de Categorías",
                    fontSize = 24.sp,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when {
                    isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    categorias.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay categorías disponibles.", color = Color.Gray)
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(categorias, key = { it.id_categoria }) { cat ->
                                // Obtener color (con fallback)
                                val catColor = try {
                                    parseColor(cat.colorHex)
                                } catch (e: Exception) {
                                    Color.Gray
                                }

                                // Texto adaptativo según luminancia
                                val textColor = if (catColor.luminance() > 0.5f) Color.Black else Color.White
                                val subTextColor = textColor.copy(alpha = 0.85f)

                                val esSistema = cat.nombre.equals("Sin organizar", ignoreCase = true)

                                if (esSistema) {
                                    // Tarjeta inmutable de sistema (sin swipe / sin favorito)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                Toast.makeText(context, "Carpeta: ${cat.nombre}", Toast.LENGTH_SHORT).show()
                                            },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE)),
                                        elevation = CardDefaults.cardElevation(0.dp)
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(20.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = cat.nombre,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp,
                                                    color = Color.Gray
                                                )
                                                if (!cat.regla.isNullOrBlank()) {
                                                    Text(
                                                        text = "Regla: ${cat.regla}",
                                                        fontSize = 12.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                } else {
                                    // Tarjeta de usuario: SwipeToDismiss con edición/eliminación
                                    val dismissState = rememberDismissState { value ->
                                        when (value) {
                                            DismissValue.DismissedToEnd -> {
                                                editingCategoria = cat
                                                showDialog = true
                                                false
                                            }
                                            DismissValue.DismissedToStart -> {
                                                deletingCategoria = cat
                                                showDeleteDialog = true
                                                false
                                            }
                                            else -> false
                                        }
                                    }

                                    SwipeToDismiss(
                                        state = dismissState,
                                        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                                        background = {
                                            val direction = dismissState.dismissDirection
                                            val progress = dismissState.progress.fraction
                                            if (direction == null || progress <= 0.05f) {
                                                Box(Modifier.fillMaxSize().background(Color.Transparent))
                                            } else {
                                                val color = when (direction) {
                                                    DismissDirection.StartToEnd -> Color(0xFF4CAF50)
                                                    DismissDirection.EndToStart -> Color(0xFFE57373)
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
                                                    Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = progress.coerceIn(0.5f, 1f)))
                                                }
                                            }
                                        },
                                        dismissContent = {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        Toast.makeText(context, "Ver correos de: ${cat.nombre}", Toast.LENGTH_SHORT).show()
                                                    },
                                                colors = CardDefaults.cardColors(containerColor = catColor),
                                                elevation = CardDefaults.cardElevation(4.dp)
                                            ) {
                                                Row(
                                                    Modifier.fillMaxWidth().padding(20.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = cat.nombre,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 18.sp,
                                                            color = textColor
                                                        )
                                                        if (!cat.regla.isNullOrBlank()) {
                                                            Text(
                                                                text = "${cat.regla}",
                                                                color = subTextColor,
                                                                fontSize = 13.sp
                                                            )
                                                        }
                                                    }

                                                    // ---------- ESTRELLA FAVORITO (REINTEGRADA EN LUGAR DE LA FLECHA) ----------
                                                    // Usamos un estado por id en favoriteStates map para evitar recrear estados no persistidos.
                                                    val isFav = favoriteStates[cat.id_categoria] ?: false
                                                    IconButton(onClick = {
                                                        val nuevo = !isFav
                                                        favoriteStates[cat.id_categoria] = nuevo
                                                        // TODO: persistir favorito en backend si tu DTO/endpoint lo soporta
                                                        // Ej: vm.marcarFavorita(cat.id_categoria, nuevo)
                                                    }) {
                                                        Icon(
                                                            imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                                            contentDescription = "Favorito",
                                                            tint = if (isFav) Color(0xFFFFD700) else subTextColor
                                                        )
                                                    }
                                                    // --------------------------------------------------------------------------------
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --------------------
            // DIÁLOGO CREAR / EDITAR
            // --------------------
            if (showDialog) {
                Dialog(
                    onDismissRequest = { showDialog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        var nombre by remember { mutableStateOf(editingCategoria?.nombre ?: "") }
                        var regla by remember { mutableStateOf(editingCategoria?.regla ?: "") }
                        var selectedColor by remember { mutableStateOf(editingCategoria?.colorHex ?: "#1976D2") }

                        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = if (editingCategoria == null) "Nueva Categoría" else "Editar Categoría",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )

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
                                label = { Text("Regla") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Text("Color de la tarjeta:", fontWeight = FontWeight.Medium)
                            ColorPicker(selectedColorHex = selectedColor, onColorSelected = { selectedColor = it })

                            // PREVIEW CON TEXTO ADAPTATIVO
                            val previewColor = try { parseColor(selectedColor) } catch (e: Exception) { Color.Gray }
                            val previewTextColor = if (previewColor.luminance() > 0.5f) Color.Black else Color.White

                            Card(
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = CardDefaults.cardColors(containerColor = previewColor)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Así se verá el texto", color = previewTextColor, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
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
                                                    repo.agregarCategoria(token, nombre, regla, selectedColor)
                                                    Toast.makeText(context, "Categoría creada", Toast.LENGTH_SHORT).show()
                                                } else {
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

            // --------------------
            // DIÁLOGO CONFIRMAR ELIMINACIÓN
            // --------------------
            if (showDeleteDialog && deletingCategoria != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("¿Eliminar Categoría?") },
                    text = { Text("Se eliminará \"${deletingCategoria!!.nombre}\".") },
                    confirmButton = {
                        Button(
                            onClick = {
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
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) { Text("Eliminar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}