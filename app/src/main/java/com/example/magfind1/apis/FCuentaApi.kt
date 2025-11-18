package com.example.magfind1.apis


import com.example.magfind1.models.CuentaResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FCuentaApi {
    /**
     * Obtiene la información de la cuenta del usuario desde /mi_cuenta/{token}
     */
    @GET("mi_cuenta/{token}")
    suspend fun obtenerCuenta(@Path("token") token: String): CuentaResponse

    @GET("verificar_email")
    suspend fun verificarEmail(@Query("email") email: String): CuentaResponse
}
