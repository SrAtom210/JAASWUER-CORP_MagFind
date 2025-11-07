package com.example.magfind.models

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST

// ===== DTOs =====
data class CategoriaDto(
    val id_categoria: Int,
    val nombre: String
)

// Respuesta real del backend
data class CategoriaListResponse(
    val categorias: List<String>
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

// ===== API =====
interface ApiService {
    // Categorías: GET /categoria/listar/{token}
    @GET("categoria/listar/{token}")
    suspend fun getCategorias(@Path("token") token: String): CategoriaListResponse

    // Login (por si lo usas aquí)
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // --- NUEVOS ENDPOINTS PARA EMAILS ---

    /**
     * Solicita al backend que genere y envíe un código de verificación
     * al email proporcionado. Esto se llama DESPUÉS de un registro exitoso.
     */
    @POST("request-verification")
    suspend fun requestVerification(@Body request: EmailRequest): GenericResponse

    /**
     * Envía el código que el usuario escribió para que el backend lo valide.
     * Si es exitoso, el backend debería marcar al usuario como 'activo'
     * y quizás devolver un token de sesión.
     */
    @POST("verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): LoginResponse // Devuelve LoginResponse para auto-loguear

    /**
     * Solicita un código de reseteo de contraseña para un email existente.
     */
    @POST("request-password-reset")
    suspend fun requestPasswordReset(@Body request: EmailRequest): GenericResponse

    /**
     * Envía el código de reseteo y la nueva contraseña.
     * El backend valida el código y, si es correcto, actualiza la contraseña.
     */
    @POST("submit-password-reset")
    suspend fun submitPasswordReset(@Body request: PasswordResetRequest): GenericResponse
}
