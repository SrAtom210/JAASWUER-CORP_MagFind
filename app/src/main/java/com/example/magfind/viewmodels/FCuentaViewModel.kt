package com.example.magfind.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind.apis.CuentaRepository
import com.example.magfind.models.CuentaData
import kotlinx.coroutines.launch

class CuentaViewModel : ViewModel() {

    private val repo = CuentaRepository()

    // Estados observables
    private val _cuenta = mutableStateOf<CuentaData?>(null)
    val cuenta: State<CuentaData?> = _cuenta

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // ------------------------------------------------------------
    // FUNCIÓN: Cargar la información de la cuenta del usuario
    // ------------------------------------------------------------
    fun cargarCuenta(token: String?) {
        if (token.isNullOrBlank()) {
            _error.value = "Token no válido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repo.getCuenta(token)
                if (result != null) {
                    _cuenta.value = result
                } else {
                    _error.value = "No se encontró información de la cuenta."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _loading.value = false
            }
        }
    }


}
