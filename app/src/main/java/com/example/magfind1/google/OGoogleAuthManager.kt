package com.example.magfind1.google

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.UUID

object GoogleAuthManager {

    private const val CLIENT_ID =
        "76794028126-h85vt3eva11286jjob5leq038mr61q6c.apps.googleusercontent.com"
    var lastEmail: String? = null    // <---- NUEVO

    suspend fun signIn(activity: Activity): String? {
        return try {

            val credentialManager = CredentialManager.create(activity)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setNonce(UUID.randomUUID().toString())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = activity,
                request = request
            )

            val googleCred = GoogleIdTokenCredential.createFrom(result.credential.data)

            Log.d("GOOGLE_DATA", """
             Email: ${googleCred.id}
            ID Token: ${googleCred.idToken}
            """.trimIndent())

            val email = googleCred.id

            lastEmail = email   // <---- GUARDA EL CORREO EN MEMORIA

            val idToken = googleCred.idToken
            Log.d("GOOGLE_CLIENT", "CLIENT_ID usado: $CLIENT_ID")


            idToken

        } catch (e: Exception) {
            Log.e("GOOGLE_DATA", "ERROR: ${e.message}")
            null
        }
    }
}
