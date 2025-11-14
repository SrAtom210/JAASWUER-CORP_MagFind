package com.example.magfind.apis

import com.example.magfind.models.LoginRequest
import com.example.magfind.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

}

