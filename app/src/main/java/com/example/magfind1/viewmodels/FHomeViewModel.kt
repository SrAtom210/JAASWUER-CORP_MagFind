package com.example.magfind1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind1.SessionManager
import com.example.magfind1.apis.FHomeRepository
import com.example.magfind1.models.CategoriaDto
import com.example.magfind1.models.recentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la UI
data class HomeUiState(
    val isLoading: Boolean = true,
    val unorganizedCount: Int = 0,
    val aiUsed: Int = 0,
    val aiLimit: Int = 50,
    val favorites: List<CategoriaDto> = emptyList(),

    // 👇 CAMBIA EL NOMBRE AQUÍ A: recentActivityList 👇
    val recentActivityList: List<recentActivity> = emptyList(),

    val error: String? = null
)

class FHomeViewModel(
    private val sessionManager: SessionManager,
    private val repository: FHomeRepository = FHomeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = "No hay sesión")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.fetchDashboardData(token)

            result.onSuccess { data ->
                _uiState.value = HomeUiState(
                    isLoading = false,
                    unorganizedCount = data.unorganizedCount,
                    aiUsed = data.aiUsed,
                    aiLimit = data.aiLimit,
                    favorites = data.favoriteCategories,

                    // 👇 ASIGNACIÓN COINCIDENTE 👇
                    recentActivityList = data.recentActivityList
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }
}