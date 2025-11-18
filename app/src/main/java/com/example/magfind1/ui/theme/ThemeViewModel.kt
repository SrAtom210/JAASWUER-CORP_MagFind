package com.example.magfind1.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(private val repository: ThemeRepository) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.isDarkMode.collect { savedValue ->
                _isDarkMode.value = savedValue
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }
}
