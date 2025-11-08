package com.example.magfind.apis

import android.util.Log
import com.example.magfind.RetrofitClient
import com.example.magfind.models.LoginRequest
import com.example.magfind.models.LoginResponse

class AuthRepository {

    private val api = RetrofitClient.instance

    // --- LOGIN ---
    suspend fun login(username: String, password: String): String? {
        return try {
            val request = LoginRequest(username, password)
            val response = api.login(request)

            // Si api.login devuelve Response<LoginResponse>
            if (response.isSuccessful) {
                response.body()?.token
            } else {
                Log.e("LOGIN", "Error HTTP ${response.code()}")
                null
            }
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
                "username" to username.trim(),
                "password" to password.trim(),
                "email" to username.trim()
            )
            val response = RetrofitClient.rawClient().post("/register", requestBody)

            Log.d("REGISTER_CODE", "Código HTTP: ${response.code()}")
            Log.d("REGISTER_ERRORBODY", "ErrorBody (si hay): ${response.errorBody()?.string()}")

            if (response.isSuccessful) {
                Log.d("REGISTER", "✅ Usuario registrado correctamente ($nombreFinal)")
                true
            } else {
                Log.e("REGISTER", "❌ Error al registrar usuario: ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("REGISTER_EXCEPTION", "💥 Excepción en register(): ${e.message}", e)
            false
        }
    }



}
