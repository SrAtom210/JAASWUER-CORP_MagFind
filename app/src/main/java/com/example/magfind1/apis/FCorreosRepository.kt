package com.example.magfind1.apis

import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.CategoriasResponse

class CorreosRepository {

    private val api = RetrofitClient.instance

    suspend fun listarCorreos(token: String): CategoriasResponse {
        return api.obtenerCorreos(token)
    }
}
