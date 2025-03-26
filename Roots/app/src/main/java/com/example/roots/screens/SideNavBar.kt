package com.example.roots.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SidebarNavigation(navController: NavController, screens: List<Screen>) {
    var isOpen by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        // Botón para abrir la barra lateral
        FloatingActionButton(
            onClick = { isOpen = !isOpen },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Text("☰", color = Color.White)
        }

        // Sidebar
        if (isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(Color.Gray.copy(alpha = 0.9f), RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    screens.forEach { screen ->
                        Text(
                            text = screen.route.replace("_", " ").uppercase(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(screen.route)
                                    isOpen = false
                                }
                                .padding(12.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
