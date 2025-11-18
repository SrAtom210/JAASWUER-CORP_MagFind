package com.example.magfind1.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.R

@Composable
fun fRegistroView(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    // Fondo blanco
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        )
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Image(painter = painterResource(R.drawable.magfind), contentDescription = "logo")
                val gradient = Brush.linearGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFF00BCD4))
                )

                Text(
                    text = "MagFind",
                    fontSize = 40.sp,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        brush = gradient
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                )
                Text(
                    text = "Usuario",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                )
                // Campo de usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("nombre@gmail.com", color = Color.DarkGray) },
                    textStyle = LocalTextStyle.current.copy(color = Color.DarkGray),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.DarkGray,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.DarkGray,
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )
                Text(
                    text = "Contraseña",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                // Campo de contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.DarkGray) },
                    textStyle = LocalTextStyle.current.copy(color = Color.DarkGray),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.DarkGray,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color.DarkGray,
                        focusedIndicatorColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )

                // Botón de login
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        "Registrarse",
                        color = Color.White
                    )
                }
            }
        }
    }
}
