package com.example.magfind1.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.RetrofitClient
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.models.ApiService
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder // Importación necesaria para el encode

@Composable
fun fSuscripcionView(navController: NavController, themeViewModel: ThemeViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedPlan by remember { mutableStateOf("Essential") }

    fPlantilla(
        title = "Suscripción",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Elige tu plan",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- TUS PLANES ---
            PlanCard(
                title = "Essential",
                price = "Gratis",
                description = "• Sincronización con 1 cuenta\n• Clasificación con IA\n• Hasta 5 Categorías\n• Soporte Básico",
                isSelected = selectedPlan == "Essential",
                selectedColor = Color(0xFFD6E6F7),
                onSelect = { selectedPlan = "Essential" }
            )

            PlanCard(
                title = "Plus",
                price = "$50 / mes",
                description = "• Hasta 50 Categorías\n• 3 cuentas\n• Reglas avanzadas\n• Sin anuncios",
                isSelected = selectedPlan == "Plus",
                selectedColor = Color(0xFFDDD3E0),
                onSelect = { selectedPlan = "Plus" }
            )

            PlanCard(
                title = "Platinum",
                price = "$150 / mes",
                description = "• 10 cuentas\n• Notificaciones avanzadas\n• IA diaria\n• Soporte Premium",
                isSelected = selectedPlan == "Platinum",
                selectedColor = Color(0xFFF7F7F7),
                onSelect = { selectedPlan = "Platinum" }
            )

            PlanCard(
                title = "Business",
                price = "$199 / mes",
                description = "• Ilimitado\n• Panel Admin\n• Categorías Compartidas\n• Integraciones",
                isSelected = selectedPlan == "Business",
                selectedColor = Color(0xFFF9F9F0),
                onSelect = { selectedPlan = "Business" }
            )

            Spacer(Modifier.height(40.dp))


            // ---------- BOTÓN CONFIRMAR (Con lógica de Debug integrada) ----------
            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(Color(0xFF1976D2)),
                onClick = {
                    println("DEBUG: Inicio click plan = $selectedPlan")

                    scope.launch(Dispatchers.IO) {
                        try {
                            val api = RetrofitClient.retrofit.create(ApiService::class.java)
                            println("DEBUG: API inicializada correctamente")

                            // Usamos orEmpty() por seguridad, aunque en el log salga el valor real
                            val emailDebug = SessionManager.email.orEmpty()
                            println("DEBUG: Email = $emailDebug")

                            // 1. Si el plan es gratuito
                            if (selectedPlan == "Essential") {
                                println("DEBUG: Essential → regreso a Home")
                                withContext(Dispatchers.Main) { navController.navigate("Home") }
                                return@launch
                            }

                            // 2. Si es plan de pago, pedimos SetupIntent
                            println("DEBUG: Solicitando SetupIntent...")
                            val response = api.createSetupIntent(mapOf("email" to emailDebug))

                            println("DEBUG: Respuesta Stripe = $response")

                            // Validaciones de respuesta
                            if (response.client_secret.isNullOrEmpty()) {
                                println("DEBUG ERROR: client_secret vacío")
                            }
                            if (response.customer_id.isNullOrEmpty()) {
                                println("DEBUG ERROR: customer_id vacío")
                            }

                            if (!response.client_secret.isNullOrEmpty() && !response.customer_id.isNullOrEmpty()) {
                                // Encoders para pasar parámetros seguros por URL
                                val secret = URLEncoder.encode(response.client_secret, "UTF-8")
                                val custId = URLEncoder.encode(response.customer_id, "UTF-8")

                                println("DEBUG: Navegando a StripeSetup/$secret/$custId/$selectedPlan")

                                withContext(Dispatchers.Main) {
                                    navController.navigate("StripeSetup/$secret/$custId/$selectedPlan")
                                }
                            } else {
                                println("DEBUG ERROR: No se puede navegar, faltan datos de Stripe")
                            }

                        } catch (e: Exception) {
                            println("DEBUG CRASH: ${e.localizedMessage}")
                            e.printStackTrace()
                        }
                    }
                }
            ) {
                Text("Confirmar plan", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    description: String,
    isSelected: Boolean,
    selectedColor: Color,
    onSelect: () -> Unit
) {
    // 🎨 Color base del plan (según nombre)
    val colorPlan = when (title) {
        "Essential" -> Color(0xFF3084D7)
        "Plus" -> Color(0xFF572364)
        "Platinum" -> Color(0xFF3A3A3A)
        "Business" -> Color(0xFF998100)
        else -> Color(0xFFD1E9FF)
    }

    // 🪄 Animación del color del borde
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) colorPlan else Color.Transparent,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "BorderColor"
    )

    // 🔹 Animación del grosor del borde
    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 2.dp,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "BorderWidth"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(vertical = 10.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(animatedBorderWidth, animatedBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) selectedColor else Color(0xFFF7FBFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorPlan,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                price,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                description,
                fontSize = 16.sp,
                textAlign = TextAlign.Left,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF1976D2) else Color.White
                ),
                border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder,
            ) {
                Text(
                    if (isSelected) "Seleccionado" else "Seleccionar",
                    color = if (isSelected) Color.White else Color(0xFF1976D2)
                )
            }
        }
    }
}