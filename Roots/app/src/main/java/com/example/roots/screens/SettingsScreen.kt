package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.R

@Composable
fun SettingsScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            // Logo arriba izquierda
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(60.dp)
                        .padding(start = 0.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Foto de perfil con botón de edición
            Box(
                modifier = Modifier
                    .size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                // Imagen de perfil (círculo gris)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                // Botón de edición flotante
                IconButton(
                    onClick = { /* acción editar */ },
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Black)
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Diego Cortés", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(24.dp))

            SettingsButton(Icons.Default.Settings, "Perfil") { /* Navegar a perfil */ }
            SettingsButton(Icons.Default.Description, "Términos y condiciones") { /* Navegar */ }
            SettingsButton(Icons.Default.GridOn, "Revisa tu plan") { /* Navegar */ }
            SettingsButton(Icons.Default.Star, "Obtén Roots Premium") { /* Navegar */ }
        }
    }
}

@Composable
fun SettingsButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = text, tint = Color.Black)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = Color.Black)
    }
}
