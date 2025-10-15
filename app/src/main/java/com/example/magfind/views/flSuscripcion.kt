package com.example.magfind.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind.components.fPlantilla
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fSuscripcionView(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedPlan by remember { mutableStateOf("Essential") }

    fPlantilla(
        title = "Suscripción",
        drawerItems = listOf(
            "Home" to { navController.navigate("Home")},
            "Ajustes" to { navController.navigate("Ajustes")},
            "Categorías" to { navController.navigate("Categorias") },
            "Correos" to { navController.navigate("CorreosCat") },
            "Mi Cuenta" to { navController.navigate("MiCuenta")},
            "Suscripcion" to { navController.navigate("Suscripcion")}
        )
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Elige tu plan",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                PlanCard(
                    title = "Essential",
                    price = "Gratis",
                    description = "Ideal para comenzar.\nIncluye funciones básicas y limitadas.",
                    isSelected = selectedPlan == "Essential",
                    onSelect = { selectedPlan = "Essential" }
                )

                PlanCard(
                    title = "Plus",
                    price = "$4.99 / mes",
                    description = "Funciones avanzadas y mayor almacenamiento.",
                    isSelected = selectedPlan == "Plus",
                    onSelect = { selectedPlan = "Plus" }
                )

                PlanCard(
                    title = "Platinum",
                    price = "$9.99 / mes",
                    description = "Acceso completo con soporte prioritario.",
                    isSelected = selectedPlan == "Platinum",
                    onSelect = { selectedPlan = "Platinum" }
                )

                PlanCard(
                    title = "Business",
                    price = "$19.99 / mes",
                    description = "Diseñado para equipos y empresas.\nGestión multiusuario y estadísticas.",
                    isSelected = selectedPlan == "Business",
                    onSelect = { selectedPlan = "Business" }
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { /* Acción para confirmar plan */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    modifier = Modifier.fillMaxWidth(0.8f)
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
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(vertical = 8.dp)
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFD1E9FF) else Color(0xFFF7FBFF)
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            Text(price, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(
                description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
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
