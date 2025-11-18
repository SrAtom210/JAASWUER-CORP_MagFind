package com.example.magfind1.apis

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class ClasificadorIA {

    private val client = OkHttpClient()

    private val API_URL = "http://158.101.114.30:8000/classify"


    /**
     * Clasifica un correo usando la API
     */
    fun clasificarCorreo(
        token: String,
        textoCorreo: String,
        categorias: List<String>,
        callback: (keyword: String, category: String, confidence: Double) -> Unit
    ) {
        // Payload requerido por tu API
        val payload = JSONObject()
        payload.put("token", token)
        payload.put("text", textoCorreo)
        payload.put("categories", categorias)

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            payload.toString()
        )

        val request = Request.Builder()
            .url(API_URL)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback("indefinido", "indefinido", 0.0)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    try {
                        val json = JSONObject(responseBody)
                        val categoria = json.optString("category", "indefinido")
                        val confianza = json.optDouble("confidence", 0.0)

                        // Extraer keyword simple (palabra más frecuente del correo)
                        val keyword = textoCorreo
                            .toLowerCase(Locale.ROOT)
                            .split("\\W+".toRegex())
                            .groupingBy { it }
                            .eachCount()
                            .maxByOrNull { it.value }?.key ?: "indefinido"

                        callback(keyword, categoria, confianza)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback("indefinido", "indefinido", 0.0)
                    }
                }
            }
        })
    }
}

