package com.example.magfind.apis

import android.util.Log
import com.example.magfind.RetrofitClient
import com.example.magfind.models.CuentaData
import com.example.magfind.models.CuentaResponse
import retrofit2.http.GET

class CuentaRepository {

    private val api = RetrofitClient.retrofit.create(FCuentaApi::class.java)

    suspend fun getCuenta(token: String): CuentaData? {
        return try {
            val response = api.obtenerCuenta(token)
            Log.d("CuentaRepository", "Respuesta del servidor: $response")

            if (response.status.lowercase() == "ok") {
                response.usuario
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CuentaRepository", "Error al obtener cuenta: ${e.message}")
            null
        }
    }
}
