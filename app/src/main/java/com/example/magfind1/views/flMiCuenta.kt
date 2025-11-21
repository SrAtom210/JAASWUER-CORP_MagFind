package com.example.magfind1.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.viewmodels.CuentaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fCuentaView(navController: NavController, themeViewModel: ThemeViewModel) {

    val cuentaVM: CuentaViewModel = viewModel()

    val cuenta = cuentaVM.cuenta.value
    val error = cuentaVM.error.value
    val loading = cuentaVM.loading.value

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken()

    // Cargar info al iniciar
    LaunchedEffect(Unit) {
        cuentaVM.cargarCuenta(token)
    }

    fPlantilla(
        title = "Mi Cuenta",
        navController = navController,
        themeViewModel = themeViewModel,
        drawerItems = listOf(
            "Home" to { navController.navigate("Home") },
            "Ajustes" to { navController.navigate("Ajustes") },
            "Categorías" to { navController.navigate("Categorias") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Mi Cuenta" to { navController.navigate("MiCuenta") },
            "Suscripcion" to { navController.navigate("Suscripcion") }
        )
    ) { innerPadding ->

        when {
            // ======================== LOADING ========================
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }
            }

            // ======================== ERROR ==========================
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = Color.Red)
                }
            }

            // ======================== CUENTA ==========================
            cuenta != null -> {

                // Datos guardados de Login con Google o nombre local
                val photoUrl = sessionManager.getProfilePhoto()
                val displayName = sessionManager.getDisplayName() ?: cuenta.nombre

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {

                    // FOTO / INICIAL
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCE9FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!photoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = displayName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 50.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre dinámico
                    Text(
                        displayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )

                    // Correo
                    Text(
                        sessionManager.getEmail() ?: cuenta.email,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )


                    Spacer(modifier = Modifier.height(30.dp))

                    // Detalles
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Detalles de la cuenta", fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                            Spacer(modifier = Modifier.height(8.dp))
                            fInfoRow("Plan actual", cuenta.tipo_suscripcion ?: "Sin plan")
                            fInfoRow("Fecha de registro", cuenta.fecha_registro.take(10))
                            fInfoRow("Inicio del plan", cuenta.fecha_inicio ?: "-")
                            fInfoRow("Fin del plan", cuenta.fecha_fin ?: "-")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { /* editar perfil */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("Editar perfil", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun fInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp, color = Color.Black)
        Text(value, fontSize = 16.sp, color = Color.Gray)
    }
}
