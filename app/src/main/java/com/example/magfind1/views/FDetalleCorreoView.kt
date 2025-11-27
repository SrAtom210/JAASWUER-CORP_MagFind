package com.example.magfind1.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.RetrofitClient
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.models.CorreoDetalleResponse
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fDetalleCorreoView(
    navController: NavController,
    emailId: Int,
    themeViewModel: ThemeViewModel
) {
    // Estados para la lógica de carga
    var correoDetalle by remember { mutableStateOf<CorreoDetalleResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Configuración de sesión
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken() ?: ""

    // Para el drawer de fPlantilla
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cargar datos al iniciar la pantalla
    LaunchedEffect(emailId) {
        try {
            val api = RetrofitClient.instance
            correoDetalle = api.obtenerDetalleCorreo(emailId, token)
        } catch (e: Exception) {
            errorMsg = "No se pudo cargar el correo. Verifica tu conexión."
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // Usamos fPlantilla para mantener el diseño consistente
    fPlantilla(
        title = "Lectura de Correo",
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
    ) {
        // Scaffold interno
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Detalle", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1976D2))
                )
            },
            floatingActionButton = {
                // Botón flotante para regresar a la lista
                FloatingActionButton(
                    onClick = { navController.popBackStack() },
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF5F5F5))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF1976D2)
                    )
                } else if (errorMsg != null) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = errorMsg!!, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                } else {
                    // CONTENIDO DEL CORREO COMPLETO
                    correoDetalle?.let { mail ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            // Tarjeta de Cabecera
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = mail.asunto,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0D47A1)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color.LightGray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "De: ${mail.remitente}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Fecha: ${mail.fecha}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Cuerpo del mensaje
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(1.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp)
                            ) {
                                Text(
                                    text = mail.cuerpo_completo,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}