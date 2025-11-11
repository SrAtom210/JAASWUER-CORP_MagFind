package com.example.magfind.apis

import CategoriaResponse
import android.util.Log
import com.example.magfind.RetrofitClient
import com.example.magfind.models.CategoriaDto
import retrofit2.http.*

data class NuevaCategoriaRequest(
    val nombre: String,
    val regla: String?
)

data class CategoriasResponse(
    val status: String,
    val categorias: List<CategoriaDto>
)

interface CategoriasApi {
    @GET("categoria/listar/{token}")
    suspend fun listar(@Path("token") token: String): CategoriasResponse

    @POST("categoria/agregar/{token}")
    suspend fun agregar(
        @Path("token") token: String,
        @Body body: NuevaCategoriaRequest
    ): CategoriaResponse

    @PUT("categoria/editar/{token}/{id_categoria}")
    suspend fun editar(
        @Path("token") token: String,
        @Path("id_categoria") id: Int,
        @Body body: NuevaCategoriaRequest
    ): CategoriaResponse

    @DELETE("categoria/eliminar/{token}/{id_categoria}")
    suspend fun eliminar(
        @Path("token") token: String,
        @Path("id_categoria") id: Int
    ): CategoriaResponse
}

class CategoriaRepository {
    private val api = RetrofitClient.retrofit.create(CategoriasApi::class.java)

    suspend fun listarCategorias(token: String): List<CategoriaDto> {
        return try {
            val response: CategoriasResponse = api.listar(token)
            response.categorias
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun agregarCategoria(token: String, nombre: String, regla: String?) {
        try {
            val body = NuevaCategoriaRequest(nombre, regla)
            api.agregar(token, body)
        } catch (e: Exception) {
        }
    }

    suspend fun editarCategoria(token: String, id: Int, nombre: String, regla: String?) {
        try {
            val body = NuevaCategoriaRequest(nombre, regla)
            api.editar(token, id, body)
        } catch (e: Exception) {
        }
    }

    suspend fun eliminarCategoria(token: String, id: Int) {
        try {
            api.eliminar(token, id)
        } catch (e: Exception) {
        }
    }
}
