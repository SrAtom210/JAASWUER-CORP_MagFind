package com.example.magfind1.views

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.RetrofitClient
import com.example.magfind1.SessionManager
import com.example.magfind1.StripeSetupActivity
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.models.ApiService
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun fSuscripcionView(navController: NavController, themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedPlan by remember { mutableStateOf("Essential") }

    // 🔥 Nuevo launcher para StripeSetupActivity
    val stripeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            println("DEBUG: StripeSetupActivity regresó OK")

            // Aquí activamos plan en backend si quieres
            navController.navigate("Home") {
                popUpTo("Suscripcion") { inclusive = true }
            }
        } else {
            println("DEBUG: StripeSetupActivity cancelado o fallido")
        }
    }

    fPlantilla(
        title = "Suscripción",
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

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Elige tu plan",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ---- PLANES ----
            PlanCard("Essential", "Gratis",
                "• 1 cuenta • AI básica • Hasta 5 categorías • Soporte básico",
                selectedPlan == "Essential", Color(0xFFD6E6F7)
            ) { selectedPlan = "Essential" }

            PlanCard("Plus", "$50 / mes",
                "• 3 cuentas • Hasta 50 categorías • Reglas avanzadas • Sin anuncios",
                selectedPlan == "Plus", Color(0xFFDDD3E0)
            ) { selectedPlan = "Plus" }

            PlanCard("Platinum", "$150 / mes",
                "• 10 cuentas • Notificaciones avanzadas • IA diaria • Soporte Premium",
                selectedPlan == "Platinum", Color(0xFFF7F7F7)
            ) { selectedPlan = "Platinum" }

            PlanCard("Business", "$199 / mes",
                "• Ilimitado • Panel admin • Categorías compartidas • Integraciones",
                selectedPlan == "Business", Color(0xFFF9F9F0)
            ) { selectedPlan = "Business" }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(Color(0xFF1976D2)),
                onClick = {
                    println("DEBUG: Inicio click en plan = $selectedPlan")

                    if (selectedPlan == "Essential") {
                        navController.navigate("Home")
                        return@Button
                    }

                    scope.launch(Dispatchers.IO) {
                        val api = RetrofitClient.retrofit.create(ApiService::class.java)
                        val email = SessionManager.email.orEmpty()

                        val res = api.createSetupIntent(mapOf("email" to email))

                        val secret = res.client_secret   // NO ENCODE
                        val cust = res.customer_id       // NO ENCODE

                        val intent = Intent(context, StripeSetupActivity::class.java)
                        intent.putExtra("clientSecret", secret)
                        intent.putExtra("customerId", cust)
                        intent.putExtra("plan", selectedPlan)

                        stripeLauncher.launch(intent)
                    }
                }
            ) {
                Text("Confirmar plan", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

// --------------------------------------------------------
// ---------------------- PLAN CARD ------------------------
// --------------------------------------------------------
@Composable
fun PlanCard(
    title: String,
    price: String,
    description: String,
    isSelected: Boolean,
    selectedColor: Color,
    onSelect: () -> Unit
) {
    val colorPlan = when (title) {
        "Essential" -> Color(0xFF3084D7)
        "Plus" -> Color(0xFF572364)
        "Platinum" -> Color(0xFF3A3A3A)
        "Business" -> Color(0xFF998100)
        else -> Color(0xFFD1E9FF)
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) colorPlan else Color.Transparent,
        animationSpec = tween(800, easing = LinearEasing)
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 2.dp,
        animationSpec = tween(500, easing = LinearOutSlowInEasing)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(vertical = 10.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(animatedBorderWidth, animatedBorderColor),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) selectedColor else Color(0xFFF7FBFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colorPlan)
            Text(price, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(Modifier.height(10.dp))
            Text(description, fontSize = 16.sp, color = Color.Black)
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF1976D2) else Color.White
                ),
                border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
            ) {
                Text(
                    if (isSelected) "Seleccionado" else "Seleccionar",
                    color = if (isSelected) Color.White else Color(0xFF1976D2)
                )
            }
        }
    }
}

suspend fun activarSuscripcionBackend(customerId: String, plan: String) {
    val api = RetrofitClient.retrofit.create(ApiService::class.java)
    api.createSubscription(mapOf("customerId" to customerId, "plan" to plan))
}
