package com.example.magfind.models

// ¡Asegúrate de que TODOS estos imports estén aquí!
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// -----------------------------------------------------------------
// ARCHIVO: models/FApiService.kt
// (El contrato que le dice a Retrofit qué hacer)
// -----------------------------------------------------------------

interface ApiService {

    // --- AUTENTICACIÓN ---
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): GenericResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("request-verification")
    suspend fun requestVerification(@Body request: EmailRequest): GenericResponse

    @POST("verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): LoginResponse

    @POST("request-password-reset")
    suspend fun requestPasswordReset(@Body request: EmailRequest): GenericResponse

    @POST("submit-password-reset")
    suspend fun submitPasswordReset(@Body request: PasswordResetRequest): GenericResponse

    @GET("verificar_email")
    suspend fun verificarEmail(@Query("email") email: String): GenericResponse

    // --- CUENTA Y DATOS ---

    @GET("cuenta/{token}")
    suspend fun obtenerCuenta(@Path("token") token: String): CuentaResponse

    // ¡¡ESTA ES LA LÍNEA CLAVE!!
    // Debe devolver CategoriaListResponse
    @GET("categoria/listar/{token}")
    suspend fun getCategorias(@Path("token") token: String): CategoriaListResponse

    @GET("correos/{token}")
    suspend fun obtenerCorreos(@Path("token") token: String): CategoriasResponse

    // --- CRUD CATEGORÍAS ---

    @POST("categoria/agregar/{token}")
    suspend fun agregarCategoria(@Path("token") token: String, @Body body: CategoriaPersonalizadaIn): GenericResponse

    @PUT("categoria/editar/{token}/{id_categoria}")
    suspend fun editarCategoria(@Path("token") token: String, @Path("id_categoria") id: Int, @Body body: CategoriaPersonalizadaIn): GenericResponse

    @DELETE("categoria/eliminar/{token}/{id_categoria}")
    suspend fun eliminarCategoria(@Path("token") token: String, @Path("id_categoria") id: Int): GenericResponse
}