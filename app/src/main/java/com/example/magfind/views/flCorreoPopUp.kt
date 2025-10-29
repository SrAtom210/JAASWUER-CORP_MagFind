package com.example.magfind.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fCorreoPopup(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = Color(0xFF2D2D2D),
        dragHandle = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Google", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            fAccountItem("Christian Hernandez", "crishdz690@gmail.com", Color(0xFF1E88E5), "29")

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = ButtonDefaults.outlinedButtonBorder,
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Text("Manage your Google Account", color = Color.White)
            }

            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(Modifier.height(12.dp))
            fAccountItem("CHRISTIAN NATHANIEL HERNANDEZ", "alu.22130813@correo.itlalaguna.edu.mx", Color(0xFF4CAF50), "99+")
            fAccountItem("Christian Nathaniel Hernandez de la Cruz", "chrishernandezdelacruz@gmail.com", Color(0xFF8E24AA), "99+")

            Spacer(Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f))

            fBottomOption(Icons.Default.Add, "Add another account")
            fBottomOption(Icons.Default.Settings, "Manage accounts on this device")

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("Privacy Policy", color = Color.Gray, fontSize = 13.sp)
                Text("  •  ", color = Color.Gray)
                Text("Terms of Service", color = Color.Gray, fontSize = 13.sp)
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun fAccountItem(nombre: String, correo: String, color: Color, notificaciones: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(nombre.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(correo, color = Color.Gray, fontSize = 14.sp)
        }
        Text(notificaciones, color = Color.Gray, fontSize = 14.sp)
    }
}



@Composable
fun fBottomOption(icon: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(texto, color = Color.White, fontSize = 16.sp)
    }
}
