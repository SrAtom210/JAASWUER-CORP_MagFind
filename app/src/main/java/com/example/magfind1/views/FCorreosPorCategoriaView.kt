package com.example.magfind1.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.magfind1.RetrofitClient
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.models.CorreoItemDto
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

// --- VIEW MODEL ---
class CorreosCategoriaViewModel : ViewModel() {
    var listaCorreos by mutableStateOf<List<CorreoItemDto>>(emptyList())
    var isLoading by mutableStateOf(true)

    fun cargar(token: String, idCategoria: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.obtenerCorreosPorCategoria(idCategoria, token)
                listaCorreos = res.correos
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}

// --- VISTA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fCorreosPorCategoriaView(
    navController: NavController,
    themeViewModel: ThemeViewModel, // Agregamos el ThemeViewModel para fPlantilla
    idCategoria: Int,
    nombreCategoria: String
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken() ?: ""
    val vm: CorreosCategoriaViewModel = viewModel()

    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) vm.cargar(token, idCategoria)
    }

    // Usamos fPlantilla para mantener el diseño consistente
    fPlantilla(
        title = nombreCategoria, // El título será el nombre de la carpeta (ej: "Seguridad")
        navController = navController,
        themeViewModel = themeViewModel,
        drawerItems = listOf(
            "Home" to { navController.navigate("Home") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Categorías" to { navController.navigate("Categorias") },
            "Mi Cuenta" to { navController.navigate("MiCuenta") },
            "Suscripción" to { navController.navigate("Suscripcion") },
            "Ajustes" to { navController.navigate("Ajustes") },
            "Ayuda" to { navController.navigate("Ayuda") }
        )

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Respetamos el espacio del header de fPlantilla
                .background(Color(0xFFF5F5F5))
        ) {
            if (vm.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF1976D2)
                )
            } else if (vm.listaCorreos.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Esta carpeta está vacía",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vm.listaCorreos) { correo ->
                        CorreoItemSimple(correo) {
                            // Navegación al detalle del correo
                            navController.navigate("DetalleCorreo/${correo.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CorreoItemSimple(correo: CorreoItemDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = correo.fecha.take(10),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = correo.asunto,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
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