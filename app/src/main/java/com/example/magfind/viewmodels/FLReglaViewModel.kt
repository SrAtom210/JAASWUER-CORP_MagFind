package com.example.magfind.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind.models.Regla
import com.example.magfind.repository.ReglaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReglaViewModel : ViewModel() {

    private val repository = ReglaRepository()

    private val _reglas = MutableStateFlow<List<Regla>>(emptyList())
    val reglas: StateFlow<List<Regla>> = _reglas

    fun cargarReglas(token: String) {
        viewModelScope.launch {
            val response = repository.getReglas(token)
            if (response.isSuccessful) {
                _reglas.value = response.body() ?: emptyList()
            }
        }
    }

    fun agregarRegla(regla: Regla) {
        viewModelScope.launch {
            val response = repository.addRegla(regla)
            if (response.isSuccessful) {
                val nueva = response.body()
                if (nueva != null) {
                    _reglas.value = _reglas.value + nueva
                }
            }
        }
    }

    fun editarRegla(id: Int, regla: Regla) {
        viewModelScope.launch {
            val response = repository.updateRegla(id, regla)
            if (response.isSuccessful) {
                val actualizada = response.body()
                if (actualizada != null) {
                    _reglas.value = _reglas.value.map {
                        if (it.id == id) actualizada else it
                    }
                }
            }
        }
    }

    fun eliminarRegla(id: Int) {
        viewModelScope.launch {
            val response = repository.deleteRegla(id)
            if (response.isSuccessful) {
                _reglas.value = _reglas.value.filter { it.id != id }
            }
        }
    }
}
