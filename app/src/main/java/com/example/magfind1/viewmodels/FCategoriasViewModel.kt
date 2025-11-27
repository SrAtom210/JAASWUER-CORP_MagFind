package com.example.magfind1.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind1.RetrofitClient
// --- IMPORTS DE MODELOS ---
import com.example.magfind1.models.CategoriaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriasViewModel : ViewModel() {
    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    private val api = RetrofitClient.instance

    private val _canOrganize = MutableStateFlow(false)
    val canOrganize: StateFlow<Boolean> = _canOrganize.asStateFlow()

    fun cargarCategorias(token: String?) {
        if (token.isNullOrBlank()) {
            Log.e("CategoriasVM", "Token es nulo o vacío, no se puede cargar.")
            _categorias.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                // Al tener el import arriba, Kotlin ya sabe que 'response'
                // es de tipo CategoriaListResponse
                val response = api.getCategorias(token)

                // Ahora .status y .categorias son reconocidos correctamente
                if (response.status == "ok") {
                    _categorias.value = response.categorias
                } else {
                    Log.e("CategoriasVM", "La API devolvió un error: ${response.status}")
                    _categorias.value = emptyList()
                }

            } catch (e: Exception) {
                Log.e("CategoriasVM", "Error al cargar categorías: ${e.message}")
                _categorias.value = emptyList()
            }
        }
    }

    fun checkQuota(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.obtenerPlanUsuario(token)
                // Puede organizar si tiene restantes mayor a 0
                _canOrganize.value = response.restantes > 0
            } catch (e: Exception) {
                // Si falla la conexión, por seguridad deshabilitamos el botón
                _canOrganize.value = false
                e.printStackTrace()
            }
        }
    }
}
