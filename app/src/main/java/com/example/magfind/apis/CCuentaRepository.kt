package com.example.magfind.apis

import android.util.Log
import com.example.magfind.RetrofitClient
import com.example.magfind.models.CuentaData

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
