package com.example.magfind1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind1.models.Correo
import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.cCorreo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FCorreosViewModel : ViewModel() {

    // Estado para la lista de correos agrupados por categoría
    private val _correosMap = MutableStateFlow<Map<String, List<cCorreo>>>(emptyMap())
    val correosMap: StateFlow<Map<String, List<cCorreo>>> = _correosMap

    // Estado para manejar carga y errores
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun cargarCorreos(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val api = RetrofitClient.instance
                val respuesta = api.obtenerCorreos(token)

                // Asignamos el mapa directamente
                _correosMap.value = respuesta

            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar correos: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}