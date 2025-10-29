package com.example.magfind.models

data class CuentaResponse(
    val status: String,
    val data: CuentaData
)

data class CuentaData(
    val id_usuario: Int,
    val nombre: String,
    val email: String,
    val fecha_registro: String,
    val activo: String,
    val tipo_suscripcion: String?,
    val fecha_inicio: String?,
    val fecha_fin: String?
)
