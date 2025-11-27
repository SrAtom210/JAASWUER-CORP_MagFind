package com.example.magfind1.views

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

@Composable
fun fSuscripcionView(navController: NavController, themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val session = remember { SessionManager(context) }
    val planActual = session.getPlan()?.trim()?.lowercase() ?: "essential"


    val stripeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            navController.navigate("Home") {
                popUpTo("Suscripcion") { inclusive = true }
            }
        }
    }

    fun iniciarStripe(plan: String) {
        scope.launch(Dispatchers.IO) {
            val api = RetrofitClient.retrofit.create(ApiService::class.java)
            val email = SessionManager.email.orEmpty()

            val res = api.createSetupIntent(mapOf("email" to email))

            val secret = res.client_secret
            val cust = res.customer_id

            val intent = Intent(context, StripeSetupActivity::class.java)
            intent.putExtra("clientSecret", secret)
            intent.putExtra("customerId", cust)
            intent.putExtra("plan", plan)

            stripeLauncher.launch(intent)
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
            "Ayuda" to { navController.navigate("Ayuda") }
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

             // PLANES CON CONFIRMAR ADENTRO
            PlanCard(
                title = "Essential",
                price = "Gratis",
                description = "• 1 cuenta • AI básica • Hasta 5 categorías • Soporte básico",
                isCurrent = planActual == "essential",
                onConfirm = {
                    // Si NO es essential → activamos plan y navegamos
                    if (planActual != "essential") {
                        session.savePlan("essential")
                        navController.navigate("Home")
                    }
                }
            )


            PlanCard(
                title = "Plus",
                price = "$50 / mes",
                description = "• 3 cuentas • Hasta 50 categorías • Reglas avanzadas • Sin anuncios",
                isCurrent = planActual == "plus",
                onConfirm = { iniciarStripe("Plus") }
            )


            PlanCard(
                title = "Platinum",
                price = "$150 / mes",
                description = "• 10 cuentas • Notificaciones avanzadas • IA diaria • Soporte Premium",
                isCurrent = planActual == "platinum",
                onConfirm = { iniciarStripe("Platinum") }
            )


            PlanCard(
                title = "Business",
                price = "$199 / mes",
                description = "• Ilimitado • Panel admin • Categorías compartidas • Integraciones",
                isCurrent = planActual == "business",
                onConfirm = { iniciarStripe("Business") }
            )
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
    isCurrent: Boolean,
    onConfirm: () -> Unit
) {

    val colorPlan = when (title) {
        "Essential" -> Color(0xFF3084D7)
        "Plus" -> Color(0xFF572364)
        "Platinum" -> Color(0xFF3A3A3A)
        "Business" -> Color(0xFF998100)
        else -> Color(0xFFD1E9FF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFE))
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colorPlan)
            Text(price, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)

            Spacer(Modifier.height(10.dp))

            Text(description, fontSize = 16.sp, color = Color.Black, textAlign = TextAlign.Center)

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { if (!isCurrent) onConfirm() },
                enabled = !isCurrent,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrent) Color.Gray else colorPlan,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(
                    text = if (isCurrent) "Actual" else "Confirmar plan",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

