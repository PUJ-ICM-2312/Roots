package com.example.roots

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.roots.screens.*
import com.example.roots.ui.theme.RootsTheme
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp

const val CHAT_NOTIFICATION_CHANNEL_ID = "chat_channel"

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Crear canal de notificaciones
        crearCanalDeNotificaciones(this)

        // ✅ Solicitar permiso POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // ✅ Inicializar Places API
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                "AIzaSyDKjhqaBtcvLF4zW_VsHkXZYi3y4lCWeh0"
            )
        }

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            RootsTheme {
                val navController = rememberNavController()

                // ✅ Detectar si fue abierto desde una notificación
                LaunchedEffect(Unit) {
                    val action = intent?.getStringExtra("notification_action")
                    if (action == "open_chat") {
                        val chatId = intent.getStringExtra("chat_id")
                        val receptorId = intent.getStringExtra("receptor_id")
                        if (!chatId.isNullOrEmpty() && !receptorId.isNullOrEmpty()) {
                            navController.navigate("chat/$chatId/$receptorId")
                        }
                    }
                }

                NavigationStack()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            println("🔔 Permiso POST_NOTIFICATIONS concedido o denegado.")
        }
    }
}

// ✅ Crear canal de notificación
fun crearCanalDeNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nombre = "Notificaciones de Chat"
        val descripcion = "Mensajes nuevos del chat"
        val importancia = NotificationManager.IMPORTANCE_DEFAULT
        val canal = NotificationChannel(CHAT_NOTIFICATION_CHANNEL_ID, nombre, importancia).apply {
            description = descripcion
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(canal)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
