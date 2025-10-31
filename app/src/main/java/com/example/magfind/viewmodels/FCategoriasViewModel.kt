package com.example.magfind.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind.RetrofitClient
import com.example.magfind.models.CategoriaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriasViewModel : ViewModel() {

    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    private val api = RetrofitClient.instance

    fun cargarCategorias(token: String) {
        viewModelScope.launch {
            try {
                val response = api.getCategorias(token)
                // Transforma ["Escuela", "Finanzas", ...] en lista de objetos
                _categorias.value = response.categorias.mapIndexed { index, nombre ->
                    CategoriaDto(id_categoria = index + 1, nombre = nombre)
                }
            } catch (e: Exception) {
                e.printStackTrace() //
                _categorias.value = emptyList()
            }
        }
    }
}
