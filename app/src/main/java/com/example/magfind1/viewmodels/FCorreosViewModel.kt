package com.example.magfind1.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind1.apis.CorreosRepository
import com.example.magfind1.models.CategoriasResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CorreosViewModel : ViewModel() {

    private val repo = CorreosRepository()

    private val _correos = MutableStateFlow<CategoriasResponse>(emptyMap())
    val correos = _correos.asStateFlow()

    fun cargarCorreos(token: String) {
        Log.d("CorreosVM", "Solicitando correos…")

        viewModelScope.launch {
            try {
                val res = repo.listarCorreos(token)
                Log.d("CorreosVM", "Respuesta: $res")
                _correos.value = res
            } catch (e: Exception) {
                Log.e("CorreosVM", "Error: ${e.message}")
            }
        }
    }
}
