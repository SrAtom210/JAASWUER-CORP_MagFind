package com.example.magfind.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore instancia única
val Context.dataStore by preferencesDataStore("user_preferences")

class ThemeRepository(private val context: Context) {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    // Leer el valor guardado
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    // Guardar el nuevo valor
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}
