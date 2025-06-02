package com.example.roots.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.ui.theme.RootsTheme
import com.example.roots.components.BottomNavBar
import com.example.roots.data.InmuebleRepository
import com.example.roots.screens.PropertyGrid


@Composable
fun FavoritesScreen(navController: NavController) {
    val favoritos = InmuebleRepository.`inmueble.kts`
        .filter { it.numFavoritos > 0 }
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

            // Reutilizamos la grilla de MyProperties
            PropertyGrid(properties = favoritos, navController = navController)

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFavorites() {
    RootsTheme {
        FavoritesScreen(navController = rememberNavController())
    }
}
