package com.example.magfind.repository

import com.example.magfind.RetrofitClient
import com.example.magfind.models.Regla
import retrofit2.Response

class ReglaRepository {

    private val api = RetrofitClient.instance

    suspend fun getReglas(token: String): Response<List<Regla>> {
        return api.listarReglas(token)
    }

    suspend fun addRegla(regla: Regla): Response<Regla> {
        return api.crearRegla(regla)
    }

    suspend fun updateRegla(id: Int, regla: Regla): Response<Regla> {
        return api.editarRegla(id, regla)
    }

    suspend fun deleteRegla(id: Int): Response<Unit> {
        return api.eliminarRegla(id)
    }
}
