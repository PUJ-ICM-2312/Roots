package com.example.roots.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository
import com.example.roots.components.BottomNavBar
@Composable
fun FavoritesScreen(navController: NavController) {
    val favoritos = remember { mutableStateOf<List<Inmueble>>(emptyList()) }

    // Carga inicial de favoritos desde Firebase
    LaunchedEffect(Unit) {
        InmuebleRepository().getAll { list ->
            favoritos.value = list.filter { it.numFavoritos > 0 }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Tus Favoritos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            PropertyGrid(properties = favoritos.value, navController = navController)

            Spacer(Modifier.height(24.dp))
        }
    }
}
