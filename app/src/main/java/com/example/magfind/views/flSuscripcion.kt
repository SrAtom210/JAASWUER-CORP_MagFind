package com.example.magfind.views

import android.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.magfind.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fSuscripcionView(navController: NavController,themeViewModel: ThemeViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedPlan by remember { mutableStateOf("Essential") }

    fPlantilla(
        title = "Suscripción",
        navController,themeViewModel = themeViewModel,
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
                    description = "• Sincronización con 1 cuenta\n" +
                            "• Clasificación con IA\n" +
                            "• Hasta 5 Categorías\n" +
                            "• Aprendizaje Básico\n" +
                            "• Búsqueda Estándar\n" +
                            "• Soporte Comunitario.",
                    isSelected = selectedPlan == "Essential",
                    selectedColor = Color(0xFFD6E6F7),
                    onSelect = { selectedPlan = "Essential" }
                )

                PlanCard(
                    title = "Plus",
                    price = "$50 / mes",
                    description = "• Hasta 50 Categorías\n" +
                            "• Sincronización con 3 cuentas\n" +
                            "• Reglas avanzadas\n" +
                            "• Reentrenamiento Semanal de IA\n" +
                            "• Sin Anuncios",
                    isSelected = selectedPlan == "Plus",
                    selectedColor = Color(0xFFDDD3E0),
                    onSelect = { selectedPlan = "Plus" }
                )

                PlanCard(
                    title = "Platinum",
                    price = "$150 / mes",
                    description = "• Sincronización con 10 Cuentas\n" +
                            "• Reentrenamiento Diario de IA\n" +
                            "• Notificaciones Inteligentes\n" +
                            "• Busqueda Avanzada\n" +
                            "• Sincronización multi-dispositivos\n" +
                            "• Soporte Premium",
                    isSelected = selectedPlan == "Platinum",
                    selectedColor = Color(0xFFF7F7F7),
                    onSelect = { selectedPlan = "Platinum" }
                )

                PlanCard(
                    title = "Business",
                    price = "$199 / mes",
                    description = "• Cuentas Ilimitadas \n" +
                            "• Panel de Administración \n" +
                            "• Categorías Compartidas \n" +
                            "• Asignación de Correos \n" +
                            "• Soporte Dedicado \n" +
                            "• Integraciones Futuras",
                    isSelected = selectedPlan == "Business",
                    selectedColor = Color(0xFFF9F9F0),
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
    selectedColor: Color, // ✅ corregido: coma fuera de lugar
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
        animationSpec = tween(durationMillis = 800, easing = LinearEasing)
    )

    // 🔹 Animación del grosor del borde
    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 2.dp,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
    )

    // ✨ Animación ligera de elevación
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 6.dp else 2.dp,
        animationSpec = tween(durationMillis = 400)
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
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colorPlan, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(price, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                description,
                fontSize = 16.sp,
                textAlign = TextAlign.Left,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
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

