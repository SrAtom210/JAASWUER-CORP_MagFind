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
import coil.compose.AsyncImage
import com.example.magfind1.SessionManager

@Composable
fun GoogleStyleProfileMenu(
    navController: NavController,
    userName: String,
    email: String
) {
    var expanded by remember { mutableStateOf(false) }

    val session = SessionManager(navController.context)
    val photo = session.getProfilePhoto()

    // ========================= AVATAR PEQUEÑO =========================
    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .clickable { expanded = true },
        contentAlignment = Alignment.Center
    ) {
        if (!photo.isNullOrEmpty()) {
            AsyncImage(
                model = photo,
                contentDescription = "Foto",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = userName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }

    // ======================= MENÚ ESTILO GOOGLE =========================
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(300.dp)
            .background(Color(0xFFF7F9FC), RoundedCornerShape(16.dp))
            .padding(bottom = 8.dp)
    ) {

        // --------------------- CABECERA ---------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO GRANDE
            if (!photo.isNullOrEmpty()) {
                AsyncImage(
                    model = photo,
                    contentDescription = "Foto perfil",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                )
            } else {
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

        // ------------------- AGREGAR OTRA CUENTA -------------------
        DropdownMenuItem(
            text = { Text("Agregar otra cuenta", fontWeight = FontWeight.Medium) },
            onClick = {
                expanded = false
                navController.navigate("Login")
            },
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
        )

        // ------------------- CERRAR SESIÓN -------------------
        DropdownMenuItem(
            text = { Text("Cerrar sesión", fontWeight = FontWeight.Medium) },
            onClick = {
                expanded = false
                val sm = SessionManager(navController.context)
                sm.clearSession()

                navController.navigate("Login") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            },
            leadingIcon = { Icon(Icons.Filled.ExitToApp, contentDescription = null) }
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

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
