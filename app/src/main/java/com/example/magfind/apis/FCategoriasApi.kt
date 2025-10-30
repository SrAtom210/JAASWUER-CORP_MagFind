package com.example.magfind.apis

import retrofit2.http.GET
import retrofit2.http.Query

data class CategoriaListResponse(
    val categorias: List<String>
)

interface CategoriasApi {
    @GET("categoria/listar/{token}")
    suspend fun getCategorias(@retrofit2.http.Path("token") token: String): CategoriaListResponse

}
