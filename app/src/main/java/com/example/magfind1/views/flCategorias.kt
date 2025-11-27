package com.example.magfind1.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.magfind1.RetrofitClient
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
import retrofit2.HttpException

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

    // --- NUEVOS ESTADOS PARA LA IA Y FAVORITOS ---
    var isOrganizing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Mapa local para manejar el estado visual de favorito inmediatamente
    val favoriteStates = remember { mutableStateMapOf<Int, Boolean>() }

    val sessionManager = SessionManager(context)
    val token = sessionManager.getToken() ?: ""
    val session = remember { SessionManager(context) }


    LaunchedEffect(token) {
        if (token.isNotBlank()) {
            isLoading = true
            vm.cargarCategorias(token)
            isLoading = false
        }
    }

    // Sincronizar favoritos cuando llegan datos del servidor
    LaunchedEffect(categorias) {
        categorias.forEach { cat ->
            // CORRECCIÓN: Acceso directo a la propiedad 'prioridad'
            favoriteStates[cat.id_categoria] = cat.prioridad >= 2
        }
    }

    LaunchedEffect(Unit, categorias) {
        vm.checkQuota(token)
    }

    // Plantilla principal (drawer, etc.)
    fPlantilla(
        title = "Categorías",
        navController = navController,
        themeViewModel = themeViewModel,
        drawerItems = listOf(
            "Home" to { navController.navigate("Home") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Categorías" to { navController.navigate("Categorias") },
            "Mi Cuenta" to { navController.navigate("MiCuenta") },
            "Suscripción" to { navController.navigate("Suscripcion") },
            "Ajustes" to { navController.navigate("Ajustes") },
        )
    ) { padding ->

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        val userPlan = session.getPlan()?.trim()?.lowercase() ?: ""

                        val isPaidUser = userPlan == "admin" || userPlan == "business" || userPlan == "plus" || userPlan == "premium"

                        // 2. Condicional: Solo mostrar si es 'essential'
                        if (!isPaidUser) {
                            Spacer(modifier = Modifier.height(30.dp))

                            // --- ANUNCIO REAL ---
                            AdMobBanner()

                            Spacer(modifier = Modifier.height(20.dp))
                        } else {
                            // Si paga (Plus/Business), solo dejamos un espacio pequeño estético
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            },
            floatingActionButton = {
                val canOrganize by vm.canOrganize.collectAsState()

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 8.dp) // Ajuste para no tapar con AdMob
                ) {
                    // --- 1. NUEVO BOTÓN: ORGANIZAR CON IA ---
                    if (canOrganize) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                if (!isOrganizing) {
                                    isOrganizing = true
                                    scope.launch {
                                        try {
                                            // 1. Llamada al backend síncrona
                                            val res = RetrofitClient.instance.triggerClasificacion(token)

                                            // 2. Cálculo de tokens usados (1 correo = 1 token)
                                            val cantidad = res.count ?: 0

                                            // 3. MENSAJE EXACTO QUE PEDISTE
                                            val mensaje = "Se han consumido $cantidad tokens\nSe clasificaron $cantidad correos"
                                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()

                                            // 4. Recargamos todo
                                            vm.cargarCategorias(token)

                                            // 5. ESTO ES LO QUE HACE QUE DESAPAREZCA EL BOTÓN
                                            // Al checar cuota, si es 0, 'canOrganize' cambia a false y el IF de arriba lo oculta.
                                            vm.checkQuota(token)

                                        } catch (e: HttpException) {
                                            if (e.code() == 429) {
                                                Toast.makeText(context, "⏳ La IA está descansando. Espera 15 min.", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "Error del servidor: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isOrganizing = false
                                        }
                                    }
                                }
                            },
                            // Cambia de color si está trabajando
                            containerColor = if (isOrganizing) Color.Gray else Color(0xFF2E7D32),
                            contentColor = Color.White,
                            icon = {
                                if (isOrganizing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.AutoAwesome, null)
                                }
                            },
                            text = {
                                Text(if (isOrganizing) "Organizando en segundo plano..." else "Organizar")
                            }
                        )
                    }
                    // --- 2. BOTÓN ORIGINAL: AGREGAR ---
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
            }
        ) { innerPadding ->

            // Usamos Box para manejar el padding del Scaffold correctamente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Padding del Scaffold interno
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding) // Padding de la Plantilla (Drawer)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
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
                                contentPadding = PaddingValues(bottom = 100.dp) // Espacio extra para los 2 botones
                            ) {
                                items(items = categorias.filter { !it.nombre.equals("Sin organizar", ignoreCase = true) },
                                    key = { it.id_categoria }) { cat ->
                                    // Obtener color (con fallback)
                                    val catColor = try {
                                        parseColor(cat.colorHex ?: "#1976D2")
                                    } catch (e: Exception) {
                                        Color.Gray
                                    }

                                    // Texto adaptativo según luminancia
                                    val textColor = if (catColor.luminance() > 0.5f) Color.Black else Color.White
                                    val subTextColor = textColor.copy(alpha = 0.85f)
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
                                            directions = setOf(
                                                DismissDirection.StartToEnd,
                                                DismissDirection.EndToStart
                                            ),
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
                                                            .background(
                                                                color.copy(alpha = progress.coerceIn(0.3f, 1f)),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .padding(horizontal = 20.dp),
                                                        contentAlignment = alignment
                                                    ) {
                                                        Icon(
                                                            icon,
                                                            contentDescription = null,
                                                            tint = Color.White.copy(
                                                                alpha = progress.coerceIn(0.5f, 1f)
                                                            )
                                                        )
                                                    }
                                                }
                                            },
                                            dismissContent = {
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            navController.navigate(
                                                                "CorreosCategoria/${cat.id_categoria}/${cat.nombre}"
                                                            )
                                                        },
                                                    colors = CardDefaults.cardColors(containerColor = catColor),
                                                    elevation = CardDefaults.cardElevation(4.dp)
                                                ) {
                                                    Row(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .padding(20.dp),
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

                                                        // --- ESTRELLA FAVORITO ---
                                                        val isFav = favoriteStates[cat.id_categoria] ?: false

                                                        IconButton(onClick = {
                                                            // Optimista: Cambiamos visualmente primero
                                                            favoriteStates[cat.id_categoria] = !isFav
                                                            scope.launch {
                                                                try {
                                                                    RetrofitClient.instance.toggleFavorito(cat.id_categoria, token)
                                                                    // Recargamos para que el orden se actualice en el backend
                                                                    vm.cargarCategorias(token)
                                                                } catch (e: Exception) {
                                                                    // Revertir si falla
                                                                    favoriteStates[cat.id_categoria] = isFav
                                                                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }) {
                                                            Icon(
                                                                imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                                                contentDescription = "Favorito",
                                                                tint = if (isFav) Color(0xFFFFD700) else subTextColor,
                                                                modifier = Modifier.size(28.dp)
                                                            )
                                                        }
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
            }

            // --------------------
            // DIÁLOGO CREAR / EDITAR (Sin cambios, igual a tu original)
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

                        Column(
                            Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
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
                            ColorPicker(
                                selectedColorHex = selectedColor,
                                onColorSelected = { selectedColor = it })

                            // PREVIEW CON TEXTO ADAPTATIVO
                            val previewColor =
                                try {
                                    parseColor(selectedColor)
                                } catch (e: Exception) {
                                    Color.Gray
                                }
                            val previewTextColor =
                                if (previewColor.luminance() > 0.5f) Color.Black else Color.White

                            Card(
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = CardDefaults.cardColors(containerColor = previewColor)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        "Así se verá el texto",
                                        color = previewTextColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
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
                                                    repo.agregarCategoria(
                                                        token,
                                                        nombre,
                                                        regla,
                                                        selectedColor
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Categoría creada",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    repo.editarCategoria(
                                                        token,
                                                        editingCategoria!!.id_categoria,
                                                        nombre,
                                                        regla,
                                                        selectedColor
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Categoría actualizada",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                vm.cargarCategorias(token)
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Error al guardar",
                                                    Toast.LENGTH_SHORT
                                                ).show()
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
            // DIÁLOGO CONFIRMAR ELIMINACIÓN (Sin cambios, igual a tu original)
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
                                        repo.eliminarCategoria(
                                            token,
                                            deletingCategoria!!.id_categoria
                                        )
                                        vm.cargarCategorias(token)
                                        Toast.makeText(
                                            context,
                                            "Categoría eliminada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error al eliminar",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
