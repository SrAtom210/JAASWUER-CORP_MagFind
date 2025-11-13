package com.example.magfind.apis

import retrofit2.http.GET

data class CategoriaListResponse(
    val categorias: List<String>
)