package com.example.magfind1

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("magfind_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "id_usuario"

        var token: String? = null
        var userId: Int? = null

        fun loadSession(context: Context) {
            val prefs = context.getSharedPreferences("magfind_prefs", Context.MODE_PRIVATE)
            token = prefs.getString(KEY_TOKEN, null)
            userId = prefs.getInt(KEY_USER_ID, -1).takeIf { it != -1 }
        }
    }

    // Guardar sesión permanentemente
    fun saveSession(userId: Int, token: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .apply()

        Companion.token = token
        Companion.userId = userId
    }

    // Obtener token
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    // Obtener ID de usuario
    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }

    // ✔ Cerrar sesión
    fun clearSession() {
        prefs.edit().clear().apply()
        token = null
        userId = null
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun saveAutoSyncEnabled(value: Boolean) {
        prefs.edit().putBoolean("auto_sync", value).apply()
    }

    fun getAutoSyncEnabled(): Boolean {
        return prefs.getBoolean("auto_sync", true)
    }

}
