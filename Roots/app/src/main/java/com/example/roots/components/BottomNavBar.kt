package com.example.roots.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Column {
        Divider(color = Color.LightGray, thickness = 1.dp)

        BottomAppBar(
            containerColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("favorites") }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favoritos",
                        tint = if (currentRoute == "favorites") Color(0xFF2E7D32) else Color.Black
                    )
                }

                IconButton(onClick = { navController.navigate("my_properties") }) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "Tus inmuebles",
                        tint = if (currentRoute == "my_properties") Color(0xFF2E7D32) else Color.Black
                    )
                }

                IconButton(onClick = { navController.navigate("real_map") }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar inmuebles",
                        tint = if (currentRoute == "search") Color(0xFF2E7D32) else Color.Black
                    )
                }

                IconButton(onClick = { navController.navigate("messages") }) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Mensajes",
                        tint = if (currentRoute == "messages") Color(0xFF2E7D32) else Color.Black
                    )
                }

                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        tint = if (currentRoute == "settings") Color(0xFF2E7D32) else Color.Black
                    )
                }
            }
        }
    }
}
