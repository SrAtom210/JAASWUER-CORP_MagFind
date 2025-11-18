package com.example.magfind1.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magfind1.apis.GmailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GmailViewModel : ViewModel() {

    private val repo = GmailRepository()

    val connected = MutableStateFlow(false)
    val connectedEmail = MutableStateFlow("")
    val loading = MutableStateFlow(false)

    fun connectGoogle(code: String, jwt: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                loading.value = true

                val res = repo.connect(code, jwt)

                if (res["status"] == "ok") {
                    connected.value = true
                    connectedEmail.value = res["email"] as String
                    onDone(true)
                } else {
                    onDone(false)
                }

            } catch (e: Exception) {
                Log.e("GMAIL", "Error: ${e.message}")
                onDone(false)
            } finally {
                loading.value = false
            }
        }
    }

    fun checkStatus(jwt: String) {
        viewModelScope.launch {
            try {
                val res = repo.getStatus(jwt)
                connected.value = res["connected"] == true
                connectedEmail.value = res["email"]?.toString() ?: ""
            } catch (_: Exception) {}
        }
    }

    fun sync(jwt: String) {
        viewModelScope.launch {
            try {
                repo.sync(jwt)
            } catch (_: Exception) {}
        }
    }
}
