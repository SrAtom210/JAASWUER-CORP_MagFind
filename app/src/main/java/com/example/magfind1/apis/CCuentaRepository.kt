package com.example.magfind1.apis

import android.util.Log
import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.CuentaData

class CuentaRepository {

    private val api = RetrofitClient.instance
    suspend fun getCuenta(token: String): CuentaData? {
        return try {
            val response = api.obtenerCuenta(token)
            if (response.status == "ok") {
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CuentaRepository", "Error al obtener cuenta: ${e.message}")
            null
        }
    }
}
