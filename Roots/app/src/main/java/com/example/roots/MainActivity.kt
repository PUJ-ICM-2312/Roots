package com.example.roots

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.roots.screens.*
import com.example.roots.ui.theme.RootsTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Inicializar Places API con tu API Key
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                "AIzaSyDKjhqaBtcvLF4zW_VsHkXZYi3y4lCWeh0"
            )
        }

        enableEdgeToEdge()
        setContent {
            RootsTheme {
                // Puedes cambiar esta línea para probar otras pantallas
                NavigationStack()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
