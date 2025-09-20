package com.example.magfind

import Categoria
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tracing.perfetto.handshake.protocol.Response
import com.example.magfind.ui.theme.MagFindTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Response.isSuccessful: Boolean
    get() {
        TODO()
    }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MagFindTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ){
                    Principal()
                }
            }
        }
    }
}
@Composable
fun Principal() {
    var categoria by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Título
        Text(
            text = "Categorías",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = 40.dp, bottom = 24.dp),
            color = Color.Black
        )

        // Subtítulo
        Text(
            text = "Crea una categoría",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color.Black
        )

        // Texto explicativo
        Text(
            text = "Introduce el nombre de la categoría que deseas agregar.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp),
            color = Color.DarkGray
        )

        // Input de texto
        OutlinedTextField(
            value = categoria,
            onValueChange = {
                categoria = it
                error = false
            },
            placeholder = { Text("Ej. Trabajo")
                          },
            isError = error,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.DarkGray,
                unfocusedTextColor = Color.DarkGray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
        )

        if (error) {
            Text(
                text = "El campo no puede estar vacío",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        val context = LocalContext.current

        Button(
            onClick = {
                if (categoria.isBlank()) {
                    error = true
                } else {
                    guardarCategoria(context, categoria)
                    println("Categoría agregada: $categoria")
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


