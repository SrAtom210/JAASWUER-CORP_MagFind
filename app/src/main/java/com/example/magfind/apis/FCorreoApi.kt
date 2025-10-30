package com.example.magfind.apis

import com.example.magfind.models.CategoriasResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FCorreosApi {
    @GET("correos/{token}")
    suspend fun obtenerCorreos(@Path("token") token: String): CategoriasResponse
}
