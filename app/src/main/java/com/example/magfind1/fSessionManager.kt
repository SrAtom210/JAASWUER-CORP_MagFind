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

        var token: String? = null
        var userId: Int? = null
        var username: String? = null
        var email: String? = null

        fun loadSession(context: Context) {
            val prefs = context.getSharedPreferences("magfind_prefs", Context.MODE_PRIVATE)
            token = prefs.getString(KEY_TOKEN, null)
            userId = prefs.getInt(KEY_USER_ID, -1).takeIf { it != -1 }
            username = prefs.getString(KEY_USERNAME, null)
        }
    }

    /**
     * Guarda sesión completa.
     */
    fun saveSession(
        userId: Int,
        token: String,
        username: String? = null,
        email: String? = null
    ) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString("EMAIL", email)
            .apply()

        Companion.token = token
        Companion.userId = userId
        Companion.username = username
        Companion.email = email
    }


    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun clearSession() {
        prefs.edit().clear().apply()
        token = null
        userId = null
        username = null
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun saveAutoSyncEnabled(value: Boolean) {
        prefs.edit().putBoolean("auto_sync", value).apply()
    }

    fun getAutoSyncEnabled(): Boolean {
        return prefs.getBoolean("auto_sync", true)
    }
    fun saveProfilePhoto(url: String?) {
        prefs.edit().putString("PHOTO_URL", url).apply()
    }

    fun getProfilePhoto(): String? {
        return prefs.getString("PHOTO_URL", null)
    }

    fun saveDisplayName(name: String?) {
        prefs.edit().putString("DISPLAY_NAME", name).apply()
    }

    fun getDisplayName(): String? {
        return prefs.getString("DISPLAY_NAME", null)
    }

    fun saveEmail(email: String?) {
        prefs.edit().putString("EMAIL", email).apply()
    }

    fun getEmail(): String? {
        return prefs.getString("EMAIL", null)
    }

}
