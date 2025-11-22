package com.example.magfind1

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("magfind_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "id_usuario"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_PLAN = "user_plan"
        private const val KEY_AUTOSYNC = "auto_sync"

        var token: String? = null
        var userId: Int? = null
        var username: String? = null
        var email: String? = null
        var plan: String? = null
        var autoSync: Boolean = false

        fun loadSession(context: Context) {
            val prefs = context.getSharedPreferences("magfind_prefs", Context.MODE_PRIVATE)

            token = prefs.getString(KEY_TOKEN, null)
            userId = prefs.getInt(KEY_USER_ID, -1).takeIf { it != -1 }
            username = prefs.getString(KEY_USERNAME, null)
            email = prefs.getString(KEY_EMAIL, null)
            plan = prefs.getString(KEY_PLAN, null)
            autoSync = prefs.getBoolean(KEY_AUTOSYNC, false)
        }

        fun isLoggedIn(context: Context): Boolean {
            val prefs = context.getSharedPreferences("magfind_prefs", Context.MODE_PRIVATE)
            val storedToken = prefs.getString(KEY_TOKEN, null)
            return !storedToken.isNullOrEmpty()
        }
    }

    // Guardar toda la sesión
    fun saveSession(
        userId: Int,
        token: String,
        username: String? = null,
        email: String? = null,
        plan: String? = null
    ) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            username?.let { putString(KEY_USERNAME, it) }
            email?.let { putString(KEY_EMAIL, it) }
            plan?.let { putString(KEY_PLAN, it) }
            apply()
        }

        Companion.token = token
        Companion.userId = userId
        Companion.username = username
        Companion.email = email
        Companion.plan = plan
    }

    // ---- NOMBRE / EMAIL ----
    fun saveDisplayName(name: String) {
        prefs.edit().putString(KEY_USERNAME, name).apply()
        Companion.username = name
    }

    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
        Companion.email = email
    }

    // ---- FOTO DE PERFIL ----
    fun saveProfilePhoto(url: String?) {
        prefs.edit().putString("profile_photo", url).apply()
    }

    fun getProfilePhoto(): String? {
        return prefs.getString("profile_photo", null)
    }


    fun getDisplayName(): String? {
        return Companion.username ?: prefs.getString(KEY_USERNAME, null)
    }

    fun getEmail(): String? {
        return Companion.email ?: prefs.getString(KEY_EMAIL, null)
    }

    // ---- PLAN ----
    fun savePlan(plan: String) {
        prefs.edit().putString(KEY_PLAN, plan).apply()
        Companion.plan = plan
    }

    fun getPlan(): String? {
        val storedPlan = prefs.getString(KEY_PLAN, null)
        return storedPlan?.replaceFirstChar { it.uppercase() }
    }

    // ---- TOKEN / ID ----
    fun getToken(): String? {
        return Companion.token ?: prefs.getString(KEY_TOKEN, null)
    }

    fun getUserId(): Int? {
        val id = Companion.userId ?: prefs.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }

    // ---- AUTOSYNC (RESTOS QUE FALTABAN) ----
    fun saveAutoSyncEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTOSYNC, enabled).apply()
        Companion.autoSync = enabled
    }

    fun getAutoSyncEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTOSYNC, false)
    }

    // ---- LOGIN ----
    fun isLoggedIn(): Boolean {
        val storedToken = prefs.getString(KEY_TOKEN, null)
        return !storedToken.isNullOrEmpty()
    }

    // ---- Cerrar sesión ----
    fun clearSession() {
        prefs.edit().clear().apply()

        Companion.token = null
        Companion.userId = null
        Companion.username = null
        Companion.email = null
        Companion.plan = null
        Companion.autoSync = false
    }
}
