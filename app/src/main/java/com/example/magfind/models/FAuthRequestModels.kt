package com.example.magfind.models

// -----------------------------------------------------------------
// ARCHIVO: models/FAuthRequestModels.kt
// -----------------------------------------------------------------

// --- DTOs para Autenticación ---

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class GenericResponse(
    val status: String,
    val message: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String // El token JWT
)

data class EmailRequest(
    val email: String
)

data class VerifyCodeRequest(
    val email: String,
    val code: String
)

data class PasswordResetRequest(
    val email: String,
    val code: String,
    val new_password: String
)

// --- DTOs para Datos de la App ---

data class CategoriaListResponse(
    val status: String,
    val categorias: List<CategoriaDto>
)

data class CategoriaDto(
    val id_categoria: Int,
    val id_usuario: Int,
    val nombre: String,
    val regla: String
)

// ¡AQUÍ ESTÁ LA CLASE QUE FALTABA!
data class CategoriaPersonalizadaIn(
    val nombre: String,
    val regla: String?
)

// DTO para /correos
typealias CategoriasResponse = Map<String, List<cCorreo>>

data class cCorreo(
    val id: Int,
    val remitente: String,
    val asunto: String,
    val descripcion: String,
    val fecha: String
)

// DTO para /cuenta
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
