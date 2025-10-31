package com.example.magfind.models

import retrofit2.http.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

// ===== DTOs =====
data class CategoriaDto(
    val id_categoria: Int,
    val nombre: String
)

// Respuesta real del backend
data class CategoriaListResponse(
    val categorias: List<String>
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

// ===== API =====
interface ApiService {
    // Categorías: GET /categoria/listar/{token}
    @GET("categoria/listar/{token}")
    suspend fun getCategorias(@Path("token") token: String): CategoriaListResponse

    // Login (por si lo usas aquí)
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    //-------------------------------------------------------------
    //EMANUEL CHECA SI ESTO ESTA BIEN LA GRAMATICA
    // --- NUEVAS RUTAS PARA REGLAS ---
    @GET("/reglas/{token}")
    suspend fun listarReglas(
        @Path("token") token: String
    ): Response<List<Regla>>

    @POST("/regla/crear")
    suspend fun crearRegla(
        @Body regla: Regla
    ): Response<Regla>

    @PUT("/regla/editar/{id}")
    suspend fun editarRegla(
        @Path("id") id: Int,
        @Body regla: Regla
    ): Response<Regla>

    @DELETE("/regla/eliminar/{id}")
    suspend fun eliminarRegla(
        @Path("id") id: Int
    ): Response<Unit>
}
