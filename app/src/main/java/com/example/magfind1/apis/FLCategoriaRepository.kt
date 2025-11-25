package com.example.magfind1.apis

import android.util.Log
import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.CategoriaDto
import com.example.magfind1.models.CategoriaPersonalizadaIn

class CategoriaRepository {
    // Instancia del API
    private val api = RetrofitClient.instance

    // LISTAR
    suspend fun listarCategorias(token: String): List<CategoriaDto> {
        return try {
            val response = api.getCategorias(token)
            if (response.status == "ok") {
                response.categorias
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al listar: ${e.message}")
            emptyList()
        }
    }

    // AGREGAR (Ahora recibe colorHex)
    suspend fun agregarCategoria(token: String, nombre: String, regla: String, colorHex: String) {
        try {
            // Empaquetamos los datos para enviarlos como JSON
            val body = CategoriaPersonalizadaIn(
                nombre = nombre,
                regla = regla,
                colorHex = colorHex,
                prioridad = 1 // Valor por defecto
            )
            api.agregarCategoria(token, body)
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al agregar: ${e.message}")
            throw e // Re-lanzamos para que la UI muestre el Toast de error si falla
        }
    }

    // EDITAR (Ahora recibe colorHex)
    suspend fun editarCategoria(token: String, id: Int, nombre: String, regla: String, colorHex: String) {
        try {
            val body = CategoriaPersonalizadaIn(
                nombre = nombre,
                regla = regla,
                colorHex = colorHex,
                prioridad = 1
            )
            api.editarCategoria(token, id, body)
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al editar: ${e.message}")
            throw e
        }
    }

    // ELIMINAR
    suspend fun eliminarCategoria(token: String, id: Int) {
        try {
            api.eliminarCategoria(token, id)
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al eliminar: ${e.message}")
            throw e
        }
    }
}