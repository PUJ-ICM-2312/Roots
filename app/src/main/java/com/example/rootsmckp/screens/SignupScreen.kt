package com.example.rootsmckp.screens

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.example.rootsmckp.components.AuthForm
import com.example.rootsmckp.components.AuthField

@Composable
fun SignupScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AuthForm(
        fields = listOf(
            AuthField("Usuario", username) { username = it },
            AuthField("Correo Electronico", email) { email = it },
            AuthField("Contraseña", password, true) { password = it },
            AuthField("Confirmar Contraseña", confirmPassword, true) { confirmPassword = it }
        ),
        buttonText = "Confirmar",
        onButtonClick = { /* TODO: handle signup */ }
    )
}
