package com.example.magfind.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.magfind.R
@Composable
fun LoginView(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }



    // Fondo blanco
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center)
            {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                // Campo de usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario", color = Color.DarkGray) },
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        "Iniciar Sesión",
                        color = Color.White
                    )
                }
                Text("------o------")
                Button(onClick = {navController.navigate("Categorias")},
                    modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, Color.Black))
                    ,colors = ButtonDefaults.buttonColors(containerColor = Color.White))
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.facebook),
                            contentDescription = "Facebook",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar con Facebook", color = Color.Black)
                        /*navController.navigate("home")*/
                    }
                }
                Button(onClick = {navController.navigate("home")},
                    modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, Color.Black))
                    , colors = ButtonDefaults.buttonColors(containerColor = Color.White))
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.gmail),
                            contentDescription = "Gmail",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar con Gmail", color = Color.Black)
                        /*navController.navigate("Categoria")*/
                    }
                }

                // Enlace de registro
                TextButton(onClick = {/*onRegisterClick*/}, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrarse", color = Color.DarkGray)
                }
            }
        }
    }
}
