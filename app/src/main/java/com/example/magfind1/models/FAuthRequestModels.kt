package com.example.magfind1.models

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
    val password: String,

)

data class LoginResponse(
    val token: String,
    val id_usuario: Int,
    val username: String
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
// Login con Google
data class GoogleLoginRequest(
    val token: String
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
    val regla: String,
    val esFavorita: Boolean = false,
    val colorHex: String? = "#1976D2"

)

// Para agregar/editar categoría
data class CategoriaPersonalizadaIn(
    val nombre: String,
    val regla: String?,
    val colorHex: String
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

data class CuentaResponse(
    val status: String,
    val data: CuentaData
)

// DTO para /cuenta
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

// Gmail connect
data class DCGmailConnectRequest(
    val code: String,
    val token: String
)
