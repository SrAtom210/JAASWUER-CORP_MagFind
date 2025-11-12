package com.example.magfind

import com.example.magfind.models.ApiService // Importa la nueva interfaz
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// -----------------------------------------------------------------
// ARCHIVO MODIFICADO: RetrofitClient.kt
// (Reemplaza tu archivo existente con este)
// -----------------------------------------------------------------

object RetrofitClient {

    // La URL base de tu API de FastAPI
    private const val BASE_URL = "http://158.101.114.30:8000/"

    // Cliente Retrofit (lazy-initialized)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // La única instancia de ApiService que usará toda la app
    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
