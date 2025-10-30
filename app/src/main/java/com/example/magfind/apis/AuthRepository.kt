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
}
