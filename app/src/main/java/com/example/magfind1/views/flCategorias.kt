package com.example.magfind1.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import com.example.magfind1.models.CategoriaDto
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.viewmodels.CategoriasViewModel
import com.example.magfind1.components.ColorPicker
import com.example.magfind1.components.parseColor
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
            bottomBar = {
                // Bloque condicional para futuro Plan Essential
                if (true) {
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
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        editingCategoria = null
                        showDialog = true
                    },
                    containerColor = Color(0xFF1976D2),
                    modifier = Modifier.padding(bottom = if(true) 60.dp else 8.dp)
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

                                // 1. OBTENER EL COLOR DE FONDO
                                val catColor = try {
                                    parseColor(cat.colorHex)
                                } catch (e: Exception) {
                                    Color.Gray
                                }

                                // 2. CALCULAR COLOR DE TEXTO ADAPTATIVO (Blanco o Negro)
                                // Si el brillo es alto (> 0.5), el fondo es claro -> texto negro.
                                // Si el brillo es bajo (< 0.5), el fondo es oscuro -> texto blanco.
                                val textColor = if (catColor.luminance() > 0.5f) Color.Black else Color.White
                                val subTextColor = textColor.copy(alpha = 0.8f)

                                val esSistema = cat.nombre.equals("Sin organizar", ignoreCase = true)

                                if (esSistema) {
                                    // --- TARJETA DE SISTEMA (Gris claro, texto gris oscuro) ---
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
                                                Text(
                                                    text = "Carpeta de sistema",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                } else {
                                    // --- TARJETA DE USUARIO (FONDO DE COLOR) ---
                                    val dismissState = rememberDismissState(
                                        confirmStateChange = {
                                            if (it == DismissValue.DismissedToEnd) {
                                                editingCategoria = cat
                                                showDialog = true
                                                false
                                            } else if (it == DismissValue.DismissedToStart) {
                                                deletingCategoria = cat
                                                showDeleteDialog = true
                                                false
                                            } else {
                                                false
                                            }
                                        }
                                    )

                                    SwipeToDismiss(
                                        state = dismissState,
                                        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                                        background = {
                                            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                                            val color = when (direction) {
                                                DismissDirection.StartToEnd -> Color(0xFF4CAF50)
                                                DismissDirection.EndToStart -> Color(0xFFE57373)
                                            }
                                            val alignment = when (direction) {
                                                DismissDirection.StartToEnd -> Alignment.CenterStart
                                                DismissDirection.EndToStart -> Alignment.CenterEnd
                                            }
                                            val icon = when (direction) {
                                                DismissDirection.StartToEnd -> Icons.Default.Edit
                                                DismissDirection.EndToStart -> Icons.Default.Delete
                                            }

                                            Box(
                                                Modifier
                                                    .fillMaxSize()
                                                    .background(color, RoundedCornerShape(12.dp))
                                                    .padding(horizontal = 20.dp),
                                                contentAlignment = alignment
                                            ) {
                                                Icon(icon, contentDescription = null, tint = Color.White)
                                            }
                                        },
                                        dismissContent = {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        Toast.makeText(context, "Ver correos de: ${cat.nombre}", Toast.LENGTH_SHORT).show()
                                                    },
                                                // FONDO: Usamos el color de la categoría
                                                colors = CardDefaults.cardColors(containerColor = catColor),
                                                elevation = CardDefaults.cardElevation(4.dp)
                                            ) {
                                                Row(
                                                    Modifier.fillMaxWidth().padding(20.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        // TEXTO: Usamos el color calculado (Blanco o Negro)
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

                                                    // FLECHA: También adaptativa
                                                    Icon(
                                                        imageVector = Icons.Default.ChevronRight,
                                                        contentDescription = "Ver",
                                                        tint = subTextColor
                                                    )
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

            // DIÁLOGO CREAR/EDITAR
            if (showDialog) {
                Dialog(
                    onDismissRequest = { showDialog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        var nombre by remember { mutableStateOf(editingCategoria?.nombre ?: "") }
                        var regla by remember { mutableStateOf(editingCategoria?.regla ?: "") }
                        var selectedColor by remember { mutableStateOf(editingCategoria?.colorHex ?: "#1976D2") }

                        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = if (editingCategoria == null) "Nueva Categoría" else "Editar Categoría",
                                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2)
                            )
                            OutlinedTextField(
                                value = nombre, onValueChange = { nombre = it },
                                label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                            )
                            OutlinedTextField(
                                value = regla, onValueChange = { regla = it },
                                label = { Text("Regla") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                            )

                            Text("Color de la tarjeta:", fontWeight = FontWeight.Medium)
                            ColorPicker(selectedColorHex = selectedColor, onColorSelected = { selectedColor = it })

                            // PREVIEW EN EL DIÁLOGO CON TEXTO ADAPTATIVO
                            val previewColor = try { parseColor(selectedColor) } catch(e:Exception){ Color.Gray }
                            val previewTextColor = if (previewColor.luminance() > 0.5f) Color.Black else Color.White

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
                                Button(
                                    onClick = {
                                        if (nombre.isBlank()) return@Button
                                        scope.launch {
                                            val repo = CategoriaRepository()
                                            if (editingCategoria == null) {
                                                repo.agregarCategoria(token, nombre, regla, selectedColor)
                                            } else {
                                                repo.editarCategoria(token, editingCategoria!!.id_categoria, nombre, regla, selectedColor)
                                            }
                                            vm.cargarCategorias(token)
                                            showDialog = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                                ) { Text("Guardar") }
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog && deletingCategoria != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("¿Eliminar Categoría?") },
                    text = { Text("Se eliminará \"${deletingCategoria!!.nombre}\".") },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    val repo = CategoriaRepository()
                                    repo.eliminarCategoria(token, deletingCategoria!!.id_categoria)
                                    vm.cargarCategorias(token)
                                    showDeleteDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) { Text("Eliminar") }
                    },
                    dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
                )
            }
        }
    }
}