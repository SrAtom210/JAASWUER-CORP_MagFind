package com.example.magfind1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.magfind1.apis.AuthRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch

class MagFindMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificaciones()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val titulo = remoteMessage.notification?.title ?: "MagFind"
        val mensaje = remoteMessage.notification?.body ?: "Nuevo mensaje recibido"

        mostrarNotificacion(this, titulo, mensaje)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCM_TOKEN", "Nuevo token: $token")

        // Ejecutar en corrutina
        kotlinx.coroutines.GlobalScope.launch {
            AuthRepository().registrarToken(token)
        }
    }


    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "magfind_channel",
                "MagFind Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }
}

fun mostrarNotificacion(context: Context, titulo: String, mensaje: String) {

    val permiso = android.Manifest.permission.POST_NOTIFICATIONS

    // Validar permiso en Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        context.checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED
    ) {
        // Si el permiso NO está otorgado, evita la notificación y no lanza warning.
        return
    }

    val builder = NotificationCompat.Builder(context, "magfind_channel")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(titulo)
        .setContentText(mensaje)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    NotificationManagerCompat.from(context).notify(
        System.currentTimeMillis().toInt(),
        builder.build()
    )
}

