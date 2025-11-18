package com.example.magfind1.apis

import android.util.Log
import com.example.magfind1.RetrofitClient
import com.example.magfind1.models.DCGmailConnectRequest

class GmailRepository {

    private val api = RetrofitClient.instance

    suspend fun getStatus(token: String): Map<String, Any> {
        Log.d("GMAIL", "Consultando estado de Gmail…")
        return api.getGmailStatus(token)
    }

    suspend fun connect(code: String, jwt: String): Map<String, Any> {
        Log.d("GMAIL", "Mandando code al backend...")
        return api.connectGmail(DCGmailConnectRequest(code, jwt))
    }

    suspend fun sync(jwt: String): Map<String, Any> {
        Log.d("GMAIL", "Sincronizando Gmail…")
        return api.syncGmail(jwt)
    }
}
