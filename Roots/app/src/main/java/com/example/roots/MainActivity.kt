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
import com.google.firebase.FirebaseApp

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

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            RootsTheme {/*
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
                // Para probar la pantalla que hagan, deben primero crearla en la carpeta screens
                // Luego poner el nombre acá abajo, así cuando se corra esta vaina, se abre esa pantalla
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
