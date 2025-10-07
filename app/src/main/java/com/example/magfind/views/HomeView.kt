package com.example.magfind.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.magfind.models.Categoria
import com.example.magfind.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeView() {
    var categoria by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top=60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Categorías",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
        Text(
            "Crea una categoría",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Text(
            "Introduce el nombre de la categoría que deseas agregar.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it; error = false },
            placeholder = { Text("Ej. Trabajo") },
            isError = error,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.DarkGray,
                unfocusedTextColor = Color.DarkGray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        if (error) {
            Text(
                "El campo no puede estar vacío",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                if (categoria.isBlank()) {
                    error = true
                } else {
                    guardarCategoria(context, categoria)
                    categoria = "" // Limpiar campo
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Agregar", color = Color.White)
        }
    }
}

fun guardarCategoria(context: Context, nombre: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Esta llamada ahora usa el cliente Retrofit unificado y correcto
            val response = RetrofitClient.instance.addCategoria(Categoria(nombre = nombre))
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val categoriaCreada = response.body()
                    Toast.makeText(
                        context,
                        "Categoría guardada: ${categoriaCreada?.nombre ?: nombre}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(context, "Error al guardar: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

