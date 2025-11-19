package com.example.magfind.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
// --- ¡IMPORTS NECESARIOS! ---
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
// -----------------------------
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.magfind1.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fResetPasswordDialog(
    onDismissRequest: () -> Unit,
    onSendClick: (String) -> Unit,
    themeViewModel: ThemeViewModel,
    isLoading: Boolean = false
) {
    var email by remember { mutableStateOf("") }

    val isDark = themeViewModel.isDarkMode.collectAsState().value

    val cardColor = if (isDark) Color(0xFF121212) else Color(0xFFFDFDFD)
    val textColor = if (isDark) Color(0xFFEFEFEF) else Color(0xFF1A1A1A)
    val subtitleColor = textColor.copy(alpha = 0.7f)
    val accentColor = Color(0xFF64B5F6) // azul MagFind
    val buttonBlue = Color(0xFF64B5F6)

    Dialog(onDismissRequest = onDismissRequest) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // TÍTULO
                Text(
                    text = "Restablecer contraseña",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                // SUBTÍTULO
                Text(
                    text = "Ingresa tu correo y te enviaremos un código de recuperación.",
                    fontSize = 15.sp,
                    color = subtitleColor,
                    lineHeight = 20.sp
                )

                // INPUT
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = subtitleColor) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = textColor.copy(alpha = 0.3f),
                        cursorColor = accentColor,
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = subtitleColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // BOTONES
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Cancelar (texto simple y elegante)
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            "Cancelar",
                            color = accentColor,
                            fontSize = 15.sp
                        )
                    }

                    // Botón azul principal
                    Button(
                        onClick = { onSendClick(email) },
                        enabled = email.isNotBlank() && !isLoading,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonBlue),
                        modifier = Modifier.height(45.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Enviar",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
