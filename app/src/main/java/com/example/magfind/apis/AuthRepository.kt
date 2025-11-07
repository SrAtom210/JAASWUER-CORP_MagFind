package com.example.magfind.apis

import android.util.Log
import com.example.magfind.RetrofitClient
import com.example.magfind.models.EmailRequest
import com.example.magfind.models.LoginRequest
import com.example.magfind.models.LoginResponse
import com.example.magfind.models.PasswordResetRequest
import com.example.magfind.models.VerifyCodeRequest

class AuthRepository {

    private val api = RetrofitClient.instance

    // --- LOGIN ---
    suspend fun login(username: String, password: String): String? {
        return try {
            val request = LoginRequest(username, password)
            val response: LoginResponse = api.login(request)
            response.token
        } catch (e: Exception) {
            Log.e("LOGIN_EXCEPTION", "Error: ${e.message}")
            null
        }
    }

    // --- REGISTRO ---
    suspend fun register(nombre: String, username: String, password: String): Boolean {
        return try {
            val nombreFinal = nombre.ifBlank { username.substringBefore('@') }

            val requestBody = hashMapOf<String, Any>(
                "nombre" to nombreFinal,
                "username" to username,
                "password" to password,
                "email" to username
            )

            val response = RetrofitClient.rawClient().post("/register", requestBody)

            if (response.isSuccessful) {
                Log.d("REGISTER", "Usuario registrado correctamente")
                true
            } else {
                Log.e("REGISTER", "Error: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("REGISTER_EXCEPTION", "Error: ${e.message}")
            false
        }
    }

    // --- NUEVA FUNCIÓN: Solicitar código de verificación ---
    /**
     * Llama al backend para que envíe un correo de verificación.
     */
    suspend fun requestVerificationCode(email: String): Boolean {
        return try {
            val request = EmailRequest(email)
            val response = api.requestVerification(request)
            response.status == "ok"
        } catch (e: Exception) {
            Log.e("REQUEST_VERIFY_EX", "Error: ${e.message}")
            false
        }
    }

    // --- NUEVA FUNCIÓN: Verificar código ---
    /**
     * Envía el código al backend para validación.
     * Si es exitoso, devuelve el token para iniciar sesión.
     */
    suspend fun verifyCode(email: String, code: String): String? {
        return try {
            val request = VerifyCodeRequest(email, code)
            val response = api.verifyCode(request)
            response.token // Devuelve el token si es exitoso
        } catch (e: Exception) {
            Log.e("VERIFY_CODE_EX", "Error: ${e.message}")
            null
        }
    }

    // --- NUEVA FUNCIÓN: Solicitar reseteo de contraseña ---
    /**
     * Llama al backend para que envíe un correo de reseteo de contraseña.
     */
    suspend fun requestPasswordReset(email: String): Boolean {
        return try {
            val request = EmailRequest(email)
            val response = api.requestPasswordReset(request)
            response.status == "ok"
        } catch (e: Exception) {
            Log.e("REQUEST_RESET_EX", "Error: ${e.message}")
            false
        }
    }

    // --- NUEVA FUNCIÓN: Enviar nueva contraseña ---
    /**
     * Envía el código de reseteo y la nueva contraseña al backend.
     */
    suspend fun submitPasswordReset(email: String, code: String, newPassword: String): Boolean {
        return try {
            val request = PasswordResetRequest(email, code, newPassword)
            val response = api.submitPasswordReset(request)
            response.status == "ok"
        } catch (e: Exception) {
            Log.e("SUBMIT_RESET_EX", "Error: ${e.message}")
            false
        }
    }
}
