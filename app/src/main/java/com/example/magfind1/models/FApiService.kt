package com.example.magfind1.models

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// -----------------------------------------------------------------
// ARCHIVO: models/FApiService.kt
// -----------------------------------------------------------------

interface ApiService {

    // --- AUTENTICACIÓN ---

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): GenericResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("request-verification")
    suspend fun requestVerification(@Body request: EmailRequest): GenericResponse

    @POST("verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<LoginResponse>

    @POST("request-password-reset")
    suspend fun requestPasswordReset(@Body request: EmailRequest): GenericResponse

    @POST("submit-password-reset")
    suspend fun submitPasswordReset(@Body request: PasswordResetRequest): GenericResponse

    @GET("verificar_email")
    suspend fun verificarEmail(@Query("email") email: String): GenericResponse

    @POST("login_google")
    suspend fun loginGoogle(@Body body: GoogleLoginRequest): Response<LoginResponse>

    // --- CUENTA ---

    @GET("cuenta/{token}")
    suspend fun obtenerCuenta(@Path("token") token: String): CuentaResponse

    // --- CATEGORÍAS PERSONALIZADAS ---

    @GET("categoria/listar/{token}")
    suspend fun getCategorias(@Path("token") token: String): CategoriaListResponse

    @POST("categoria/agregar/{token}")
    suspend fun agregarCategoria(
        @Path("token") token: String,
        @Body body: CategoriaPersonalizadaIn // Enviamos el objeto JSON completo
    ): GenericResponse

    @PUT("categoria/editar/{token}/{id_categoria}")
    suspend fun editarCategoria(
        @Path("token") token: String,
        @Path("id_categoria") idCategoria: Int,
        @Body body: CategoriaPersonalizadaIn // Enviamos el objeto JSON completo
    ): GenericResponse

    @DELETE("categoria/eliminar/{token}/{id_categoria}")
    suspend fun eliminarCategoria(
        @Path("token") token: String,
        @Path("id_categoria") idCategoria: Int
    ): GenericResponse

    // --- CORREOS CLASIFICADOS ---
    @GET("correos/{token}")
    suspend fun obtenerCorreos(@Path("token") token: String): CategoriasResponse

    // --- GMAIL ---

    @GET("gmail/status/{token}")
    suspend fun getGmailStatus(@Path("token") token: String): Map<String, Any>

    @GET("gmail/sync/{token}")
    suspend fun syncGmail(@Path("token") token: String): Map<String, Any>

    @POST("gmail/connect")
    suspend fun connectGmail(@Body request: DCGmailConnectRequest): Map<String, Any>

    @POST("gmail/disconnect")
    suspend fun disconnectGmail(@Header("Authorization") token: String): Map<String, Any>

    @POST("/fcm/token")
    suspend fun registrarToken(@Body body: Map<String, String>): Response<Map<String, String>>

    @PUT("usuario/editar")
    suspend fun editarPerfil(
        @Body body: Map<String, String>
    ): GenericResponse

    @GET("correo/detalle/{id}/{token}")
    suspend fun obtenerDetalleCorreo(
        @Path("id") id: Int,
        @Path("token") token: String
    ): CorreoDetalleResponse

    //stripe para pagos
    @POST("/subscription/create-setup-intent")
    suspend fun createSetupIntent(@Body body: Map<String, String>): SetupIntentResponse

    @POST("/subscription/create-subscription")
    suspend fun createSubscription(@Body body: Map<String, String>): SubscriptionResponse
    @GET("usuario/plan/{token}")
    suspend fun obtenerPlanUsuario(@Path("token") token: String): PlanUsuarioResponse

    @POST("clasificar/auto/{token}")
    suspend fun triggerClasificacion(@Path("token") token: String): GenericResponse

    @PUT("categoria/toggle-favorito/{id}/{token}")
    suspend fun toggleFavorito(@Path("id") id: Int, @Path("token") token: String): GenericResponse

    // 4. Listar Categorías (Ordenadas por prioridad)
    @GET("categoria/listar/{token}")
    suspend fun listarCategorias(@Path("token") token: String): CategoriasListResponse

    @GET("correos/categoria/{id_cat}/{token}")
    suspend fun obtenerCorreosPorCategoria(
        @Path("id_cat") idCategoria: Int,
        @Path("token") token: String
    ): CorreosListResponse
}
