package com.example.magfind.models

data class Regla(
    val id: Int? = null,               // ID asignado por el backend
    val condicion: String,
    val accion: String,
    val usuario_id: Int                // o token, dependiendo cómo lo manejan
)
