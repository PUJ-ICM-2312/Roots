package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.R

@Composable
fun EditProfileScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo arriba
            Spacer(modifier = Modifier.height(8.dp))
            // Reemplaza painterResource si tienes un logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(60.dp)
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Imagen de perfil circular
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* cambiar foto */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5)),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(),
            ) {
                Text("Cambiar foto", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            var nombres by remember { mutableStateOf("Diego") }
            var apellidos by remember { mutableStateOf("Cortés Acevedo") }
            var correo by remember { mutableStateOf("diegocortes@gmail.com") }
            var celular by remember { mutableStateOf("301-234-5678") }

            ProfileTextField("Nombres", nombres) { nombres = it }
            ProfileTextField("Apellidos", apellidos) { apellidos = it }
            ProfileTextField("Correo", correo) { correo = it }
            ProfileTextField("Celular", celular) { celular = it }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* actualizar perfil */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5))
            ) {
                Text("Actualizar perfil", fontSize = 16.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}
