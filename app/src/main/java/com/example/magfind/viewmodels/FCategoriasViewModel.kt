package com.example.magfind.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind.apis.CategoriaRepository
import com.example.magfind.models.CategoriaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriasViewModel : ViewModel() {
    private val repo = CategoriaRepository()

    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    /**
     * Carga las categorías desde la API y actualiza el flujo observable.
     */
    fun cargarCategorias(token: String) {
        viewModelScope.launch {
            try {
                val lista = repo.listarCategorias(token)
                Log.d("CategoriasVM", "Respuesta API: $lista")
                _categorias.value = lista
            } catch (e: Exception) {
                Log.e("CategoriasVM", "Error cargando categorías: ${e.message}")
            }
        }
    }

}

