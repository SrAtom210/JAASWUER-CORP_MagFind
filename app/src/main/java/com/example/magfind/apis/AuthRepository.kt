package com.example.magfind.apis

import android.util.Log
import com.example.magfind.RetrofitClient
import com.example.magfind.models.LoginRequest

class AuthRepository {

    private val api = RetrofitClient.instance

    suspend fun login(username: String, password: String): String? {
        return try {
            val request = LoginRequest(username, password)
            val response = api.login(request)

            Log.d("LOGIN_DEBUG", "Request: $request")
            Log.d("LOGIN_DEBUG", "Response code: ${response.code()}")
            Log.d("LOGIN_DEBUG", "Response body: ${response.body()}")
            Log.d("LOGIN_DEBUG", "Error body: ${response.errorBody()?.string()}")

            if (response.isSuccessful) {
                response.body()?.token
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LOGIN_DEBUG", "Error: ${e.message}")
            null
        }
    }
}
