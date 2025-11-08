package com.example.magfind.apis

import com.example.magfind.models.cCorreo
import retrofit2.http.GET
import retrofit2.http.Path

data class CorreosResponse(
    val status: String,
    val categorias: Map<String, List<cCorreo>>
)

// Interfaz del endpoint /correos/{token}
interface FCorreosApi {
    @GET("correos/{token}")
    suspend fun obtenerCorreos(@Path("token") token: String): CorreosResponse
}
