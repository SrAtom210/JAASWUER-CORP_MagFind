package com.example.magfind1.models

import com.google.gson.annotations.SerializedName

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
    val id_usuario: Int,
    val token: String,
    val username: String?,
    val email: String?,
    val plan: String?
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
    @SerializedName("status") val status: String,
    @SerializedName("categorias") val categorias: List<CategoriaDto>
)

// El objeto Categoría (Lo que recibes de la DB)
data class CategoriaDto(
    @SerializedName("id_categoria") val id_categoria: Int,
    @SerializedName("id_usuario") val id_usuario: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("regla") val regla: String? = "",
    val prioridad: Int,
    // TRADUCCIÓN CLAVE: API envía "color_hex", Kotlin usa "colorHex"
    @SerializedName("color_hex") val colorHex: String? = "#1976D2",

    val esFavorita: Boolean = false
)

// El objeto para ENVIAR datos (Lo que mandas al Crear/Editar)
data class CategoriaPersonalizadaIn(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("regla") val regla: String?,
    @SerializedName("color_hex") val colorHex: String,
    @SerializedName("prioridad") val prioridad: Int? = 1
)

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
    val foto: String?,               //  ← AGREGA ESTO
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

data class EditarPerfilRequest(
    val nombre: String,
    val foto: String   // Base64
)

data class Correo(
    @SerializedName("id") val id: Int,
    @SerializedName("remitente") val remitente: String,
    @SerializedName("asunto") val asunto: String,
    @SerializedName("descripcion") val descripcion: String, // Tu API manda 'descripcion', no 'cuerpo'
    @SerializedName("fecha") val fecha: String
)

data class CorreoDetalleResponse(
    val id_email: Int,
    val remitente: String,
    val asunto: String,
    val cuerpo_completo: String,
    val fecha: String
)
// Respuesta para el Plan y Cuotas
data class PlanUsuarioResponse(
    val status: String,
    val plan: String,
    val limite_total: Int,
    val usados_hoy: Int,
    val restantes: Int
)

// Respuesta para la lista de Categorías
data class CategoriasListResponse(
    val status: String,
    val categorias: List<CategoriaItem>
)

// Item individual de categoría
data class CategoriaItem(
    val id_categoria: Int,
    val nombre: String,
    val regla: String,
    val color_hex: String,
    val prioridad: Int // 1 = Normal, 2 = Favorito
)

data class CorreosListResponse(
    val status: String,
    val correos: List<CorreoItemDto>
)

data class CorreoItemDto(
    val id: Int,
    val remitente: String,
    val asunto: String,
    val descripcion: String,
    val fecha: String
)