package com.example.magfind1

import com.example.magfind1.models.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

object RetrofitClient {

    private const val BASE_URL = "http://158.101.114.30:8000/"

    // Cliente principal para toda la API del backend (FastAPI)
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
    // Retrofit crudo por si necesitas clientes dinámicos
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Cliente dinámico opcional (lo dejo por si lo usas después)
    fun rawClient(): DynamicApi {
        return retrofit.create(DynamicApi::class.java)
    }
}
interface DynamicApi {
    @POST
    suspend fun post(@Url url: String, @Body body: HashMap<String, Any>): Response<Unit>
}
