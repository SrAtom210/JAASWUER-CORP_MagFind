package com.example.magfind.models

data class cCorreo(
    val id: Int,
    val remitente: String,
    val asunto: String,
    val descripcion: String,
    val fecha: String
)

typealias CategoriasResponse = Map<String, List<cCorreo>>
