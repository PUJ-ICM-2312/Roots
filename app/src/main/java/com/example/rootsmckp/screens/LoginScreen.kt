package com.example.rootsmckp.screens

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.example.rootsmckp.components.AuthForm
import com.example.rootsmckp.components.AuthField
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults

@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthForm(
        fields = listOf(
            AuthField("Usuario", username) { username = it },
            AuthField("Contraseña", password, true) { password = it }
        ),
        buttonText = "Continuar",
        onButtonClick = { /* TODO: handle login */ },
        buttonColors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98F5A9))
    )
}
