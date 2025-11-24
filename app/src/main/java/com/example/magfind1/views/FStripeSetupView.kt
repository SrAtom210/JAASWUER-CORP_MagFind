package com.example.magfind1.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- IMPORTS CLAVE PARA STRIPE 20.48.0 ---
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetContract
import com.stripe.android.paymentsheet.PaymentSheetResult

@Composable
fun StripeSetupView(
    navController: NavController,
    clientSecret: String,
    customerId: String,
    plan: String
) {
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Confirma tu método de pago") }

    // 1. Configuración del Launcher
    val stripeLauncher = rememberLauncherForActivityResult(
        contract = PaymentSheetContract()
    ) { result ->
        when (result) {
            is PaymentSheetResult.Canceled -> {
                isLoading = false
                statusMessage = "Operación cancelada"
            }
            is PaymentSheetResult.Failed -> {
                isLoading = false
                statusMessage = "Error en Stripe: ${result.error.localizedMessage}"
                println("DEBUG STRIPE ERROR: ${result.error}")
            }
            is PaymentSheetResult.Completed -> {
                statusMessage = "Tarjeta guardada. Creando suscripción..."

                // 2. Notificar al Backend
                scope.launch(Dispatchers.IO) {
                    activarSuscripcionEnBackend(customerId, plan, navController) { errorMsg ->
                        statusMessage = errorMsg
                        isLoading = false
                    }
                }
            }
        }
    }

    fun openStripeSheet() {
        isLoading = true

        // Configuración visual básica de Stripe
        val configuration = PaymentSheet.Configuration(
            merchantDisplayName = "MagFind",
            allowsDelayedPaymentMethods = true
        )

        try {
            // CON STRIPE 20.48.0 ESTA LÍNEA DEBE FUNCIONAR:
            // Se usa el método estático createSetupIntent
            val args = PaymentSheetContract.Args.createSetupIntent(
                clientSecret = clientSecret,
                config = configuration
            )
            stripeLauncher.launch(args)

        } catch (e: Exception) {
            isLoading = false
            statusMessage = "Error al iniciar Stripe: ${e.message}"
            e.printStackTrace()
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Suscripción: $plan",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = statusMessage,
                color = if (statusMessage.contains("Error")) Color.Red else Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { openStripeSheet() },
                colors = ButtonDefaults.buttonColors(Color(0xFF1976D2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar Tarjeta")
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Cancelar")
            }
        }
    }
}

// --- LÓGICA BACKEND ---
suspend fun activarSuscripcionEnBackend(
    customerId: String,
    plan: String,
    navController: NavController,
    onError: suspend (String) -> Unit
) {
    try {
        val api = RetrofitClient.retrofit.create(ApiService::class.java)

        println("DEBUG: Enviando a backend -> Customer: $customerId, Plan: $plan")

        val response = api.createSubscription(
            mapOf(
                "customerId" to customerId,
                "plan" to plan
            )
        )

        withContext(Dispatchers.Main) {
            // Verificamos el status según tu modelo
            if (response.status == "active" || response.status == "succeeded" || response.status == "trialing") {
                println("DEBUG: Suscripción ACTIVADA correctamente")
                navController.navigate("Home") {
                    popUpTo("Suscripcion") { inclusive = true }
                }
            } else {
                onError("Pago procesado pero suscripción no activa. Estado: ${response.status}")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            onError("Error de conexión: ${e.localizedMessage}")
        }
    }
}