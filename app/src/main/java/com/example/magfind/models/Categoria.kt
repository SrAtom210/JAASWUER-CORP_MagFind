package com.example.magfind.models

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// --- Modelos de Datos ---

data class Categoria(
    val id: Int? = null,
    val nombre: String
)

data class CategoriaResponse(
    val items: List<Categoria>
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

// --- Interfaz de API unificada ---
interface ApiService {


    // Categorías
    @POST("categorias/")
    suspend fun addCategoria(@Body categoria: Categoria): Response<Categoria>

    @GET("categorias/")
    suspend fun getCategorias(): Response<CategoriaResponse>

    // Login
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
