package com.example.magfind1.apis

import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.DashboardData
import com.example.magfind1.models.recentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class FHomeRepository {
    private val api = RetrofitClient.instance

    suspend fun fetchDashboardData(token: String): Result<DashboardData> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Peticiones
                val planDeferred = async { api.obtenerPlanUsuario(token) }
                val catsDeferred = async { api.getCategorias(token) }
                val emailsDeferred = async { api.obtenerCorreos(token) }

                val planResponse = planDeferred.await()
                val catsResponse = catsDeferred.await()
                val emailsMap = emailsDeferred.await()

                // 2. Procesar básicos
                val used = planResponse.usados_hoy
                val limit = planResponse.limite_total
                val favorites = catsResponse.categorias.filter { it.prioridad >= 2 }

                val unorganizedKey = emailsMap.keys.find { it.equals("Sin organizar", ignoreCase = true) }
                    ?: "Sin organizar"
                val unorganizedCount = emailsMap[unorganizedKey]?.size ?: 0

                // 3. FRANKENSTEIN (Creando los objetos recentActivity)
                val tempList = mutableListOf<recentActivity>()

                emailsMap.forEach { (catName, listaCorreos) ->
                    // Buscar color
                    val catColor = catsResponse.categorias.find {
                        it.nombre.equals(catName, ignoreCase = true)
                    }?.colorHex ?: "#757575"

                    // Mapear
                    listaCorreos.forEach { email ->
                        tempList.add(
                            recentActivity(
                                id = email.id,
                                remitente = email.remitente,
                                asunto = email.asunto,
                                categoria = catName,
                                colorHex = catColor,
                                fecha = email.fecha
                            )
                        )
                    }
                }

                // Ordenar y cortar
                val finalRecentList = tempList.sortedByDescending { it.id }.take(5)

                Result.success(
                    DashboardData(
                        unorganizedCount = unorganizedCount,
                        aiUsed = used,
                        aiLimit = if (limit > 0) limit else 1,
                        favoriteCategories = favorites,
                        recentActivityList = finalRecentList
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}