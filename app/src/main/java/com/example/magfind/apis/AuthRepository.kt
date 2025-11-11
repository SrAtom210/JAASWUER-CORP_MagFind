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

            if (response.isSuccessful) {
                response.body()?.token
            } else {
                null
            }
        } catch (e: Exception) {
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

            if (response.isSuccessful) {
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }



}
