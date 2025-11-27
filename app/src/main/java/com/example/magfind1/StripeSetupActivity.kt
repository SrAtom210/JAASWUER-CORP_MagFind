package com.example.magfind1

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.ApiService

class StripeSetupActivity : ComponentActivity() {

    private lateinit var paymentSheet: PaymentSheet
    private var clientSecret: String = ""
    private var customerId: String = ""
    private var plan: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clientSecret = intent.getStringExtra("clientSecret") ?: ""
        customerId = intent.getStringExtra("customerId") ?: ""
        plan = intent.getStringExtra("plan") ?: ""

        if (clientSecret.isEmpty()) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        // ⚠ IMPORTANTE: inicializar Stripe antes de usar PaymentSheet
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51SWWvO2LjLlB5Um0MayyyBlfJQHNelDgPUpMtusWwkjLL0dm0N3fXfnlF409jBm2H9stjdbXwesB8XEUE9oO2gpt008yoVZNIS"    // <-- TU PUBLICABLE KEY DE STRIPE
        )

        paymentSheet = PaymentSheet(
            this,
            ::onPaymentSheetResult
        )

        setContent {
            StripeSetupScreen(
                onReady = {
                    presentPaymentSheet()
                }
            )
        }
    }

    private fun presentPaymentSheet() {
        val config = PaymentSheet.Configuration(
            merchantDisplayName = "MagFind"
        )

        paymentSheet.presentWithSetupIntent(
            clientSecret,
            config
        )
    }

    private fun onPaymentSheetResult(result: PaymentSheetResult) {
        when (result) {
            is PaymentSheetResult.Completed -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
            is PaymentSheetResult.Canceled -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            is PaymentSheetResult.Failed -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }
}

@Composable
fun StripeSetupScreen(onReady: () -> Unit) {
    LaunchedEffect(Unit) {
        onReady()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Cargando Stripe...", color = Color.Gray)
    }
}
