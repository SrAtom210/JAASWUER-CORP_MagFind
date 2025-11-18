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

    suspend fun agregarCategoria(token: String, nombre: String, regla: String?) {
        try {
            val body = CategoriaPersonalizadaIn(nombre, regla)
            api.agregarCategoria(token, body)
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al agregar: ${e.message}")
        }
    }
    suspend fun editarCategoria(token: String, id: Int, nombre: String, regla: String?) {
        try {
            val body = CategoriaPersonalizadaIn(nombre, regla)
            api.editarCategoria(token, id, body)
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al editar: ${e.message}")
        }
    }

    suspend fun eliminarCategoria(token: String, id: Int) {
        try {
            api.eliminarCategoria(token, id)
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al eliminar: ${e.message}")
        }
    }
}