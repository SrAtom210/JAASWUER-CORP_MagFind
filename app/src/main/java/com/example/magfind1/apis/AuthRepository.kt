package com.example.magfind1.apis

import android.util.Log
import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.*

class AuthRepository {

    private val api = RetrofitClient.instance

    // --- REGISTRO ---
    suspend fun register(nombre: String, email: String, password: String): Boolean {
        return try {
            val request = RegisterRequest(username = nombre, email = email, password = password)
            val response = api.register(request)
            response.status == "ok"
        } catch (e: Exception) {
            Log.e("REGISTER_EXCEPTION", "Error: ${e.message}")
            false
        }
    }

    // --- LOGIN ---
    suspend fun login(username: String, password: String): LoginResponse? {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful)
                response.body()
            else
                null
        } catch (e: Exception) {
            null
        }
    }


    // --- VERIFICACIÓN DE EMAIL ---
    suspend fun checkEmailDuplicate(email: String): Boolean {
        return try {
            val response = api.verificarEmail(email)
            response.status == "ok"
        } catch (e: Exception) {
            // Si da error (ej. 409), significa que ya existe
            false
        }
    }

    // --- CÓDIGOS Y PASSWORD ---
    suspend fun requestVerificationCode(email: String): Boolean {
        return try {
            val request = EmailRequest(email)
            val response = api.requestVerification(request)
            response.status == "ok"
        } catch (e: Exception) {
            false
        }
    }

    suspend fun verifyCode(email: String, code: String): LoginResponse? {
        return try {
            val response = api.verifyCode(VerifyCodeRequest(email, code))
            if (response.isSuccessful)
                response.body()
            else
                null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun requestPasswordReset(email: String): Boolean {
        return try {
            val request = EmailRequest(email)
            val response = api.requestPasswordReset(request)
            response.status == "ok"
        } catch (e: Exception) {
            false
        }
    }

    suspend fun submitPasswordReset(email: String, code: String, newPassword: String): Boolean {
        return try {
            val request = PasswordResetRequest(email, code, newPassword)
            val response = api.submitPasswordReset(request)
            response.status == "ok"
        } catch (e: Exception) {
            false
        }
    }

    // --- LOGIN GOOGLE ---
    suspend fun loginGoogle(idToken: String): LoginResponse? {
        return try {
            val body = GoogleLoginRequest(token = idToken)
            val response = api.loginGoogle(body)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registrarToken(token: String): Boolean {
        return try {
            val body = mapOf("token" to token)
            val response = api.registrarToken(body)

            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerCuenta(token: String): CuentaResponse {
        return api.obtenerCuenta(token)
    }

}




