package com.example.magfind.apis


import com.example.magfind.models.CuentaResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FCuentaApi {
    /**
     * Obtiene la información de la cuenta del usuario desde /cuenta/{token}
     */
    @GET("cuenta/{token}")
    suspend fun obtenerCuenta(@Path("token") token: String): CuentaResponse
}
