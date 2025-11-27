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
import androidx.compose.ui.platform.LocalUriHandler
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

    val colors = MaterialTheme.colorScheme

    // ========================= AVATAR PEQUEÑO =========================
    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(colors.primary.copy(alpha = 0.3f))
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
                color = colors.onPrimaryContainer,
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
            .background(colors.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(bottom = 8.dp)
    ) {

        // --------------------- CABECERA ---------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(colors.surface, RoundedCornerShape(12.dp))
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
                        .border(1.dp, colors.outline, CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(1.dp, colors.outline, CircleShape)
                        .clip(CircleShape)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        color = colors.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onSurface)
            Text(text = email, fontSize = 14.sp, color = colors.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    expanded = false
                    navController.navigate("MiCuenta")
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface)
            ) {
                Text("Administrar tu Cuenta de MagFind")
            }
        }

        Divider(modifier = Modifier.padding(vertical = 4.dp), color = colors.outlineVariant)

        // ------------------- AGREGAR OTRA CUENTA -------------------
        DropdownMenuItem(
            text = { Text("Agregar otra cuenta", fontWeight = FontWeight.Medium, color = colors.onSurface) },
            onClick = {
                expanded = false
                navController.navigate("Login")
            },
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = colors.onSurface) }
        )

        // ------------------- CERRAR SESIÓN -------------------
        DropdownMenuItem(
            text = { Text("Cerrar sesión", fontWeight = FontWeight.Medium, color = colors.onSurface) },
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
            leadingIcon = { Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = colors.onSurface) }
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp), color = colors.outlineVariant)

        // ------------------- LINKS DE PRIVACIDAD Y TÉRMINOS -------------------
        val uriHandler = LocalUriHandler.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Política de Privacidad",
                fontSize = 11.sp,
                color = colors.primary,
                modifier = Modifier
                    .clickable {
                        uriHandler.openUri("https://magfind.xyz/politica")
                    }
            )

            Text(text = "  •  ", fontSize = 11.sp, color = colors.onSurfaceVariant)

            Text(
                text = "Términos",
                fontSize = 11.sp,
                color = colors.primary,
                modifier = Modifier
                    .clickable {
                        uriHandler.openUri("https://magfind.xyz/terminos")
                    }
            )
        }
    }
}
