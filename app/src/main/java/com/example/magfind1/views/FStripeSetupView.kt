package com.example.magfind1.views

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.magfind1.models.SubscriptionResponse
import com.example.magfind1.models.ApiService
import com.example.magfind1.RetrofitClient
import com.example.magfind1.SessionManager
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StripePremiumSetupView(
    activity: ComponentActivity,
    clientSecret: String,
    customerId: String,
    plan: String
) {
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val paymentSheet = remember {
        PaymentSheet(activity) { result ->
            scope.launch {
                handlePaymentResult(result, activity, plan, customerId)
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            paymentSheet.presentWithSetupIntent(
                clientSecret,
                PaymentSheet.Configuration(
                    merchantDisplayName = "MagFind",
                    customer = PaymentSheet.CustomerConfiguration(
                        id = customerId,
                        ephemeralKeySecret = "" // No requerido para SetupIntent
                    )
                )
            )
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Error desconocido"
            isLoading = false
            e.printStackTrace()
        }
    }

    // UI PREMIUM
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {

        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let { msg ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(msg, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(20.dp))
                Button(onClick = { activity.finish() }) {
                    Text("Cerrar")
                }
            }
        }
    }
}

suspend fun handlePaymentResult(
    result: PaymentSheetResult,
    activity: Activity,
    plan: String,
    customerId: String
) {
    when (result) {

        is PaymentSheetResult.Canceled -> {
            Toast.makeText(activity, "Pago cancelado", Toast.LENGTH_LONG).show()
            activity.finish()
        }

        is PaymentSheetResult.Failed -> {
            Toast.makeText(activity, "Error: ${result.error.localizedMessage}", Toast.LENGTH_LONG).show()
            activity.finish()
        }

        is PaymentSheetResult.Completed -> {
            Toast.makeText(activity, "Método de pago guardado, activando plan...", Toast.LENGTH_LONG).show()

            activarPlanEnBackend(activity, plan, customerId)

            activity.finish()
        }
    }
}

suspend fun activarPlanEnBackend(
    activity: Activity,
    plan: String,
    customerId: String
) {
    try {
        val api = RetrofitClient.retrofit.create(ApiService::class.java)

        val response: SubscriptionResponse = api.createSubscription(
            mapOf(
                "customerId" to customerId,
                "plan" to plan
            )
        )

        withContext(Dispatchers.Main) {
            if (response.status == "active" || response.status == "trialing") {
                Toast.makeText(activity, "Suscripción activada", Toast.LENGTH_LONG).show()

                // Guardamos plan en SessionManager
                SessionManager.username = plan

            } else {
                Toast.makeText(activity, "Pago hecho pero plan no activado", Toast.LENGTH_LONG).show()
            }
        }

    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(activity, "Error backend: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
        e.printStackTrace()
    }
}
