package com.example.magfind1.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.SessionManager

@Composable
fun GoogleStyleProfileMenu(
    navController: NavController,
    userName: String,
    email: String
) {
    var expanded by remember { mutableStateOf(false) }

    // Avatar pequeño de la TopBar
    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .size(40.dp)
            .border(1.dp, Color.Black, CircleShape)
            .clip(CircleShape)
            .background(Color(0xFF1976D2))
            .clickable { expanded = true },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = userName.take(1).uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(300.dp)
            .background(Color(0xFFF7F9FC), RoundedCornerShape(16.dp))
            .padding(bottom = 8.dp)
    ) {

        // Cabecera del menú
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(1.dp, Color.Black, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = email, fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    expanded = false
                    navController.navigate("MiCuenta")
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text("Administrar tu Cuenta de MagFind")
            }
        }

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        // Agregar otra cuenta
        DropdownMenuItem(
            text = { Text("Agregar otra cuenta", fontWeight = FontWeight.Medium) },
            onClick = {
                expanded = false
                navController.navigate("Login")
            },
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
        )

        // Cerrar sesión (corregido)
        DropdownMenuItem(
            text = { Text("Cerrar sesión", fontWeight = FontWeight.Medium) },
            onClick = {
                expanded = false

                // Limpiar sesión de forma correcta
                val sm = SessionManager(navController.context)
                sm.clearSession()

                // Navegación segura
                navController.navigate("Login") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            },
            leadingIcon = { Icon(Icons.Filled.ExitToApp, contentDescription = null) }
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        // Footer opcional
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Política de Privacidad • Términos", fontSize = 11.sp, color = Color.Gray)
        }
    }
}
