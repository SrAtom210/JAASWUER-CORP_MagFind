package com.example.magfind.apis

import android.util.Log
import com.example.magfind.RetrofitClient
import com.example.magfind.models.*
import com.example.magfind.models.LoginRequest

class AuthRepository {

    private val api = RetrofitClient.instance

    // --- REGISTRO (CORREGIDO) ---
    suspend fun register(nombre: String, email: String, password: String): Boolean {
        return try {
            val request = RegisterRequest(username = nombre, email = email, password = password)
            val response = api.register(request)
            // Si la respuesta es "ok", el backend ya envió el correo
            response.status == "ok"
        } catch (e: Exception) {
            Log.e("REGISTER_EXCEPTION", "Error: ${e.message}")
            false
        }
    }

    // --- LOGIN ---
    suspend fun login(username: String, password: String): LoginResponse? {
        return try {
            val api = RetrofitClient.instance
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
            val api = RetrofitClient.instance
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
}