package com.example.magfind1.apis

import android.util.Log
import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.CategoriaDto
import com.example.magfind1.models.CategoriaListResponse
import com.example.magfind1.models.CategoriaPersonalizadaIn

class CategoriaRepository {
    private val api = RetrofitClient.instance

    suspend fun listarCategorias(token: String): List<CategoriaDto> {
        return try {
            val response: CategoriaListResponse = api.getCategorias(token)
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

    // Ejemplo de cómo debe quedar en CategoriaRepository.kt
    suspend fun agregarCategoria(token: String, nombre: String, regla: String, colorHex: String) {
        // ... crear objeto CategoriaPersonalizadaIn con el color ...
        val body = CategoriaPersonalizadaIn(nombre, regla, colorHex)
        api.agregarCategoria(token, body)
    }

    suspend fun editarCategoria(token: String, id: Int, nombre: String, regla: String, colorHex: String) {
        val body = CategoriaPersonalizadaIn(nombre, regla, colorHex)
        api.editarCategoria(token,id,body)
    }

    suspend fun eliminarCategoria(token: String, id: Int) {
        try {
            api.eliminarCategoria(token, id)
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al eliminar: ${e.message}")
        }
    }


}