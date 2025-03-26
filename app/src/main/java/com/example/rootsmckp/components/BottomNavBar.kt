package com.example.rootsmckp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BottomNavBar(navController: NavHostController) {
    Column {
        Divider(color = Color.LightGray, thickness = 1.dp)
        BottomAppBar(containerColor = Color.White) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("filters") }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
                }
                IconButton(onClick = { navController.navigate("map") }) {
                    Icon(Icons.Default.Lock, contentDescription = "Tus inmuebles")
                }
                IconButton(onClick = { navController.navigate("filters") }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar inmuebles")
                }
                IconButton(onClick = { /* Mensajes no implementado */ }) {
                    Icon(Icons.Default.Email, contentDescription = "Mensajes")
                }
                IconButton(onClick = { /* Configuración no implementado */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Configuración")
                }
            }
        }
    }
}
