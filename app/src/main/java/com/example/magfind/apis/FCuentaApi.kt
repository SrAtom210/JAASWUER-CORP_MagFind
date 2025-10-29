package com.example.magfind.apis


import com.example.magfind.models.CuentaResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FCuentaApi {
    /**
     * Obtiene la información de la cuenta del usuario desde /mi_cuenta/{token}
     */
    @GET("mi_cuenta/{token}")
    suspend fun obtenerCuenta(@Path("token") token: String): CuentaResponse
}
